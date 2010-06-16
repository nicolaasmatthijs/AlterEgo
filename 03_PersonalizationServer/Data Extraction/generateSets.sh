##############################################################################
#
# Copyright (c) 2010 Nicolaas Matthijs (nm417)
# All Rights Reserved.
#
##############################################################################

# Script that will get a list of all unprocessed web page ids and will process
# all of these one by one

###########################
## Generate the datasets ##
###########################

while read set
do

	echo "***************************************"
	echo "**       Starting $set set       **"
	echo "***************************************"
	echo ""

	while read user
	do

		echo ">>>>>> Processing $user >>>>>>>"
		echo ""
		
		echo "<?xml version=\"1.0\"?>" > tmp.xml
		echo "<documents>" >> tmp.xml
		
		curl http://alterego.caret.cam.ac.uk/processing/generateSetsId.php?uuid=${user} > tmp.txt
		
		while read eid
		do

			echo "---- Processing ${eid}"
			curl "http://alterego.caret.cam.ac.uk/processing/generateSets.php?uuid=${user}&id=${eid}" >> tmp.xml
		
		done < tmp.txt
		
		echo "</documents>" >> tmp.xml
		
		/usr/bin/python beautify.py tmp.xml > data/${set}/${user}.xml
		rm tmp.txt
		rm tmp.xml
			
		echo ""
		echo ">>>>>> Processed $user >>>>>>>"

	done < data/${set}/${set}.txt
	
	echo ""
	echo "***************************************"
	echo "**       Finished $set set       **"
	echo "***************************************"

done < sets.txt