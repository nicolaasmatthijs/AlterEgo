/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.profile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import org.cl.nm417.AlterEgo;
import org.cl.nm417.config.ConfigLoader;
import org.cl.nm417.util.WebFile;
import org.cl.nm417.xmlparser.DataParser;

/**
 * Script that is run during the online interleaved evaluation. It will continuously
 * update and re-generate the user profiles
 */
public class ProfileGenerator {

	private Timer timer = new Timer();
	
	public void doRun(){
	    timer.schedule(new RemindTask(), 1000);
	}
	
	private class RemindTask extends TimerTask {
		/**
		 * Function which will fetch newly visited pages for all users, will add them to the 
		 * user XML and will parse the new text. Once this is done, this is restarted after
		 * 60 seconds
		 */
	    public void run() {

	    	try {
	    	
		    	///////////////////////
		    	// Get list of users //
		    	///////////////////////
	    		
	    		WebFile file = new WebFile(ConfigLoader.getConfig().getProperty("listusers"));
		    	System.out.println(file.getContent());
				String userss = (String) file.getContent();
		    	String[] users = userss.split(";");
		    	
		    	for (String user: users){
		    		
		    		AlterEgo.config.put("ccparse", false);
		    		
		    		AlterEgo.config.put("user", user);
		    		AlterEgo.config.put("ccfile", ConfigLoader.getConfig().getProperty("tmpdir"));
		    		
		    		if (user.length() > 0){
		    		
			    		///////////////////////////
				    	// Get last processed id //
				    	///////////////////////////
			    		
		    			int lastId = 0;
		    			
			    		// If file exists
			    		File f = new File(ConfigLoader.getConfig().getProperty("xmlfiles") + user + ".xml");
			    		if (f.exists()){
			    			DataParser data = new DataParser(user, false);
			    			if (data.getDocuments().size() > 0){
			    				lastId = data.getDocuments().get(data.getDocuments().size() - 1).getId();
			    			}
			    		}
		    		
			    		/////////////////////////////////////
			    		// Generate XML file for that user //
			    		/////////////////////////////////////
			    		
			    		try {
			                Runtime rt = Runtime.getRuntime();
			                Process pr = rt.exec(ConfigLoader.getConfig().getProperty("generatescr") + " " + user + " " + lastId);
			 
			                InputStream is = pr.getErrorStream();
			                InputStreamReader isr = new InputStreamReader(is);
			                BufferedReader br = new BufferedReader(isr);
			                
			                String line;
			                while ((line = br.readLine()) != null) {  
			                  System.out.println(line);  // Prints the error lines
			                }

			                int exitVal = pr.waitFor();   

			                System.out.println("Exited with exit code "+exitVal);
			 
			            } catch(Exception e) {
			                System.out.println(e.toString());
			                e.printStackTrace();
			            }
			            
			            /////////////////////////////////////
			    		// Generate XML file for that user //
			    		/////////////////////////////////////
			            
			            
			            AlterEgo.config.put("ccparse", true);
			            AlterEgo.config.put("writecc", true);
			            DataParser data = new DataParser(ConfigLoader.getConfig().getProperty("tmpdir"), true);
		    		
			            ///////////////////////////////////
			    		// Append CCParse 2 to CCParse 1 //
			    		///////////////////////////////////
			           
			            File ccp2 =  new File(ConfigLoader.getConfig().getProperty("tmpdir") + "cc.parsed.txt");
			            File ccp1 =  new File(ConfigLoader.getConfig().getProperty("profiles") + user + ".cc.parsed.txt");
			            
			            if (ccp1.exists()){
			            	BufferedWriter out = new BufferedWriter(new FileWriter(ccp1, true)); 
			            	FileInputStream fstream = new FileInputStream(ccp2);
			                DataInputStream in = new DataInputStream(fstream);
			                BufferedReader br = new BufferedReader(new InputStreamReader(in));
			                String strLine;
			                //Read File Line By Line
			                while ((strLine = br.readLine()) != null)   {
			                  // Print the content on the console
			                	out.write(strLine + "\n");
			                }
			                //Close the input stream
			                in.close();	
			            	out.close();
			            } else {
			            	ccp2.renameTo(new File(ConfigLoader.getConfig().getProperty("profiles") + user + ".cc.parsed.txt"));
			            }
			            
			            ///////////////////////
			            // Append CC2 to CC1 //
			            ///////////////////////
			            
			            File cc2 =  new File(ConfigLoader.getConfig().getProperty("tmpdir") + "cc.txt");
			            File cc1 =  new File(ConfigLoader.getConfig().getProperty("profiles") + user + ".cc.txt");
			               
			            if (cc1.exists()){
			            	BufferedWriter out = new BufferedWriter(new FileWriter(cc1, true)); 
			            	FileInputStream fstream = new FileInputStream(cc2);
			                DataInputStream in = new DataInputStream(fstream);
			                BufferedReader br = new BufferedReader(new InputStreamReader(in));
			                String strLine;
			                //Read File Line By Line
			                while ((strLine = br.readLine()) != null)   {
			                  // Print the content on the console
			                	out.write(strLine + "\n");
			                }
			                //Close the input stream
			                in.close();	
			            	out.close();
			            } else {
			            	cc2.renameTo(new File(ConfigLoader.getConfig().getProperty("profiles") + user + ".cc.txt"));
					    }
			            
			            /////////////////////////
			    		// Append XML2 to XML1 //
			    		/////////////////////////
			            
			            File xml1 = new File(ConfigLoader.getConfig().getProperty("xmlfiles") + user + ".xml");
			            File xml2 = new File(ConfigLoader.getConfig().getProperty("tmpdir") + "tmp2.xml");	            
			            
			            if (xml1.exists()){
			            	// Move 
			            	xml1.renameTo(new File(ConfigLoader.getConfig().getProperty("xmlfiles") + user + ".tmp.xml"));
			            	
			            	// Remove last line
			            	BufferedWriter out = new BufferedWriter(new FileWriter(ConfigLoader.getConfig().getProperty("xmlfiles") + user + ".xml", true)); 
			            	FileInputStream fstream = new FileInputStream(ConfigLoader.getConfig().getProperty("xmlfiles") + user + ".tmp.xml");
			                DataInputStream in = new DataInputStream(fstream);
			                BufferedReader br = new BufferedReader(new InputStreamReader(in));
			                String strLine;
			                //Read File Line By Line
			                while ((strLine = br.readLine()) != null)   {
			                  // Print the content on the console
			                	if (!strLine.trim().equals("</documents>")){
			                		out.write(strLine + "\n");
			                	}
			                }
			                //Close the input stream
			                in.close();	
			            	
			            	fstream = new FileInputStream(xml2);
			                in = new DataInputStream(fstream);
			                br = new BufferedReader(new InputStreamReader(in));
			                //Read File Line By Line
			                int prog = 0;
			                while ((strLine = br.readLine()) != null)   {
			                  // Print the content on the console
			                	if (prog >= 2){
			                		out.write(strLine + "\n");
			                	}
			                	prog++;
			                }
			                //Close the input stream
			                in.close();	
			            	out.close(); 
			            	
			            	// Remove the tmp file & xml2 file
			            	new File(ConfigLoader.getConfig().getProperty("xmlfiles") + user + ".tmp.xml").delete();
			            
			            } else {
			            	new File(ConfigLoader.getConfig().getProperty("profiles") + user).mkdir();
			            	xml2.renameTo(new File(ConfigLoader.getConfig().getProperty("xmlfiles") + user + ".xml"));
			            }
			            
			            ccp2.delete();
			            cc2.delete();
		            	xml2.delete();
		            	
		            	////////////////////////////////////////
		            	// Generate the 3 necessairy profiles //
			            ////////////////////////////////////////
		            	
		            	AlterEgo.config.put("writecc", false);
		            	AlterEgo.config.put("user", user);
		        		AlterEgo.config.put("ccparse", true);
		        		data = AlterEgo.getDataParser(user);
		        		
		        		AlterEgo.config.put("user", user); AlterEgo.config.put("split", true); AlterEgo.config.put("excludeDuplicate", false); 
		        		AlterEgo.config.put("googlengramW",1000); AlterEgo.config.put("titleW",1); AlterEgo.config.put("metadescriptionW",1); 
		        		AlterEgo.config.put("metakeywordsW",1); AlterEgo.config.put("plaintextW",1); AlterEgo.config.put("termsW",1); AlterEgo.config.put("ccparseW",1);
		        		AlterEgo.config.put("takeLog",false); AlterEgo.config.put("posVerb", false); AlterEgo.config.put("posAdjective", false); 
		        		AlterEgo.config.put("posAdverb", false);
		        		
		        		AlterEgo.config.put("ngrams",UserProfile.getGoogleNGrams(data));
		        		
		        		AlterEgo.config.put("posAll", true);
						AlterEgo.config.put("posNoun", false);
						AlterEgo.config.put("googlengram", false);
						AlterEgo.config.put("dict", null);
		        		
		        		// Collect statistics
						UserProfile.extractStatistics(data, true, true, true, true, true, true);
						AlterEgo.config.put("statistics", true);
						
		        			///////////////
		        			// Profile 1 //
		        			///////////////
		        		
		        			AlterEgo.config.put("weighting","tfidf");
		        			
							AlterEgo.config.put("useRelativeW", true);
							AlterEgo.config.put("title", true);
							AlterEgo.config.put("metadescription", false);
							AlterEgo.config.put("metakeywords", true);
							AlterEgo.config.put("plaintext", false);
							AlterEgo.config.put("terms", false);
							AlterEgo.config.put("ccparse", true);
							
							doActualGenerate(user, data);
		        		
		        			///////////////
	        				// Profile 2 //
	        				///////////////
							
							AlterEgo.config.put("weighting","tf");
		        			
							AlterEgo.config.put("useRelativeW", true);
							AlterEgo.config.put("title", false);
							AlterEgo.config.put("metadescription", false);
							AlterEgo.config.put("metakeywords", false);
							AlterEgo.config.put("plaintext", false);
							AlterEgo.config.put("terms", true);
							AlterEgo.config.put("ccparse", true);
							
							doActualGenerate(user, data);
		        		
		        			///////////////
	        				// Profile 3 //
	        				///////////////
							
							AlterEgo.config.put("weighting","bm25");
		        			
							AlterEgo.config.put("useRelativeW", true);
							AlterEgo.config.put("title", true);
							AlterEgo.config.put("metadescription", false);
							AlterEgo.config.put("metakeywords", true);
							AlterEgo.config.put("plaintext", false);
							AlterEgo.config.put("terms", true);
							AlterEgo.config.put("ccparse", false);
							
							doActualGenerate(user, data);
		            	
		            	/////////////////////////////////////
		            	// Move profiles to profile folder //
		            	/////////////////////////////////////
		            	
						File profiledir = new File(ConfigLoader.getConfig().getProperty("finprofiles") + user);
						if (!profiledir.exists()){
							profiledir.mkdir();
						}
						
						File profile1 = new File(ConfigLoader.getConfig().getProperty("profiles") + user + "/" + user + "_ti_r_n_r_n_n_r_n_n.txt");
						profile1.renameTo(new File(ConfigLoader.getConfig().getProperty("finprofiles") + user + "/" + user + "_ti_r_n_r_n_n_r_n_n.txt"));
						
						File profile2 = new File(ConfigLoader.getConfig().getProperty("profiles") + user + "/" + user + "_t_n_n_n_n_r_r_n_n.txt");
						profile2.renameTo(new File(ConfigLoader.getConfig().getProperty("finprofiles") + user + "/" + user + "_t_n_n_n_n_r_r_n_n.txt"));
							
						File profile3 = new File(ConfigLoader.getConfig().getProperty("profiles") + user + "/" + user + "_b_r_n_r_n_r_n_n_n.txt");
						profile3.renameTo(new File(ConfigLoader.getConfig().getProperty("finprofiles") + user + "/" + user + "_b_r_n_r_n_r_n_n_n.txt"));
						
						File urlfile = new File(ConfigLoader.getConfig().getProperty("profiles") + user + ".url.txt");
						urlfile.renameTo(new File(ConfigLoader.getConfig().getProperty("finprofiles") + user + ".url.txt"));
		    		
		    		}
		    		
		    	}
		    	
		    	AlterEgo.config.put("user", null);
	    		AlterEgo.config.put("ccfile", null);
	    		
		    	//timer.cancel();
	    		System.out.println("Finishing and pauzing ...");
		    	timer.schedule(new RemindTask(), 60000);
	    	
	    	} catch (Exception ex){
	    		ex.printStackTrace();
	    	}

	    }
	  }

	/**
	 * Generate profile given parameter configuration
	 * @param user	User's unique identifier
	 * @param data	User's browsing history
	 */
	private static void doActualGenerate(String user, DataParser data){
		//Calculate filename
		String extention = user;
		
		// Weighting
		String weighting = (String)AlterEgo.config.get("weighting");
		if (weighting.equals("tf")){
			extention += "_t";
		} else if (weighting.equals("tfidf")){
			extention += "_ti";
		} else if (weighting.equals("bm25")){
			extention += "_b";
		}
		
		// Title
		boolean useRelative = (Boolean)AlterEgo.config.get("useRelativeW");
		boolean title = (Boolean)AlterEgo.config.get("title");
		if (title && useRelative){
			extention += "_r";
		} else if (title){
			extention += "_y";
		} else {
			extention += "_n";
		}
		
		// Meta description
		boolean md = (Boolean)AlterEgo.config.get("metadescription");
		if (md && useRelative){
			extention += "_r";
		} else if (md){
			extention += "_y";
		} else {
			extention += "_n";
		}
		
		// Meta keywords
		boolean mk = (Boolean)AlterEgo.config.get("metakeywords");
		if (mk && useRelative){
			extention += "_r";
		} else if (mk){
			extention += "_y";
		} else {
			extention += "_n";
		}
		
		// Plain text
		boolean pt = (Boolean)AlterEgo.config.get("plaintext");
		if (pt && useRelative){
			extention += "_r";
		} else if (pt){
			extention += "_y";
		} else {
			extention += "_n";
		}
		
		// Terms
		boolean t = (Boolean)AlterEgo.config.get("terms");
		if (t && useRelative){
			extention += "_r";
		} else if (t){
			extention += "_y";
		} else {
			extention += "_n";
		}
		
		// C&C Parsed
		boolean cc = (Boolean)AlterEgo.config.get("ccparse");
		if (cc && useRelative){
			extention += "_r";
		} else if (cc){
			extention += "_y";
		} else {
			extention += "_n";
		}
		
		// Filtering
		boolean allPos = (Boolean)AlterEgo.config.get("posAll");
		boolean nGram = (Boolean)AlterEgo.config.get("googlengram");
		boolean posNoun = (Boolean)AlterEgo.config.get("posNoun");
		if (nGram){
			extention += "_g";
		} else if (posNoun){
			extention += "_wn";
		} else if (allPos){
			extention += "_n";
		}
		
		// Exclude duplicate pages
		boolean excludeDuplicate = (Boolean)AlterEgo.config.get("excludeDuplicate");
		if (excludeDuplicate){
			extention += "_y";
		} else {
			extention += "_n";
		}
		
		//If not, create the profile
		AlterEgo.generateProfile(AlterEgo.config, data);
		
	}
	
}
