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
* @date : 2017��4��11������4:27:16
* Title : MoocInit
* Description : 
*/
public class InitializeMooc {
	public static void initMooc() {
		/* �û����γ���Ϣ��ȡ & ��־���ݴ��� */
		CrawlUsers.crawlUsers();
		System.out.println("******************** �û���Ϣ��ȡ�ɹ���********************");
		
		CrawlCourses.crawlCourses();
		System.out.println("******************** �γ���Ϣ��ȡ�ɹ���********************");
		
		ProcessLogs.initProcessLogs();
		System.out.println("******************** ��־��Ϣ��ȡ�ɹ���********************");
		
		GenUserLearnedCourses.initGenUserLearnedCourses();
		System.out.println("******************** �û���ѧ�γ����ɳɹ���********************");
		
		/* Ƶ����Ƽ� */		
		GenAprioriDataset.generateRecords();
		GenFrequentCourses.genFrequentCourses();
		System.out.println("******************** �γ�Ƶ��������ɳɹ���********************");
		
		GenFrequentRec.generateFrequentRec();
		System.out.println("******************** �û��Ƽ�������ɳɹ���********************");	
	}
}