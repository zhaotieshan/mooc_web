package org.mooc.processing.courses;

import org.bson.Document;
import org.mooc.utility.APICrawler;
import org.mooc.utility.MongodbConn;

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
	
	public static void main(String[] args) {
		// CrawlerGetCoursesStoreMongodb.test();
	}
	
	/**
	 * test this function
	 */
	static void test() {
		String url = "http://www.mooc2u.com/API/Open/CourseOpen/GetAllCourseData";
		String strCourses = "";
		
		strCourses = APICrawler.getApiContent(url, "utf-8");
		System.out.println(strCourses.length());
		
		storeCoursesIntoMongodb(strCourses);
	}
	
	/**
	 * store the String, which contains a JSON array of courses, into MongoDB 'mooc.courses'
	 */
	static void storeCoursesIntoMongodb(String strCourses) {		
		JSONObject courses = JSONObject.fromObject(strCourses); // transform the String into a JSON object
		JSONArray jsonArr = courses.getJSONArray("Data"); // and then extract the JSON array from the JSON object
		
		/* get Collection 'mooc.courses', need to delete the old data */
		MongoCollection<Document> collection = MongodbConn.getMongoCollection("mooc", "courses");
		collection.drop();
		collection = MongodbConn.getMongoCollection("mooc", "courses");
				
		// store the new data into the Collection 'mooc.courses'
		Document document = null;
		for(int i = 0; i < jsonArr.size(); i++) {
			document = Document.parse(jsonArr.getJSONObject(i).toString());
			collection.insertOne(document);
		}
		System.out.println("Totally " + jsonArr.size() + " courses!");
	}
}
