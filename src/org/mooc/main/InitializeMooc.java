package org.mooc.main;

import org.mooc.processing.courses.CrawlCourses;
import org.mooc.processing.logs.GenUserLearnedCourses;
import org.mooc.processing.logs.ProcessLogs;
import org.mooc.processing.users.CrawlUsers;
import org.mooc.recommend.frequentPattern.GenAprioriDataset;
import org.mooc.recommend.frequentPattern.GenFrequentCourses;
import org.mooc.recommend.frequentPattern.GenFrequentRec;

/**
* @author : wuke
* @date : 2017年4月11日下午4:27:16
* Title : MoocInit
* Description : Initialize the Web Service, which is called in Class InitListener.
*/
public class InitializeMooc {
	public static void initMooc() {
		
		// CrawlUsers.crawlUsers(); // Not use yet.
		// System.out.println("******************** 用户信息爬取成功！********************");
		
		CrawlCourses.crawlCourses();
		System.out.println("******************** 课程信息爬取成功！********************");
		
		ProcessLogs.initProcessLogs();
		System.out.println("******************** 日志信息读取成功！********************");
		
		GenUserLearnedCourses.initGenUserLearnedCourses();
		System.out.println("******************** 用户已学课程生成成功！********************");
			
		GenAprioriDataset.generateRecords();
		GenFrequentCourses.genFrequentCourses();
		System.out.println("******************** 课程频繁二项集生成成功！********************");
		
		GenFrequentRec.generateFrequentRec();
		System.out.println("******************** 用户推荐结果生成成功！********************");	
	}
}
