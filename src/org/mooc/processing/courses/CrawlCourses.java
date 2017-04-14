package org.mooc.processing.courses;

import org.bson.Document;
import org.mooc.utility.APICrawler;
import org.mooc.utility.MongoDBConn;

import com.mongodb.client.MongoCollection;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author: wuke 
 * @date  : 2016年11月23日 下午5:36:47
 * Title  : CrawlerGetAllCourseData
 * Description : Get all courses' data from "http://www.mooc2u.com/API/Open/CourseOpen/GetAllCourseData", 
 *               then store them into MongoDB.
 *               Until 20170331, totally 153 courses!
 * Problem : BufferedReader 大小限制 会不会有影响？？？
 */
public class CrawlCourses {
	static String URL = "http://www.mooc2u.com/API/Open/CourseOpen/GetAllCourseData";
	
	public static void main(String[] args) {
		// CrawlCourses.crawlCourses();
	}
	
	/**
	 * Get courses from the API of courses.
	 */
	public static void crawlCourses() {		
		String strCourses = "";
		
		strCourses = APICrawler.getApiContent(URL, "utf-8");
		
		storeCoursesIntoMongodb(strCourses);
	}
	
	/**
	 * Store the String, which contains a JSON array of courses, into MongoDB 'mooc.courses'
	 * @param strCourses
	 */
	private static void storeCoursesIntoMongodb(String strCourses) {		
		JSONObject courses = JSONObject.fromObject(strCourses); // transform the String into a JSON object
		JSONArray jsonArr = courses.getJSONArray("Data"); // and then extract the JSON array from the JSON object
		
		// get Collection 'mooc.courses', need to delete the old data
		MongoCollection<Document> collection = MongoDBConn.getMongoCollection("mooc", "courses");
		collection.drop();
		collection = MongoDBConn.getMongoCollection("mooc", "courses");
				
		// store the new data into the Collection 'mooc.courses'
		Document document = null;
		for(int i = 0; i < jsonArr.size(); i++) {
			document = Document.parse(jsonArr.getJSONObject(i).toString());
			collection.insertOne(document);
		}
		System.out.println("Totally " + jsonArr.size() + " courses!");
	}
}
