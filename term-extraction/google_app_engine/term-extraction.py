import cgi
import sys
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
sys.path.insert(0, 'zope.zip')
from topia.termextract import extract
from django.utils import simplejson as json
import re

class MainPage(webapp.RequestHandler):
  def get(self):
    self.redirect("http://fivefilters.org/term-extraction/", permanent=True)
    #self.response.out.write("""
      #<html>
        #<body>
          #<form action="/terms" method="post">
            #<div>content<br /><textarea name="content" rows="6" cols="60"></textarea></div>
			#<div>callback<br /><input type="text" name="callback" /></div>
            #<div><input type="submit" value="Get Terms"></div>
          #</form>
        #</body>
      #</html>""")


class Terms(webapp.RequestHandler):
  def post(self):
    self.response.headers['Content-Type'] = 'text/plain'
    #self.response.out.write('<html><body>You wrote:<pre>')
    #self.response.out.write(cgi.escape(self.request.get('content')))
    extractor = extract.TermExtractor()
    extractor.filter = extract.DefaultFilter(singleStrengthMinOccur=1)
    def term_compare(x, y):
        if y[1]+y[2]*2 > x[1]+x[2]*2:
            return 1
        elif y[1]==x[1] and y[2]==x[2]:
            return 0
        else: # x<y
            return -1
    content = self.request.get('content').lower()
    content = content.replace(u"\u201c", '"').replace(u"\u201d", '"').replace(u"\u2018", "'").replace(u"\u2019", "'").replace(u"\u2026", "")
    list = sorted(extractor(content), term_compare)
    list = list[:50]
    for i in range(len(list)-1, -1, -1):
        if len(list[i][0]) == 1 or list[i][2] > 2 or (list[i][0].find("http") >= 0) or not re.search('[a-z]', list[i][0]) or re.search('[0-9]', list[i][0]):
            list.remove(list[i])
        else:
            list[i] = list[i][0]
    callback = self.request.get('callback')
    if re.match('^[a-zA-Z0-9._\[\]]+$', callback):
        self.response.out.write(callback + '(' + json.dumps(list, indent=4) + ')')
    else:
        self.response.out.write(json.dumps(list, indent=4))
    #self.response.out.write(list)
    #self.response.out.write('</pre></body></html>')

application = webapp.WSGIApplication(
                                     [('/', MainPage),
                                      ('/terms', Terms)],
                                     debug=True)

def main():
  run_wsgi_app(application)

if __name__ == "__main__":
  main()