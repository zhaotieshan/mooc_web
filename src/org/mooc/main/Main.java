package org.mooc.main;

import org.mooc.processing.courses.CrawlCourses;
import org.mooc.processing.logs.ProcesssLogs;
import org.mooc.processing.users.CrawlUsers;
import org.mooc.recommend.frequentPattern.GenAprioriDataset;
import org.mooc.recommend.frequentPattern.GenFrequentCourses;
import org.mooc.recommend.frequentPattern.GenFrequentRec;
import org.mooc.recommend.frequentPattern.GenUserLearnedCourses;

/**
* @author : wuke
* @date   : 2016年12月26日下午6:38:33
* Title   : Main
* Description : Need to be rewritten.
*/
public class Main {	
	public static void main(String[] args) {
		Main.first(args);
	}
	
	static void first(String[] args) {
		/* 用户、课程、日志等数据获取 */
		CrawlUsers.main(args);      // 爬取用户,存入MongoDB
		System.out.println("******************************用户信息爬取成功！******************************");
		
		CrawlCourses.main(args);    // 爬取课程,存入MongoDB
		System.out.println("******************************课程信息爬取成功！******************************");
		
		ProcesssLogs.main(args); // 读取日志,存入MongoDB,需要在org.mooc.processing.logs.ProcesssLogsJsonFileStoreMongodb中修改日志文件路径
		System.out.println("******************************日志信息读取成功！******************************");
		
		/* 频繁项集推荐 */
		GenUserLearnedCourses.main(args); // user_learned_courses
		System.out.println("***************************用户已学课程生成成功！***************************");
		
		GenAprioriDataset.main(args);     // process the user_learned_courses records into the form that fit the method MyApriori
		GenFrequentCourses.main(args);    // call MyApriori(), generate frequent pattern courses
		System.out.println("*************************课程频繁项集生成成功！*************************");
		
		GenFrequentRec.main(args);        // generate frequent recommendations for every user
		System.out.println("**************************为每个用户生成推荐结果成功！**************************");		
	}
}