package org.mooc.recommend.frequentPattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.bson.Document;
import org.mooc.utility.MongodbConn;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

/**
 * @author: wuke 
 * @date  : 2016年12月12日 下午9:06:17
 * Title  : GenerateUserLearnedCoursesSets
 * Description : generate records of user-learned-courses, like:
 * {
 * 	  "_id" : ObjectId("5857a528d51d2e46cc5ea749"),
 * 	  "userId" : "b5cfa615-35b0-43ca-a856-6e300949b0f3",
 * 	  "coursesSet" : [
 * 		  "4800fd2b-c9da-4994-af88-95de7c2ef980",
 * 		  "53be568c-af84-4e4d-93f1-b8a4c657598d",
 * 		  "55e5bc18-b09b-4964-bf3e-69dabd1957d4",
 * 		  "65fe9ec6-c084-43a6-970c-97b71a5edba9",
 * 		  "c56bc1f9-cbad-4c55-8a0e-6403bceef936"
 * 	  ]
 * }
 * 20161227 cost 2121 milliseconds!
 */

public class GenerateUserLearnedCourses {
	/**
	 * 
	 * @param args   
	 */
	public static void main(String[] args) {
		// first time using for batch processing
		long start = System.currentTimeMillis();
		
		List<Document> logsDocuments = 
				GenerateUserLearnedCourses.readLogsFromMongodb();
		Map<String, TreeSet<String>> userCoursesMap = 
				GenerateUserLearnedCourses.processLogsDocuments(logsDocuments);
		Map<String, ArrayList<String>> historyUserCoursesMap = 
				GenerateUserLearnedCourses.readHistoryUserCoursesFromMongodb();
		Map<String, ArrayList<String>> newUserCoursesMap = 
				GenerateUserLearnedCourses.mergeUserCourses(historyUserCoursesMap, userCoursesMap);
		GenerateUserLearnedCourses.storeUserCoursesIntoMongodb(newUserCoursesMap);
		
		long cost = System.currentTimeMillis() - start;
		System.out.println("Cost " + cost + " milliseconds!");
		
		// one day
		/*String date = "";
		List<Document> logsDocuments = 
				GenerateUserLearnedCoursesSets.readOneDayLogsFromMongodb(date);
		Map<String, TreeSet<String>> userCoursesMap = 
				GenerateUserLearnedCoursesSets.processLogsDocuments(logsDocuments);
		Map<String, ArrayList<String>> historyUserCoursesMap = 
				GenerateUserLearnedCoursesSets.readHistoryUserCoursesFromMongodb();
		Map<String, ArrayList<String>> newUserCoursesMap = 
				GenerateUserLearnedCoursesSets.mergeUserCourses(historyUserCoursesMap, userCoursesMap);
		GenerateUserLearnedCoursesSets.storeUserCoursesIntoMongodb(newUserCoursesMap);*/
	}
	
	/**
	 * testing
	 */
	static void test() {
		// test readOneDayLogsFromMongodb(String date)
		readOneDayLogsFromMongodb("2016-06-08");
	}
	
	/**
	 * Read all the logs from mooc.logs, return a ArrayList of Documents
	 * @return logsDocuments List<Document> 
	 */
	static List<Document> readLogsFromMongodb() {
		List<Document> logsDocuments = new ArrayList<Document>();
		
		// connect to Mongodb, get collection mooc.logs
		MongoCollection<Document> collection = MongodbConn.getMongoCollection("mooc", "logs");
			
		MongoCursor<Document> cursor = collection.find().iterator();
		Document doc = new Document();
		try {
			while(cursor.hasNext()) {
				doc = cursor.next();
				
				// just keep the "url_courseid" and "url_uid"
				Document temp = new Document();
				temp.append("url_courseid", doc.getString("url_courseid"));
				temp.append("url_uid", doc.getString("url_uid"));
				logsDocuments.add(temp);
			}
		} finally {
			cursor.close();
		}
			
	    System.out.println("Successfully read the all the logs!");
		
		return logsDocuments;
	}
	
	/**
	 * Read one day's logs from mooc.logs, return a ArrayList of Documents
	 * @param date
	 * @return logsDocuments List<Document> 
	 */
	static List<Document> readOneDayLogsFromMongodb(String date) {
		List<Document> logsDocuments = new ArrayList<Document>();
		
		// connect to Mongodb, get collection mooc.logs
		MongoCollection<Document> collection = MongodbConn.getMongoCollection("mooc", "logs");
			
		Pattern pattern = Pattern.compile("^" + date + ".*$"); // 左匹配
		BasicDBObject query = new BasicDBObject();
        query.put("@timestamp",pattern);
        
		MongoCursor<Document> cursor = collection.find(query).iterator();
	    
		Document doc = new Document();
		try {
			while(cursor.hasNext()) {
				doc = cursor.next();
				
				// just keep the "url_courseid" and "url_uid"
				Document temp = new Document();
				temp.append("url_courseid", doc.getString("url_courseid"));
				temp.append("url_uid", doc.getString("url_uid"));
				logsDocuments.add(temp);
			}
		} finally {
			cursor.close();
		}
		System.out.println(logsDocuments.size());
	    System.out.println("Successfully read " + date + " logs!");
		
		return logsDocuments;
	}
	
	/**
	 * process one Document list, store the result into "Map<String, TreeSet<String>> userCoursesMap"
	 * @param logsDocuments 
	 * @return userCoursesMap Map<String, TreeSet<String>>
	 */
    static Map<String, TreeSet<String>> processLogsDocuments(List<Document> logsDocuments) {
    	Map<String, TreeSet<String>> userCoursesMap = new HashMap<String, TreeSet<String>>();
    	
    	TreeSet<String> courseSet = null;
    	
    	String userId = "";
    	String courseId = "";
    	for(Document doc : logsDocuments) {
    		userId = (String) doc.get("url_uid");
    		courseId = (String) doc.get("url_courseid");
    		
    		if((userId != null) && (courseId != null)) {
				if (userCoursesMap.containsKey(userId)) { // old user
					// update the course set
					courseSet = userCoursesMap.get(userId);
					courseSet.add(courseId);
					
					userCoursesMap.put(userId, courseSet);				
				} else { // new user
					courseSet = new TreeSet<String>();
					courseSet.add(courseId);
					
					userCoursesMap.put(userId, courseSet);
				}
    		}
    	}
    	
    	System.out.println("Successfully processLogsDocuments()!");
    	return userCoursesMap;
    }
	
	/**
	 * read history records, user-learned-courses collection, form mongodb,
	 * store into one HashMap historyUserCoursesMap
	 * notice : TreeSet<String> will change to ArrayList<String> 
	 * @return
	 */
	static Map<String, ArrayList<String>> readHistoryUserCoursesFromMongodb() {
		Map<String, ArrayList<String>> historyUserCoursesMap = new HashMap<String, ArrayList<String>>();
		
		// connect to Mongodb, get collection mooc.userCourses
		MongoCollection<Document> collection = MongodbConn.getMongoCollection("mooc", "userCourses");
		
		MongoCursor<Document> cursor = collection.find().iterator();
		Document doc = new Document();
		try {
			while(cursor.hasNext()) {
				doc = cursor.next();
				historyUserCoursesMap.put(doc.getString("userId"), (ArrayList<String>) doc.get("coursesSet"));
			}
		} finally {
			cursor.close();
		}
		
    	System.out.println("Successfully read the old records of user-learned-courses!");
		return historyUserCoursesMap;
	}
	
	/**
	 * merge the new and old records of user-learned-courses
	 * @param historyUserCoursesMap
	 * @param userCoursesMap
	 */
	static Map<String, ArrayList<String>> mergeUserCourses(Map<String, ArrayList<String>> historyUserCoursesMap, 
			Map<String, TreeSet<String>> userCoursesMap) {
		Map<String, ArrayList<String>> newUserCoursesMap = null;
		
		ArrayList<String> arrayList = null;
		TreeSet<String> treeSet = null;
		String key = "";
		// iterate "Map<String, TreeSet<String>> userCoursesMap"
		for(Map.Entry<String, TreeSet<String>> entry : userCoursesMap.entrySet()) {
			key = entry.getKey();
			
			if(historyUserCoursesMap.containsKey(key)) { // old user
				// first merge the ArrayList into TreeSet, then put the result TreeSet into the ArrayList 
				arrayList = historyUserCoursesMap.get(key);
				treeSet = entry.getValue();
				
				treeSet.addAll(arrayList);
				
				arrayList.clear();
				arrayList.addAll(treeSet);
				
				historyUserCoursesMap.put(key, arrayList);
			} else { // new user
				// directly store into ArrayList
				treeSet = entry.getValue();
				
				arrayList = new ArrayList<String>();
				arrayList.addAll(treeSet);
				
				historyUserCoursesMap.put(key, arrayList);
			}
		}
		
    	System.out.println("Successfully merge the old and new records of user-learned-courses!");
		newUserCoursesMap = historyUserCoursesMap;
		return newUserCoursesMap;
	}
	
	/**
	 * store new user_learned_courses records into MongoDB
	 * @param userCoursesMap Map<String, ArrayList<String>>
	 */
	static void storeUserCoursesIntoMongodb(Map<String, ArrayList<String>> userCoursesMap) {
		MongoCollection<Document> collection = MongodbConn.getMongoCollection("mooc", "userCourses");
		
		collection.drop(); // delete the old data
		
		collection = MongodbConn.getMongoCollection("mooc", "userCourses");
		
		Document doc = null;
		for(Map.Entry<String, ArrayList<String>> entry : userCoursesMap.entrySet()) {
			doc = new Document(); // need new Document object every time, because ObjectId
			doc.append("userId", entry.getKey());
			doc.append("coursesSet", entry.getValue());
			
			collection.insertOne(doc);
		}
		
    	System.out.println("Successfully store the merged records of user-learned-courses!");
	}
}
