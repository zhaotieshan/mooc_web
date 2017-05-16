package org.mooc.controller;

import org.mooc.processing.courses.CrawlCourses;
import org.mooc.processing.logs.GenUserLearnedCourses;
import org.mooc.processing.logs.ProcessLogs;
import org.mooc.processing.users.CrawlUsers;
import org.mooc.recommend.frequentPattern.GenAprioriDataset;
import org.mooc.recommend.frequentPattern.GenFrequentCourses;
import org.mooc.recommend.frequentPattern.GenFrequentRec;

/**
* @author : wuke
* @date : 20170411 4:27:16
* Title : MoocInit
* Description : Initialize the Web Service.
*/
public class InitializeMOOC {
	/**
	 * Initialization of the whole service
	 */
	public static void main(String[] args) {
		InitializeMOOC.initMooc();
	}
	
	public static void initMooc() {
		
		// CrawlUsers.crawlUsers(); // Not use yet.
		// System.out.println("******************** Successfully crawling user information! ********************");
		
		CrawlCourses.crawlCourses();
		System.out.println("******************** Successfully crawling course information! ********************");
		
		ProcessLogs.initProcessLogs();
		System.out.println("******************** Successfully processing logs! ********************");
		
		GenUserLearnedCourses.initGenUserLearnedCourses();
		System.out.println("******************** Successfully generating user-learned-courses! ********************");
			
		GenAprioriDataset.generateRecords();
		GenFrequentCourses.genFrequentCourses();
		System.out.println("******************** Successfully generating frequent_two_itemset! ********************");
		
		GenFrequentRec.generateFrequentRec();
		System.out.println("******************** Successfully generating recommendations! ********************");	
	}
}
