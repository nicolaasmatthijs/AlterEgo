/*
 * Copyright (c) 2010 Nicolaas Matthijs (nm417)
 * All Rights Reserved.
 */
package org.cl.nm417.results;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.cl.nm417.config.ConfigLoader;

/**
 * Stand-alone class used to calculate the number of times re-ranking happened
 * at all possible ranks for all investigated personalization strategies
 * 
 * The log file on which this is calculated has the following format:
 * <!--
 *	Time: Mon May 31 18:31:43 BST 2010
 *	User: usr_5945189
 *	Query: area+of+portion+of+circle
 *	Page: 1
 *	Profile: usr_5945189_b_r_n_r_n_r_n_n_n.txt
 *	Random 1: 0
 *	Random 2: 0
 *	Random 3: 0
 *	Random 4: 0
 *	Random 5: 0
 *	Random 6: 0
 *	Random 7: 1
 *	Random 8: 1
 *	Random 9: 1
 *	Random 10: 0
 *	Random 11: 1
 *	Random 12: 1
 *	Random 13: 1
 *	Random 14: 0
 *	Random 15: 0
 *	Random 16: 0
 *	Random 17: 1
 *	Random 18: 0
 *	Random 19: 0
 *	Random 20: 0
 *	Random 21: 0
 *	Random 22: 0
 *	Random 23: 1
 *	Random 24: 1
 *	Random 25: 0
 *	Random 26: 1
 *	Random 27: 0
 *	Random 28: 1
 *	Google Ranking:
 *		1. Math Forum: Ask Dr. Math FAQ: Circle Formulas (http://mathforum.org/dr/math/faq/formulas/faq.circle.html)
 *		2. Circular segment - Wikipedia, the free encyclopedia (http://en.wikipedia.org/wiki/Circular_segment)
 *		3. Area of Sectors and Segments of Circles (http://regentsprep.org/Regents/math/geometry/GP16/CircleSectors.htm)
 *		4. Circular Segment -- from Wolfram MathWorld (http://mathworld.wolfram.com/CircularSegment.html)
 *		5. The Area of Part of a Circle (http://www.mathreference.com/ca-int,carea2.html)
 *		6. Area of a circle segment - Math Open Reference (http://www.mathopenref.com/segmentarea.html)
 *		7. Area Of Portion Of Circle | TutorNext.com (http://www.tutornext.com/help/area-of-portion-of-circle)
 *		8. Cool math .com - The Geometry of Circles - Radius, Diameter ... (http://www.coolmath.com/reference/circles-geometry.html)
 *		9. Geometry: Arc Length and Sectors - CliffsNotes (http://www.cliffsnotes.com/study_guide/Arc-Length-and-Sectors.topicArticleId-18851,articleId-18829.html)
 *		10. How to Calculate the Area of a Portion of a Circle | eHow.com (http://www.ehow.com/how_5957595_calculate-area-portion-circle.html)
 *		11. what portion of the whole circle does each equal part cover as ... (http://www.tutorvista.com/q/what-portion-of-the-whole-circle-does-each-equal-part-cover-as-shown/q16911_86838)
 *		12. xkcd ¥ View topic - Area of Part of a Circle (http://forums.xkcd.com/viewtopic.php%3Ff%3D17%26t%3D57341)
 *		13. Circular segment: Definition from Answers.com (http://www.answers.com/topic/circular-segment)
 *		14. area of circle (http://www.docstoc.com/docs/10610674/area-of-circle)
 *		15. Arc Length and Surface Area (http://www.ugrad.math.ubc.ca/coursedoc/math101/notes/moreApps/arclength.html)
 *		16. San Jose Math Circle February 7, 2009 CIRCLES AND FUNKY AREAS ... (http://sanjosemathcircle.org/handouts/2008-2009/20090207.pdf)
 *		17. San Jose Math Circle February 7, 2009 CIRCLES AND FUNKY AREAS ... (http://sanjosemathcircle.org/handouts/2008-2009/20090207.pdf)
 *		18. Great Circle Mapper FAQ (http://gc.kls2.com/faq.html)
 *		19. Circle of Willis - Wikipedia, the free encyclopedia (http://en.wikipedia.org/wiki/Circle_of_Willis)
 *		20. circumference of a circle (http://math.about.com/library/blcircle.htm)
 *		21. How to Calculate the Area of a Circle - wikiHow (http://www.wikihow.com/Calculate-the-Area-of-a-Circle)
 *		22. How to Calculate the Area of a Circle | eHow.com (http://www.ehow.com/how_2384369_calculate-area-circle.html)
 *		23. Area (http://www.staff.vu.edu.au/mcaonline/units/measure/meaarea.html)
 *		24. SOLUTION: Find the area of the shaded portion of the circle. Use ... (http://www.algebra.com/algebra/homework/Volume/Volume.faq.question.204056.html)
 *		25. Wapedia - Wiki: Circular segment (http://wapedia.mobi/en/Circular_segment)
 *		26. Cross-Country Flying Part Three: Thermalling Technique (http://www.expandingknowledge.com/Jerome/PG/Skill/XC/WillGadd_Thermals/Part_3.htm)
 *		27. Volume of a Cylindrical Tank - Math Forum - Ask Dr. Math (http://mathforum.org/library/drmath/view/53945.html)
 *		28. Circles - GMAT Math Study Guide (http://www.platinumgmat.com/gmat_study_guide/circles)
 *		29. Arc of a circle, minor arc, major arc, and central angle. (http://www.mathwarehouse.com/geometry/circle/arc-of-circle.php)
 *		30. How To Find The Centre Of A Circle From A Part Of The Circumference (http://chestofbooks.com/crafts/metal/Builder-Mechanic/How-To-Find-The-Centre-Of-A-Circle-From-A-Part-Of-The-Circumference.html)
 *		31. Word Problems: Area and Perimeter of Circles (http://www.algebralab.org/Word/Word.aspx%3Ffile%3DGeometry_AreaPerimeterCircles.xml)
 *		32. How to Calculate the Area of a Circle: Geometry Tips | eHow.co.uk (http://www.ehow.co.uk/video_4754338_calculate-area-circle.html)
 *		33. Neolithic Revolutions: Archaeological Patterning across Southern ... (http://neolithic-revolutions.blogspot.com/2010/01/archaeological-patterning-across.html)
 *		34. Perimeters and Areas (http://www.andrews.edu/~calkins/math/webtexts/geom08.htm)
 *		35. Twelve Mile Circle È The Twelve Mile Circle, Part II - maps ... (http://www.howderfamily.com/blog/%3Fp%3D497)
 *		36. What is the way to calculate the remaining area of any circle that ... (http://www.answerbag.com/q_view/895740)
 *		37. WikiAnswers - A portion of the area of a circle (http://wiki.answers.com/Q/A_portion_of_the_area_of_a_circle)
 *		38. 11th Street bridges, part 1: The plan - Greater Greater Washington (http://greatergreaterwashington.org/post.cgi%3Fid%3D1676)
 *		39. Measurement Session 7: Circles and pi (http://www.learner.org/courses/learningmath/measurement/session7/part_b/examining.html)
 *		40. Raccoon Circles -- A Handbook for Facilitators Book II (http://sitemaker.umich.edu/adventuretherapy/files/raccooncircles_2.pdf)
 *		41. Practice with Sectors and Segments of Circles (http://regentsprep.org/Regents/math/geometry/GP16/PracCircleSectorsSegments.htm)
 *		42. Ask NRICH: Area common to two circles (https://nrich.maths.org/discus/messages/114352/114289.html%3F1161628588)
 *		43. Circle definition and properties- Math Open Reference (http://www.mathopenref.com/circle.html)
 *		44. Green Circle Trail: Frequently Asked Questions (http://greencircletrail.org/faqs.aspx)
 *		45. name the shaded part of the circle as a fraction | TutorVista | 22228 (http://www.tutorvista.com/q/name-the-shaded-part-of-the-circle-as-a-fraction/q22228_86839)
 *		46. HGIC 1801 Landscape Irrigation Equipment Part 1: Sprinklers ... (http://www.clemson.edu/extension/hgic/plants/other/irrigation/hgic1810.html)
 *		47. MATHS TEST --- THE CIRCLE (http://www.seomraranga.com/maths/The%2520Circle.doc)
 *		48. Humanity On The Pollen Path - Part Five (http://www.greatdreams.com/plpath5.htm)
 *		49. Anatomy of the Brain: Stroke Center at University Hospital, Newark ... (http://www.theuniversityhospital.com/stroke/anatomy.htm)
 *		50. Centennial Fire District (http://www.centennialfire.org/)
 *		51. BUL320/AE084: Lawn Sprinkler Selection and Layout for Uniform ... (http://edis.ifas.ufl.edu/ae084)
 *		52. Trigonometry: Radians - CliffsNotes (http://www.cliffsnotes.com/study_guide/Radians.topicArticleId-11658,articleId-11589.html)
 *		53. PI.html (http://www.hitchins.net/PI.html)
 *		54. Circle O Trail (http://www.utahmountainbiking.com/trails/circle-o.htm)
 *		55. Early Fraction Ideas with Models: 2.5 - Part 2 - Number ... (http://www.education.vic.gov.au/studentlearning/teachingresources/maths/mathscontinuum/number/N25001P_2.htm)
 *		56. Intermediate Test Prep Math 8(Grade 6) Area of a sector of a circle (http://www.studyzone.org/mtestprep/math8/e/circlesector6p.cfm)
 *	Reranked Ranking:
 *		1. Circular segment - Wikipedia, the free encyclopedia (http://en.wikipedia.org/wiki/Circular_segment)
 *		2. Math Forum: Ask Dr. Math FAQ: Circle Formulas (http://mathforum.org/dr/math/faq/formulas/faq.circle.html)
 *		3. Area of Sectors and Segments of Circles (http://regentsprep.org/Regents/math/geometry/GP16/CircleSectors.htm)
 *		4. The Area of Part of a Circle (http://www.mathreference.com/ca-int,carea2.html)
 *		5. Area of a circle segment - Math Open Reference (http://www.mathopenref.com/segmentarea.html)
 *		6. Area Of Portion Of Circle | TutorNext.com (http://www.tutornext.com/help/area-of-portion-of-circle)
 *		7. Circular Segment -- from Wolfram MathWorld (http://mathworld.wolfram.com/CircularSegment.html)
 *		8. what portion of the whole circle does each equal part cover as ... (http://www.tutorvista.com/q/what-portion-of-the-whole-circle-does-each-equal-part-cover-as-shown/q16911_86838)
 *		9. xkcd ¥ View topic - Area of Part of a Circle (http://forums.xkcd.com/viewtopic.php%3Ff%3D17%26t%3D57341)
 *		10. Cool math .com - The Geometry of Circles - Radius, Diameter ... (http://www.coolmath.com/reference/circles-geometry.html)
 *		11. Circular segment: Definition from Answers.com (http://www.answers.com/topic/circular-segment)
 *		12. How to Calculate the Area of a Portion of a Circle | eHow.com (http://www.ehow.com/how_5957595_calculate-area-portion-circle.html)
 *		13. SOLUTION: Find the area of the shaded portion of the circle. Use ... (http://www.algebra.com/algebra/homework/Volume/Volume.faq.question.204056.html)
 *		14. Circle of Willis - Wikipedia, the free encyclopedia (http://en.wikipedia.org/wiki/Circle_of_Willis)
 *		15. Volume of a Cylindrical Tank - Math Forum - Ask Dr. Math (http://mathforum.org/library/drmath/view/53945.html)
 *		16. Area (http://www.staff.vu.edu.au/mcaonline/units/measure/meaarea.html)
 *		17. How to Calculate the Area of a Circle - wikiHow (http://www.wikihow.com/Calculate-the-Area-of-a-Circle)
 *		18. circumference of a circle (http://math.about.com/library/blcircle.htm)
 *		19. San Jose Math Circle February 7, 2009 CIRCLES AND FUNKY AREAS ... (http://sanjosemathcircle.org/handouts/2008-2009/20090207.pdf)
 *		20. How to Calculate the Area of a Circle | eHow.com (http://www.ehow.com/how_2384369_calculate-area-circle.html)
 *		21. San Jose Math Circle February 7, 2009 CIRCLES AND FUNKY AREAS ... (http://sanjosemathcircle.org/handouts/2008-2009/20090207.pdf)
 *		22. Geometry: Arc Length and Sectors - CliffsNotes (http://www.cliffsnotes.com/study_guide/Arc-Length-and-Sectors.topicArticleId-18851,articleId-18829.html)
 *		23. Cross-Country Flying Part Three: Thermalling Technique (http://www.expandingknowledge.com/Jerome/PG/Skill/XC/WillGadd_Thermals/Part_3.htm)
 *		24. Great Circle Mapper FAQ (http://gc.kls2.com/faq.html)
 *		25. Arc Length and Surface Area (http://www.ugrad.math.ubc.ca/coursedoc/math101/notes/moreApps/arclength.html)
 *		26. Wapedia - Wiki: Circular segment (http://wapedia.mobi/en/Circular_segment)
 *		27. Green Circle Trail: Frequently Asked Questions (http://greencircletrail.org/faqs.aspx)
 *		28. What is the way to calculate the remaining area of any circle that ... (http://www.answerbag.com/q_view/895740)
 *		29. area of circle (http://www.docstoc.com/docs/10610674/area-of-circle)
 *		30. How To Find The Centre Of A Circle From A Part Of The Circumference (http://chestofbooks.com/crafts/metal/Builder-Mechanic/How-To-Find-The-Centre-Of-A-Circle-From-A-Part-Of-The-Circumference.html)
 *		31. Raccoon Circles -- A Handbook for Facilitators Book II (http://sitemaker.umich.edu/adventuretherapy/files/raccooncircles_2.pdf)
 *		32. Ask NRICH: Area common to two circles (https://nrich.maths.org/discus/messages/114352/114289.html%3F1161628588)
 *		33. How to Calculate the Area of a Circle: Geometry Tips | eHow.co.uk (http://www.ehow.co.uk/video_4754338_calculate-area-circle.html)
 *		34. WikiAnswers - A portion of the area of a circle (http://wiki.answers.com/Q/A_portion_of_the_area_of_a_circle)
 *		35. Word Problems: Area and Perimeter of Circles (http://www.algebralab.org/Word/Word.aspx%3Ffile%3DGeometry_AreaPerimeterCircles.xml)
 *		36. Twelve Mile Circle È The Twelve Mile Circle, Part II - maps ... (http://www.howderfamily.com/blog/%3Fp%3D497)
 *		37. Circles - GMAT Math Study Guide (http://www.platinumgmat.com/gmat_study_guide/circles)
 *		38. Circle definition and properties- Math Open Reference (http://www.mathopenref.com/circle.html)
 *		39. MATHS TEST --- THE CIRCLE (http://www.seomraranga.com/maths/The%2520Circle.doc)
 *		40. name the shaded part of the circle as a fraction | TutorVista | 22228 (http://www.tutorvista.com/q/name-the-shaded-part-of-the-circle-as-a-fraction/q22228_86839)
 *		41. Early Fraction Ideas with Models: 2.5 - Part 2 - Number ... (http://www.education.vic.gov.au/studentlearning/teachingresources/maths/mathscontinuum/number/N25001P_2.htm)
 *		42. Neolithic Revolutions: Archaeological Patterning across Southern ... (http://neolithic-revolutions.blogspot.com/2010/01/archaeological-patterning-across.html)
 *		43. Anatomy of the Brain: Stroke Center at University Hospital, Newark ... (http://www.theuniversityhospital.com/stroke/anatomy.htm)
 *		44. Humanity On The Pollen Path - Part Five (http://www.greatdreams.com/plpath5.htm)
 *		45. 11th Street bridges, part 1: The plan - Greater Greater Washington (http://greatergreaterwashington.org/post.cgi%3Fid%3D1676)
 *		46. Circle O Trail (http://www.utahmountainbiking.com/trails/circle-o.htm)
 *		47. Centennial Fire District (http://www.centennialfire.org/)
 *		48. Arc of a circle, minor arc, major arc, and central angle. (http://www.mathwarehouse.com/geometry/circle/arc-of-circle.php)
 *		49. BUL320/AE084: Lawn Sprinkler Selection and Layout for Uniform ... (http://edis.ifas.ufl.edu/ae084)
 *		50. PI.html (http://www.hitchins.net/PI.html)
 *		51. Measurement Session 7: Circles and pi (http://www.learner.org/courses/learningmath/measurement/session7/part_b/examining.html)
 *		52. Intermediate Test Prep Math 8(Grade 6) Area of a sector of a circle (http://www.studyzone.org/mtestprep/math8/e/circlesector6p.cfm)
 *		53. Practice with Sectors and Segments of Circles (http://regentsprep.org/Regents/math/geometry/GP16/PracCircleSectorsSegments.htm)
 *		54. Trigonometry: Radians - CliffsNotes (http://www.cliffsnotes.com/study_guide/Radians.topicArticleId-11658,articleId-11589.html)
 *		55. Perimeters and Areas (http://www.andrews.edu/~calkins/math/webtexts/geom08.htm)
 *		56. HGIC 1801 Landscape Irrigation Equipment Part 1: Sprinklers ... (http://www.clemson.edu/extension/hgic/plants/other/irrigation/hgic1810.html)
 *	Shown results:
 *		1. Circular segment - Wikipedia, the free encyclopedia (http://en.wikipedia.org/wiki/Circular_segment - Reranked - 2)
 *		2. Math Forum: Ask Dr. Math FAQ: <b>Circle</b> Formulas (http://mathforum.org/dr/math/faq/formulas/faq.circle.html - Original - 1)
 *		3. <b>Area</b> <b><b>of</b></b> Sectors and Segments <b><b>of</b></b> <b>Circle</b>s (http://regentsprep.org/Regents/math/geometry/GP16/CircleSectors.htm - Reranked - 3)
 *		4. Circular Segment -- from Wolfram MathWorld (http://mathworld.wolfram.com/CircularSegment.html - Original - 4)
 *		5. The <b>Area</b> <b><b>of</b></b> Part <b><b>of</b></b> a <b>Circle</b> (http://www.mathreference.com/ca-int,carea2.html - Reranked - 5)
 *		6. <b>Area</b> <b><b>of</b></b> a <b>circle</b> segment - Math Open Reference (http://www.mathopenref.com/segmentarea.html - Original - 6)
 *		7. <b>Area</b> <b><b>Of</b></b> <b>Portion</b> <b><b>Of</b></b> <b>Circle</b> | TutorNext.com (http://www.tutornext.com/help/area-of-portion-of-circle - Reranked - 7)
 *		8. Cool math .com - The Geometry <b><b>of</b></b> <b>Circle</b>s - Radius, Diameter ... (http://www.coolmath.com/reference/circles-geometry.html - Original - 8)
 *		9. what <b>portion</b> <b><b>of</b></b> the whole <b>circle</b> does each equal part cover as ... (http://www.tutorvista.com/q/what-portion-of-the-whole-circle-does-each-equal-part-cover-as-shown/q16911_86838 - Reranked - 11)
 *		10. Geometry: Arc Length and Sectors - CliffsNotes (http://www.cliffsnotes.com/study_guide/Arc-Length-and-Sectors.topicArticleId-18851,articleId-18829.html - Original - 9)
 *	-->
 */
public class QueryStatistics {

	public static void main(String[] args) {

		try {
			
			// Load the interleaving log file from the server
			FileInputStream fstream = new FileInputStream(ConfigLoader.getConfig().getProperty("logfile"));
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			
			// ArrayLists for the number of re-rankings per profile. The position in the ArrayList
			// is an indication of the rank investigated
			ArrayList<Integer> RR1 = new ArrayList<Integer>();
			ArrayList<Integer> RR2 = new ArrayList<Integer>();
			ArrayList<Integer> RR3 = new ArrayList<Integer>();
			
			for (int i = 0; i < 56; i++){
				RR1.add(i, 0);
				RR2.add(i, 0);
				RR3.add(i, 0);
			}
			
			boolean inRR1 = false; boolean inRR2 = false; boolean inRR3 = false;
			int totalRR1 = 0; int totalRR2 = 0; int totalRR3 = 0;
			
			ArrayList<String> GoogleResults = new ArrayList<String>();
			ArrayList<String> RerankResults = new ArrayList<String>();
			
			boolean inGoogle = false; boolean inRerank = false;
			
			while ((strLine = br.readLine()) != null) {
				
				// Extract which profile is currently used for re-ranking
				if (strLine.contains("Profile:")){
					inRR1 = false; inRR2 = false; inRR3 = false;
					inGoogle = false; inRerank = false;
					if (strLine.contains("_ti_")){
						inRR1 = true; totalRR1++;
					} else if (strLine.contains("_t_")){
						inRR2 = true; totalRR2++;
					} else if (strLine.contains("_b_")){
						inRR3 = true; totalRR3++;
					}
				// Detect start of Google Ranking	
				} else if (strLine.contains("Google Ranking:")){
					inGoogle = true; inRerank = false;
				// Detect start of personalized ranking
				} else if (strLine.contains("Reranked Ranking:")){
					inGoogle = false; inRerank = true;
				// Detect start of interleaved ranking
				} else if (strLine.contains("-->") || strLine.contains("Shown results:")){
					inGoogle = false; inRerank = false;
					try {
						for (int i = 0; i < GoogleResults.size(); i++){
							// Check whether the current rank is different to the original rank
							//  i.e. has re-ranking happened
							if (!GoogleResults.get(i).equals(RerankResults.get(i))){
								if (inRR1){
									RR1.set(i, RR1.get(i) + 1); 
								} else if (inRR2){
									RR2.set(i, RR2.get(i) + 1); 
								} else if (inRR3){
									RR3.set(i, RR3.get(i) + 1); 
								}
							}
						}
					} catch (Exception ex){
						ex.printStackTrace();
					}
					GoogleResults = new ArrayList<String>();
					RerankResults = new ArrayList<String>();
				} else {
					if (inGoogle){
						// Store the Google rank result
						String result = strLine.trim().substring(strLine.trim().indexOf(".") + 1);
						GoogleResults.add(result);
					} else if (inRerank){
						// Store the re-ranked result
						String result = strLine.trim().substring(strLine.trim().indexOf(".") + 1);
						RerankResults.add(result);
					}
				}
				
			}
			in.close();
			
			// Total number of queries for each investigated profile
			System.out.println("Total queries for RR1: " + totalRR1);
			System.out.println("Total queries for RR2: " + totalRR2);
			System.out.println("Total queries for RR3: " + totalRR3);
			
			// Print out how much re-ranking happened at each rank
			System.out.println("");
			System.out.println("RR1 per rank:");
			for (int i = 0; i < 50; i++){
				System.out.println(RR1.get(i));
			}
			
			System.out.println("");
			System.out.println("RR2 per rank:");
			for (int i = 0; i < 50; i++){
				System.out.println(RR2.get(i));
			}
			
			System.out.println("");
			System.out.println("RR3 per rank:");
			for (int i = 0; i < 50; i++){
				System.out.println(RR3.get(i));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
