import sys
#sys.path.insert(0, 'topia.zip')
#sys.path.insert(0, 'setuptools.zip')
sys.path.insert(0, 'zope.zip')
from topia.termextract import extract
extractor = extract.TermExtractor()
extractor.filter = extract.permissiveFilter
print 'Content-Type: text/plain'
print ''
print 'Hello, goodbye2!'
print sys.path
print extractor("The fox can't jump over the fox's tail.")