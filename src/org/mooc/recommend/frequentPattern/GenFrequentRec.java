package org.mooc.recommend.frequentPattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.mooc.utility.MongoDBConn;

import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.*;

/**
* @author : wuke
* @date   : 2016年12月22日下午5:02:56
* Title   : GenFrequentRec
* Description : Generate frequent courses pairs for every user. 
*   Then store the recommendations into MongoDB mooc.frequentRec. 
*/
public class GenFrequentRec {	
	/**
	 * user_learned_courses & frequent_two_courses -> Frequent recommendation.
	 * @param userCoursesMap
	 * An example of the recommendation, 
	 * {
	 *     "_id" : ObjectId("58eae309d1ad2112f04b1e8b"),
	 *     "userId" : "cdb848ca-d702-4ae1-83f6-1f1e67c800ce",
	 *     "course1" : "0d7973e0-67b1-4a0a-bbd4-fd2f0d6be887",
	 *     "course1Name" : "CAD制图",
	 *     "course2" : "daf84e78-9fd6-4e17-be58-f71c38cee288",
	 *     "course2Name" : "计算机安全",
	 *     "count" : NumberInt("18")
	 * }
	 */
	public static void generateFrequentRec() {
		Map<String, ArrayList<String>> userCoursesMap = GenFrequentRec.readUserCourses();
		
		MongoCollection<Document> frequentRec_collection = 
				MongoDBConn.getMongoCollection("mooc", "frequentRec");
		frequentRec_collection.drop(); // delete the old data
		frequentRec_collection = MongoDBConn.getMongoCollection("mooc", "frequentRec");
		
		MongoCollection<Document> frequentCourses_collection = 
				MongoDBConn.getMongoCollection("mooc", "frequentCourses");
		
		// Iterate every record in userCoursesMap
		String userId = "";
		ArrayList<String> coursesSet = null;
		for(Map.Entry<String, ArrayList<String>> entry : userCoursesMap.entrySet()) {
			userId = entry.getKey();
			coursesSet = entry.getValue();
			
			// Iterate through the courses user has learned, search the frequentCourses
			String course2 = null;
			String course1Name = null;
			String course2Name = null;
			int count = 0;
			for(String course : coursesSet) {
				for(Document doc : frequentCourses_collection.find(eq("course1",course))) {					
					course2 = doc.getString("course2");
					
					// Judge if the course2 has already been learned
					if(coursesSet.contains(course2))
						continue;
					
					course1Name = doc.getString("course1Name");
					course2Name = doc.getString("course2Name");
					count = doc.getInteger("count");
					
					Document rec = new Document();
					rec.append("userId", userId);
					rec.append("course1", course);
					rec.append("course1Name", course1Name);
					rec.append("course2", course2);
					rec.append("course2Name", course2Name);
					rec.append("count", count);
					
					frequentRec_collection.insertOne(rec);
				}
			}
		}
	}
	
	/**
	 * Read records of user_learned_courses from MongoDB mooc.userCourses.
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
	@SuppressWarnings("unchecked")
	private static Map<String, ArrayList<String>> readUserCourses() {
		Map<String, ArrayList<String>> userCoursesMap = new HashMap<String, ArrayList<String>>();
		
		MongoCollection<Document> collection = MongoDBConn.getMongoCollection("mooc", "userCourses");
		
		String userId = "";
		ArrayList<String> coursesSet = null; 
		for(Document doc : collection.find()) {
			userId = doc.getString("userId");
			coursesSet = (ArrayList<String>) doc.get("coursesSet");
			
			userCoursesMap.put(userId, coursesSet);
		}
		
		return userCoursesMap;
	}
}
