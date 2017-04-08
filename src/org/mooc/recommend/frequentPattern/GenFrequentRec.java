package org.mooc.recommend.frequentPattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.mooc.utility.MongodbConn;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;

/**
* @author : wuke
* @date   : 2016年12月22日下午5:02:56
* Title   : GenerateFrequentRec
* Description : 
*/
public class GenFrequentRec {

	private final static String MONGODB_NAME = "mooc";
	
	public static void main(String[] args) {
		Map<String, ArrayList<String>> userCoursesMap = GenFrequentRec.readUserCourses();
		
		long startTime = System.currentTimeMillis();
		
		System.out.println("Start generate frequent recommendations for users!");
		GenFrequentRec.generateFrequentRec(userCoursesMap);
		
		long cost = System.currentTimeMillis() - startTime;
		System.out.println("Cost " + cost + " milliseconds!");
	}
	
	/**
	 * frequent recommendation = user_learned_courses + frequent_two_courses 
	 * @param userCoursesMap
	 * @param frequentTwoItemsetMap
	 */
	static void generateFrequentRec(Map<String, ArrayList<String>> userCoursesMap) {
		String frequentRec_collection_name = "frequentRec";
		MongoCollection<Document> frequentRec_collection = 
				MongodbConn.getMongoCollection(MONGODB_NAME, frequentRec_collection_name);
		
		String frequentCourses_collection_name = "frequentCourses";
		MongoCollection<Document> frequentCourses_collection = 
				MongodbConn.getMongoCollection(MONGODB_NAME, frequentCourses_collection_name);
		
		// iterate every record(every user) in userCoursesMap
		String userId = "";
		ArrayList<String> coursesSet = null;
		for(Map.Entry<String, ArrayList<String>> entry : userCoursesMap.entrySet()) {
			userId = entry.getKey();
			coursesSet = entry.getValue();
			
			// iterate every course the user has learned, search the frequentCourses
			String course2 = "";
			int count = 0;
			for(String course : coursesSet) {
				for(Document doc : frequentCourses_collection.find(eq("course1",course))) {
					course2 = doc.getString("course2");
					count = doc.getInteger("count");
					
					Document rec = new Document(); // "userId", "course1", "course2", "count"
					rec.append("userId", userId);
					rec.append("course1", course);
					rec.append("course2", course2);
					rec.append("count", count);
					
					frequentRec_collection.insertOne(rec);
				}
			}
		}
	}
	
	/**
	 * read records of user_learned_courses from mooc.userCourses
	 * {
	 *     "_id" : ObjectId("5857a528d51d2e46cc5ea75f"),
	 *     "userId" : "02e2fbf7-bda6-44ca-b8c9-59efeb534472",
	 *     "coursesSet" : [
	 * 	       "b817c460-2141-4d85-8d7a-33eec0672b27",
	 * 	       "daf84e78-9fd6-4e17-be58-f71c38cee288"
	 *     ]
     * }
	 * @return userCoursesMap
	 */
	static Map<String, ArrayList<String>> readUserCourses() {
		Map<String, ArrayList<String>> userCoursesMap = new HashMap<String, ArrayList<String>>();
		
		String mongodb_collection = "userCourses";
		MongoCollection<Document> collection = MongodbConn.getMongoCollection(MONGODB_NAME, mongodb_collection);
		
		String userId = "";
		ArrayList<String> coursesSet = null; 
		for(Document doc : collection.find()) {
			userId = doc.getString("userId");
			coursesSet = (ArrayList<String>) doc.get("coursesSet");
			
			userCoursesMap.put(userId, coursesSet);
		}
		
		return userCoursesMap;
	}
	
	/**
	 * Not use!
	 * read the records of frequent_two_courses from mooc.frequentCourses
	 * {
	 *     "_id" : ObjectId("585b3403d51d2e0994796bd2"),
	 *     "course1" : "050fb0b8-bffe-4b28-994d-2190debef53b",
	 *     "course2" : "0d7973e0-67b1-4a0a-bbd4-fd2f0d6be887",
	 *     "count" : NumberInt("13")
     * }    
	 * @return frequentTwoItemsetMap
	 */
	/*static Map<String, Integer> readFrequentCourses() {
		Map<String, Integer> frequentTwoItemsetMap = new HashMap<String, Integer>();
		
		String mongodb_collection = "frequentCourses";
		MongoCollection<Document> collection = MongodbConn.getMongoCollection(MONGODB_NAME, mongodb_collection);
		
		String course1 = "";
		String course2 = "";
		int count = 0; 
		String temp = "";
		for(Document doc : collection.find()) {
			course1 = doc.getString("course1");
			course2 = doc.getString("course2");
			count = doc.getInteger("count");
			temp = course1 + "," + course2;
			
			frequentTwoItemsetMap.put(temp, count);
		}
		
		return frequentTwoItemsetMap;
	}*/
}
