/********************************************************************************
 * MIT Java Wordnet Interface (JWI)
 * Copyright (c) 2007-2008 Massachusetts Institute of Technology
 *
 * This is the non-commercial version of JWI.  This version may *not* be used
 * for commercial purposes.
 * 
 * This program and the accompanying materials are made available by the MIT
 * Technology Licensing Office under the terms of the MIT Java Wordnet Interface 
 * Non-Commercial License.  The MIT Technology Licensing Office can be reached 
 * at 617-253-6966 for further inquiry.
 *******************************************************************************/

package edu.mit.jwi.data;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.mit.jwi.data.parse.ILineParser;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IVersion;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Synset;

/**
 * A basic implementation of the {@code IDataProvider} interface for Wordnet
 * that uses files in the file system to back instances of
 * {@code IDataSource}. It takes a {@code URL} to a file system directory as
 * its path argument, and uses the hints from the
 * {@link IDataType#getResourceNameHints()} and
 * {@link POS#getResourceNameHints()} interfaces to examine the filenames in the
 * that directory to determine which files contain which data.
 * 
 * @author Mark A. Finlayson
 * @version 2.1.5, Nov. 16, 2007
 * @since 1.5.0
 */
public class FileProvider implements IDataProvider {

	public static final String PROTOCOL_FILE = "file";

	private URL fUrl = null;
	private IVersion fVersion = null;
	private Set<IContentType<?>> fSearchTypes = null;
	private Map<IContentType<?>, IDataSource<?>> fileMap = null;
	private Collection<IDataSource<?>> fSources = null;

	/**
	 * Constructs the file provider pointing to the resource indicated by the
	 * path.
	 * 
	 * @param url
	 *            A file URL in UTF-8 decodable format
	 */
	public FileProvider(URL url) {
		this(url, ContentType.values());
	}

	/**
	 * Allows specification of the content types that this file
	 * provider should load in the form of a an array. Duplicate content types
	 * will be ignored.
	 */
	public FileProvider(URL url, IContentType<?> ... types) {
		this(url, Arrays.asList(types));
	}

	/**
	 * Allows specification of the content types that this file
	 * provider should load in the form of a {@code Collection}. Duplicate
	 * content types will be ignored.
	 */
	public FileProvider(URL url, Collection<? extends IContentType<?>> types) {
		setSource(url);
		fSearchTypes = new HashSet<IContentType<?>>(types);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.data.IDataProvider#setSource(java.net.URL)
	 */
	public void setSource(URL url) {
		fUrl = url;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.data.IDataProvider#getSource()
	 */
	public URL getSource() {
		return fUrl;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.item.IHasVersion#getVersion()
	 */
	public IVersion getVersion() {
		return fVersion;
	}

	/**
	 * @see edu.mit.jwi.data.IDataProvider#open()
	 * @throws IOException
	 *             if the dictionary directory does not exist or the directory
	 *             is empty, or there is a problem with a file
	 */
	public void open() throws IOException {
		File directory = getDirectoryHandle();
		if (!directory.exists()) throw new IOException("Dictionary directory does not exist: " + directory);

		List<File> files = new ArrayList<File>();
		File[] filesInDir = directory.listFiles(new FileFilter(){
			public boolean accept(File file) {
				return file.isFile();
			}
		});

		if (filesInDir.length == 0) throw new IOException("No files found in " + directory);
		files.addAll(Arrays.asList(filesInDir));
		fileMap = new HashMap<IContentType<?>, IDataSource<?>>();
		
		File file;
		String name;
		IDataType<?> fileType;
		Set<String> typePatterns, posPatterns;
		for (IContentType<?> type : fSearchTypes) {
			fileType = type.getDataType();
			typePatterns = fileType.getResourceNameHints();
			posPatterns = type.getPOS() != null ? type.getPOS().getResourceNameHints() : Collections
					.<String> emptySet();

			for (Iterator<File> i = files.iterator(); i.hasNext();) {
				file = i.next();
				name = file.getName();
				if (containsOneOf(name, typePatterns) & containsOneOf(name, posPatterns)) {
					i.remove();
					fileMap.put(type, createDataSource(file, type));
					break;
				}
			}
		}
		
		fVersion = determineVersion();
		return;
	}
	
	/**
	 * Allows subclasses to change the data source implementation.
	 * 
	 * @throws FileNotFoundException
	 *             if the file is not found
	 * @see edu.mit.jwi.data.FileProvider#createDataSource(java.io.File,
	 *      edu.mit.jwi.data.IContentType)
	 */
	protected <T> IDataSource<T> createDataSource(File file, IContentType<T> type) throws IOException {
		if(type.getDataType() == DataType.DATA){
			
			IDataSource<T> src = new DirectAccessWordnetFile<T>(file, type);
			
			// check to see if direct access works with the file
			// often people will extract the files incorrectly on windows machines
			// and the binary files will be corrupted with extra CRs
			
			// get first line
			Iterator<String> itr = src.iterator();
			String firstLine = itr.next();
			if(firstLine == null) return src;
			
			// extract key
			ILineParser<T> parser = type.getDataType().getParser();
			ISynset s = (ISynset)parser.parseLine(firstLine);
			String key = Synset.zeroFillOffset(s.getOffset());
			
			// try to find line by direct access
			String soughtLine = src.getLine(key);
			if(soughtLine != null) return src;
			
			System.err.println(System.currentTimeMillis() + " - Error on direct access in " + type.getPOS().toString() + " data file: check CR/LF endings");
		}
		
		return new BinarySearchWordnetFile<T>(file, type);
	}
	
	protected IVersion determineVersion(){
		IVersion ver = null;
		for(IDataSource<?> dataSrc : fileMap.values()){
			
			// if no version to set, ignore
			if(dataSrc.getVersion() == null) continue;

			// init version
			if(ver == null){
				ver = dataSrc.getVersion();
				continue;
			} 
			
			// if version different from current
			if(!ver.equals(dataSrc.getVersion())) return null;
		}
		
		return ver;
	}

	protected void checkOpen() {
		if (!isOpen()) throw new DataProviderClosedException();
	}

	/**
	 * Translates the source URL into a java {@code File} object for access
	 * to the local filesystem. The URL must be in a UTF-8 compatible format as
	 * specified in {@link java.net.URLDecoder}
	 * 
	 * @return A {@code File} object pointing to the Wordnet dictionary data
	 *         directory.
	 * @throws NullPointerException
	 *             if url is <code>null</code>
	 * @throws IOException
	 *             if url does not use the 'file' protocol
	 */
	public File getDirectoryHandle() throws IOException {
		if (!fUrl.getProtocol().equals(PROTOCOL_FILE)) throw new IOException("URL source must use 'file' protocol");
		if (fUrl == null) throw new NullPointerException("Source not set (url=null)");
		try {
			return new File(URLDecoder.decode(fUrl.getPath(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Checks to see if one of the string patterns specified in the set of
	 * strings is found in the specified target string. If the pattern set is
	 * empty or null, returns <code>true</code>. If a pattern is found in the
	 * target string, returns <code>true</code>. Otherwise, returns
	 * <code>false</code>.
	 */
	protected boolean containsOneOf(String target, Set<String> patterns) {
		if (patterns == null || patterns.size() == 0) return true;
		for (String pattern : patterns) {
			if (target.indexOf(pattern) > -1) return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.data.IDataProvider#close()
	 */
	public void close() {
		fileMap = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.data.IDataProvider#isOpen()
	 */
	public boolean isOpen() {
		return fileMap != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jwi.data.IDataProvider#getFile(edu.mit.jwi.content.IContentType)
	 */
	@SuppressWarnings("unchecked") // no way to safely cast; must rely on registerSource method to assure compliance
	public <T> IDataSource<T> getSource(IContentType<T> type) {
		checkOpen();
		return (IDataSource<T>)fileMap.get(type);
	}

	/** 
	 * This method is marked {@code final} to ensure that no subclasses
	 * can intervene and registered non-matching parameterized types into
	 * the fileMap.
	 * @param <T>
	 * @param file
	 * @param type
	 */
	protected final <T> void registerSource(IContentType<T> type, IDataSource<T> file) {
		checkOpen();
		fileMap.put(type, file);
	}

	public Collection<IDataSource<?>> getSources() {
		checkOpen();
		if(fSources == null) fSources = Collections.unmodifiableCollection(fileMap.values());
		return fSources;
	}


}