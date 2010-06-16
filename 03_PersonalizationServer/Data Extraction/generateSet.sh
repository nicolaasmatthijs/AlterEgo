##############################################################################
#
# Copyright (c) 2010 Nicolaas Matthijs (nm417)
# All Rights Reserved.
#
##############################################################################

# Script which will get the newly visited web pages for a user out of the database. 
# It then extracts the necessairy data out of the HTML and creates an XML record 
# for each page. These XML records are added to the user's XML file and the text 
# is prepared for parsing
		
echo ">>>>>> Processing $user >>>>>>>" > /Users/nicolaas/Desktop/AlterEgo/dataprocessing/log.txt
echo "" >> /Users/nicolaas/Desktop/AlterEgo/dataprocessing/log.txt
		
echo "<?xml version=\"1.0\"?>" > /Users/nicolaas/Desktop/AlterEgo/dataprocessing/tmp.xml
echo "<documents>" >> /Users/nicolaas/Desktop/AlterEgo/dataprocessing/tmp.xml
		
curl "http://alterego.caret.cam.ac.uk/processing/generateSetsId.php?uuid=$1&id=$2" > /Users/nicolaas/Desktop/AlterEgo/dataprocessing/tmp.txt
		
while read eid
do

	echo "---- Processing ${eid}" > /Users/nicolaas/Desktop/AlterEgo/dataprocessing/log.txt
	curl "http://alterego.caret.cam.ac.uk/processing/generateSets.php?uuid=$1&id=${eid}" >> /Users/nicolaas/Desktop/AlterEgo/dataprocessing/tmp.xml
		
done < /Users/nicolaas/Desktop/AlterEgo/dataprocessing/tmp.txt
		
echo "</documents>" >> /Users/nicolaas/Desktop/AlterEgo/dataprocessing/tmp.xml
		
/usr/bin/python /Users/nicolaas/Desktop/AlterEgo/dataprocessing/beautify.py /Users/nicolaas/Desktop/AlterEgo/dataprocessing/tmp.xml > /Users/nicolaas/Desktop/AlterEgo/dataprocessing/tmp2.xml
rm /Users/nicolaas/Desktop/AlterEgo/dataprocessing/tmp.txt
rm /Users/nicolaas/Desktop/AlterEgo/dataprocessing/tmp.xml
rm /Users/nicolaas/Desktop/AlterEgo/dataprocessing/cc.txt
			
echo "" > /Users/nicolaas/Desktop/AlterEgo/dataprocessing/log.txt
echo ">>>>>> Processed $user >>>>>>>" > /Users/nicolaas/Desktop/AlterEgo/dataprocessing/log.txt