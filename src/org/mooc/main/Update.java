package org.mooc.main;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.mooc.processing.logs.GenUserLearnedCourses;
import org.mooc.processing.logs.ProcessLogs;
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
			System.out.println("***************** Update start *****************");
			long start_time=System.currentTimeMillis();
			
			// Current date
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
			String dateString = sdf.format(date);
			
			/* 
			 * One day's increment, 
			 * (logs -> the user-learned-courses -> frequent-courses-pairs -> frequent course recommendations).
			 * Notice : 
			 * 1. course is update in Class GenFrequentCourses just when it need to be updated; 
			 * 2. users don't need to update yet.
			 */
			ProcessLogs.oneDayIncrease(dateString);
			
			GenUserLearnedCourses.oneDayIncrease(dateString);
			
			GenAprioriDataset.generateRecords();
			GenFrequentCourses.genFrequentCourses();
			GenFrequentRec.generateFrequentRec();
			
			long end_time=System.currentTimeMillis();
			System.out.println("***************** Update finished *****************");
			long cost = (end_time-start_time) / 1000;
			System.out.println("***************** Cost " + cost + "s *****************");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
