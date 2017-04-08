package org.mooc.processing.logs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.bson.Document;
import org.mooc.utility.MongodbConn;

import com.mongodb.client.MongoCollection;

/**
 * @author: wuke 
 * @date  : 2016年12月8日 上午9:04:24
 * Title  : ProcesssLogs
 * Description :  Read and store the logs(store in JSON files) into MongoDB mooc.logs.
 * An example,
   {
	"_id" : ObjectId("58e6ecaa47a3cc2c0cc9af0b"),
	"client" : "115.155.126.192",
	"@timestamp" : "2016-06-08T13:05:26.000Z",
	"referer" : "http://course.open.com.cn/study/ustudying.aspx?CourseID=4800fd2b-c9da-4994-af88-95de7c2ef980&ChapterID=ca279993-2e2f-4848-a10e-db99e1880708&ParentChapterID=f2e4491c-b436-4195-825d-dc655f1bda5f",
	"agent" : "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36",
	"status" : "200",
	"@version" : "1",
	"path" : "/data/logs/learning.log",
	"host" : "cloud-m3-02",
	"method" : "GET",
	"verb" : "HTTP/1.0",
	"url_path" : "/stulog",
	"url_evt" : "view",
	"url_courseid" : "4800fd2b-c9da-4994-af88-95de7c2ef980",
	"url_cid" : "ca279993-2e2f-4848-a10e-db99e1880708",
	"url_pcid" : "f2e4491c-b436-4195-825d-dc655f1bda5f",
	"url_ver" : "20160601.1",
	"url_clttime" : "1465391205374",
	"url_uid" : "12665686-4e14-4ad8-8d7f-f96badd2f68b"
   }
 */
public class ProcesssLogs {
	static final String PATH = "E:\\data\\mooc_logs\\"; // the catalog where logs are stored
	
	public static void main(String[] args) {
		// ProcesssLogs.intiProcessLogs();
	}
	
	/**
	 * Incremental processing logs.
	 */
	private static void oneDayIncrease(String date) {
		ArrayList<Document> documents = null;
		
		documents = readOneDayLogs(date);
		
		ProcesssLogs.storeOneDayLogs(documents);
	}
	
	/**
	 * First time using, process all the logs, which are stored in the catalog PATH.
	 */
	static void initProcessLogs() {
		File file = new File(PATH);
		File[] array = file.listFiles();
		
		int i;
		String date;
		for(i = 0; i < array.length; i++) {
			if(array[i].isFile()) {
				// extract date form String like "2016-08-31.json"
				date = array[i].getName().split("\\.")[0];
				
				storeOneDayLogs(readOneDayLogs(date));
				System.out.println("Successfully store logs of " + date);
			} else {
				 System.out.println(array[i].getPath() + " is not a file!");
			 }
		}
	}
	
	/**
	 * Read a day's logs from one JSON file, for example "2016-08-31.json", 
	 *   then return the result as a ArrayList of Document
	 * @param date which day's logs to be read
	 * @return logsDocuments a ArrayList of Document
	 */
	static ArrayList<Document> readOneDayLogs(String date) {
		String tempPath = PATH + date + ".json"; // the path of the JSON file
		
		ArrayList<Document> logsDocuments = new ArrayList<Document>();
		Document doc = new Document();
		
		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		try {
			fileInputStream = new FileInputStream(tempPath);
			inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			reader = new BufferedReader(inputStreamReader);

			String JsonContext = null;
			while ((JsonContext = reader.readLine()) != null) {
				// Parses a string in MongoDB Extended JSON format to a Document
				doc = Document.parse(JsonContext); 
				logsDocuments.add(doc); 
			}
			
			reader.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return logsDocuments;
	}
	
	/**
	 * Store an ArrayList of logs documents, which is actually one day's logs
	 * @param documents an ArrayList of Document
	 */
	static void storeOneDayLogs(ArrayList<Document> documents) {
		MongoCollection<Document> logsCollection = MongodbConn.getMongoCollection("mooc", "logs");
		
		logsCollection.insertMany(documents);
	}
}


