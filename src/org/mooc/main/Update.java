package org.mooc.main;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.mooc.processing.courses.CrawlCourses;
import org.mooc.processing.logs.GenUserLearnedCourses;
import org.mooc.processing.logs.ProcessLogs;
import org.mooc.processing.users.CrawlUsers;
import org.mooc.recommend.frequentPattern.GenAprioriDataset;
import org.mooc.recommend.frequentPattern.GenFrequentCourses;
import org.mooc.recommend.frequentPattern.GenFrequentRec;

/** 
 * @author wuke
 * @date  : 2017年04月11日 下午20:56:46
 * Title  : Update
 * Description : Update everyday
 */
class Update implements Runnable {
	public void run() {
		try {
			System.out.println("***************** Upate start *****************");
			long start_time=System.currentTimeMillis();
			
			// Current date
			/*Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
			String dateString = sdf.format(date);
			
			// One day's increment
			CrawlCourses.crawlCourses();
			CrawlUsers.crawlUsers();
			ProcessLogs.oneDayIncrease(dateString);
			
			GenUserLearnedCourses.oneDayIncrease(dateString);
			
			GenAprioriDataset.generateRecords();
			GenFrequentCourses.genFrequentCourses();
			GenFrequentRec.generateFrequentRec();*/
			
			long end_time=System.currentTimeMillis();
			System.out.println("***************** Upate finish *****************");
			long cost = (end_time-start_time) / 1000;
			System.out.println("***************** Cost " + cost + "s *****************");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
