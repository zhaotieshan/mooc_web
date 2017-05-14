package org.mooc.processing.logs;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.bson.Document;
import org.mooc.utility.MongoConn;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

/**
* @author : wuke
* @date   : 20170422 9:03:06
* Title   : GenUserResources
* Description : Generate user viewed resources, store in silkRoad.user_viewed_company, 
*     silkRoad.user_viewed_country...
* An example in user_viewed_company,
{
	"_id" : ObjectId("58fc5ed3d1ad214d60a13fef"),
	"user_id" : "908",
	"companySet" : {
		"0014776367997089a447e3ef7874d82b5015f3316b4b829000" : NumberInt("1"),
		"001477636798918cc8557669e3d4c27a5f03f3b09b16724000" : NumberInt("1"),
		"0014776367988093e75c709322d4576a176fa7467b647b5000" : NumberInt("1")
	}
}
*/
public class GenUserLearnedCourses_new {

	public static void main(String[] args) {
		GenUserLearnedCourses_new.readLogs();
	}
	
	/**
	 * 增量计算，待补充
	 */
	static void readHistoryRecords() {
		
	}
	
	/**
	 * 增量计算，待补充
	 */
	static void readOneDayLogs(String date) {		
		MongoCollection<Document> collection = MongoConn.getMongoCollection("mooc", "logs");
		
		// "@timestamp" : "2016-06-08T13:05:26.000Z"
		Pattern pattern = Pattern.compile("^" + date + ".*$");
		BasicDBObject query = new BasicDBObject();
        query.put("@timestamp",pattern);
        
		MongoCursor<Document> cursor = collection.find(query).iterator();
	    
		Document doc = null;
		try {
			while(cursor.hasNext()) {
				doc = cursor.next();
				
				
			}
		} finally {
			cursor.close();
		}
	}
	
	/**
	 * Read all the logs, first time using
	 */
	public static void readLogs() {
		HashMap<String, HashMap<String, Integer>> userCourses = new HashMap<String, HashMap<String, Integer>>();
		
		MongoCollection<Document> collection = MongoConn.getMongoCollection("mooc", "logs");
		MongoCursor<Document> cursor = collection.find().iterator();
		
		Document doc = null;
		String userId = null;
		String courseId = null;
		
		while(cursor.hasNext()) {
			doc = cursor.next();
			
			userId = doc.getString("url_uid");
			courseId = doc.getString("url_courseid");
			
			addOneRecord(userId, courseId, userCourses);

		}
		
		// Store into MongoDB		
		GenUserLearnedCourses_new.storeUserLearnedCourses(userCourses);
	}
	
	/**
	 * 
	 * @param userId
	 * @param courseId
	 * @param userCourses
	 */
	private static void addOneRecord(String userId, String courseId, HashMap<String, HashMap<String, Integer>> userCourses) {
		HashMap<String, Integer> tem = null;
		if((userId != null) && (courseId != null)) {
			if(userCourses.containsKey(userId)) { // old user
				tem = userCourses.get(userId);
				if(tem.containsKey(courseId)) { // old course
					tem.put(courseId, tem.get(courseId) + 1); // view times plus one
				} else { // new course
					tem.put(courseId, 1); 
				}
				
				userCourses.put(userId, tem); // cover the old record
			} else { // new user
				tem = new HashMap<String, Integer>();
				tem.put(courseId, 1);
				
				userCourses.put(userId, tem); // insert the new record
			}
		}
	}
	
	/**
	 * 
	 * @param records
	 */
	private static void storeUserLearnedCourses(HashMap<String, HashMap<String, Integer>> records) {
		String collectionName = "user_learned_courses";
		MongoCollection<Document> collection = MongoConn.getMongoCollection("mooc", collectionName);
		
	    collection.drop(); // delete the old data
	    collection = MongoConn.getMongoCollection("mooc", collectionName);
	    
		String userId = null;
		HashMap<String, Integer> tem = null;
		Document doc = null;
		for(Entry<String, HashMap<String, Integer>> entry : records.entrySet()) {
			userId = entry.getKey();
			tem = entry.getValue();
			
			doc = new Document();
			doc.append("userId", userId);
			doc.append("coursesSet", tem);
			
			collection.insertOne(doc);
		}
	}
}