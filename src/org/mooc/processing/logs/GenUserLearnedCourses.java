package org.mooc.processing.logs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.bson.Document;
import org.mooc.utility.MongoDBConn;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

/**
 * @author: wuke 
 * @date  : 2016年12月12日 下午9:06:17
 * Title  : GenUserLearnedCourses
 * Description : Generate records of user-learned-courses, then store them into MongoDB mooc.userCourses.
 * An example, 
 * {
 *     "_id" : ObjectId("5857a528d51d2e46cc5ea752"),
 * 	   "userId" : "a90749a2-0fbf-42b7-b1dd-9d5beb8be0e9",
 * 	   "coursesSet" : [
 * 	       "417cc764-ec96-4251-8200-1033ac256b93",
 * 		   "7c731203-fef5-4b28-95cb-fea534210f97",
 * 		   "a5fa3d7c-b633-429c-9d58-09e44cfe60de"
 * 	   ]
 * }
 * This can be apply into both Frequent Pattern and Collaborative Filtering.
 */

public class GenUserLearnedCourses {
	public static void main(String[] args) {
		// GenerateUserLearnedCourses.initGenUserLearnedCourses();
	}
	
	/**
	 * First time using, process all the records in MongoDB mooc.logs.
	 */
	public static void initGenUserLearnedCourses() {
		List<Document> logsDocuments = 
				GenUserLearnedCourses.readLogsFromMongoDB();
		Map<String, TreeSet<String>> userCoursesMap = 
				GenUserLearnedCourses.processLogsDocuments(logsDocuments);
		Map<String, ArrayList<String>> historyUserCoursesMap = 
				GenUserLearnedCourses.readHistoryUserCoursesFromMongodb(); // empty
		Map<String, ArrayList<String>> newUserCoursesMap = 
				GenUserLearnedCourses.mergeUserCourses(historyUserCoursesMap, userCoursesMap);
		GenUserLearnedCourses.storeUserCoursesIntoMongodb(newUserCoursesMap);
	}
	
	/**
	 * Incremental processing user-learned-courses.
	 */
	public static void oneDayIncrease(String date) {
		List<Document> logsDocuments = 
				GenUserLearnedCourses.readOneDayLogsFromMongodb(date);
		Map<String, TreeSet<String>> userCoursesMap = 
				GenUserLearnedCourses.processLogsDocuments(logsDocuments);
		Map<String, ArrayList<String>> historyUserCoursesMap = 
				GenUserLearnedCourses.readHistoryUserCoursesFromMongodb();
		Map<String, ArrayList<String>> newUserCoursesMap = 
				GenUserLearnedCourses.mergeUserCourses(historyUserCoursesMap, userCoursesMap);
		GenUserLearnedCourses.storeUserCoursesIntoMongodb(newUserCoursesMap);
	}
	
	/**
	 * Read all the logs from MongoDB mooc.logs, return a ArrayList of Documents.
	 * An example, 
	 * {
	 *     "url_courseid" : "4800fd2b-c9da-4994-af88-95de7c2ef980", 
	 *     "url_uid" : "12665686-4e14-4ad8-8d7f-f96badd2f68b"
	 * }
	 * @return logsDocuments List<Document> 
	 */
	private static List<Document> readLogsFromMongoDB() {
		List<Document> logsDocuments = new ArrayList<Document>();
		
		MongoCollection<Document> collection = MongoDBConn.getMongoCollection("mooc", "logs");
			
		MongoCursor<Document> cursor = collection.find().iterator();
		Document doc = new Document();
		try {
			while(cursor.hasNext()) {
				doc = cursor.next();
				
				Document temp = new Document();
				temp.append("url_courseid", doc.getString("url_courseid"));
				temp.append("url_uid", doc.getString("url_uid"));
				logsDocuments.add(temp);
			}
		} finally {
			cursor.close();
		}
			
	    System.out.println("Successfully read the all the logs from MongoDB mooc.logs!");
		
		return logsDocuments;
	}
	
	/**
	 * Read one day's logs from MongoDB mooc.logs, return a ArrayList of Documents.
	 * @param date
	 * @return logsDocuments List<Document> 
	 */
	private static List<Document> readOneDayLogsFromMongodb(String date) {
		List<Document> logsDocuments = new ArrayList<Document>();
		
		MongoCollection<Document> collection = MongoDBConn.getMongoCollection("mooc", "logs");
		
		// "@timestamp" : "2016-06-08T13:05:26.000Z"
		Pattern pattern = Pattern.compile("^" + date + ".*$");
		BasicDBObject query = new BasicDBObject();
        query.put("@timestamp",pattern);
        
		MongoCursor<Document> cursor = collection.find(query).iterator();
	    
		Document doc = new Document();
		try {
			while(cursor.hasNext()) {
				doc = cursor.next();

				Document temp = new Document();
				temp.append("url_courseid", doc.getString("url_courseid"));
				temp.append("url_uid", doc.getString("url_uid"));
				logsDocuments.add(temp);
			}
		} finally {
			cursor.close();
		}
		
		// System.out.println(logsDocuments.size());
	    System.out.println("Successfully read " + date + " logs from MongoDB mooc.logs!");
		
		return logsDocuments;
	}
	
	/**
	 * Process the List of Document, store the result into "Map<String, TreeSet<String>> userCoursesMap"
	 * @param logsDocuments List<Document>
	 * @return userCoursesMap Map<String, TreeSet<String>>
	 */
	private static Map<String, TreeSet<String>> processLogsDocuments(List<Document> logsDocuments) {
    	Map<String, TreeSet<String>> userCoursesMap = new HashMap<String, TreeSet<String>>();
    	
    	TreeSet<String> courseSet = null;
    	
    	String userId = "";
    	String courseId = "";
    	for(Document doc : logsDocuments) { // iterate through the List<Doucment> logsDocuments
    		userId = (String) doc.get("url_uid");
    		courseId = (String) doc.get("url_courseid");
    		
    		if((userId != null) && (courseId != null)) {
				if (userCoursesMap.containsKey(userId)) { // old user, then update
					courseSet = userCoursesMap.get(userId);
					courseSet.add(courseId);
					
					userCoursesMap.put(userId, courseSet);				
				} else { // new user, then add
					courseSet = new TreeSet<String>();
					courseSet.add(courseId);
					
					userCoursesMap.put(userId, courseSet);
				}
    		}
    	}
    	
    	System.out.println("Successfully process List<Document> LogsDocuments!");
    	
    	return userCoursesMap;
    }
	
	/**
	 * Read history records(user-learned-courses)form MongoDB,
	 * then return a HashMap<String, ArrayList<String>> historyUserCoursesMap
	 * Notice : TreeSet<String> will change to ArrayList<String> 
	 * @return 
	 */
    private static Map<String, ArrayList<String>> readHistoryUserCoursesFromMongodb() {
		Map<String, ArrayList<String>> historyUserCoursesMap = new HashMap<String, ArrayList<String>>();
		
		MongoCollection<Document> collection = MongoDBConn.getMongoCollection("mooc", "userCourses");
		
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
	 * Merge the new and old records of user-learned-courses.
	 * @param historyUserCoursesMap
	 * @param userCoursesMap
	 * @return newUserCoursesMap Map<String, ArrayList<String>>
	 */
	private static Map<String, ArrayList<String>> mergeUserCourses(Map<String, ArrayList<String>> historyUserCoursesMap, 
			Map<String, TreeSet<String>> userCoursesMap) {
		Map<String, ArrayList<String>> newUserCoursesMap = null;
		
		ArrayList<String> arrayList = null;
		TreeSet<String> treeSet = null;
		String key = "";
		// iterate "Map<String, TreeSet<String>> userCoursesMap"
		for(Map.Entry<String, TreeSet<String>> entry : userCoursesMap.entrySet()) {
			key = entry.getKey();
			
			if(historyUserCoursesMap.containsKey(key)) { // old user
				// first merge the ArrayList into TreeSet, then put the result TreeSet back into the ArrayList 
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
	 * Store new user-learned-courses records into MongoDB mooc.userCourses
	 * @param userCoursesMap Map<String, ArrayList<String>>
	 */
	private static void storeUserCoursesIntoMongodb(Map<String, ArrayList<String>> userCoursesMap) {
		MongoCollection<Document> collection = MongoDBConn.getMongoCollection("mooc", "userCourses");
		
		collection.drop(); // delete the old data
		
		collection = MongoDBConn.getMongoCollection("mooc", "userCourses");
		
		Document doc = null;
		for(Map.Entry<String, ArrayList<String>> entry : userCoursesMap.entrySet()) {
			doc = new Document(); // need new Document object every time, because ObjectId
			doc.append("userId", entry.getKey());
			doc.append("coursesSet", entry.getValue());
			
			collection.insertOne(doc);
		}
		
    	System.out.println("Successfully store the new records of user-learned-courses!");
	}
}
