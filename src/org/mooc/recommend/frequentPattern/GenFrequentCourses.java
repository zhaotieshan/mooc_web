package org.mooc.recommend.frequentPattern;

import java.util.ArrayList;
import java.util.Map;

import org.bson.Document;
import org.mooc.utility.MongoDBConn;

import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.*;

/**
 * @author: wuke 
 * @date  : 2016年12月22日 上午9:41:33
 * Title  : GenFrequentCourses
 * Description : Generate frequent courses pairs by calling algorithm MyApriori. 
 * The result namely the Documents in MongoDB mooc.frequentCourses is like:
 * {
 *     "_id" : ObjectId("58ea412d631a4e18b09165a6"),
 * 	   "course1" : "050fb0b8-bffe-4b28-994d-2190debef53b",
 *     "course1Name" : "电路",
 * 	   "course2" : "0d7973e0-67b1-4a0a-bbd4-fd2f0d6be887",
 *     "course2Name" : "CAD制图",
 * 	   "count" : NumberInt("13")
 * }
 */
public class GenFrequentCourses {
	
	public static void main(String[] args) {
		// GenFrequentCourses.genFrequentCourses();
	}
	
	/**
	 * Call functions in GenAprioriDataset and MyApriori to generate frequent courses pairs, 
	 * then call function storeFrequentCourses() store the result into MongoDB mooc.frequentCourses.
	 */
	public static void genFrequentCourses() {
		// process the logs, then generate the records for Apriori
		ArrayList<String> dataList = GenAprioriDataset.generateRecords();
		
		// generate frequent_two_itemset
		Map<String, Integer> frequentOneItemsetMap = null;
		Map<String, Integer> frequentTwoItemsetMap = null;
		frequentOneItemsetMap = MyApriori.findFrequentOneItemset(dataList);
		frequentTwoItemsetMap = MyApriori.findFrequentTwoItemset(dataList, frequentOneItemsetMap);
		
		// Store the frequent_two_itemset into Mongodb mooc.frequentCourses
		GenFrequentCourses.storeFrequentCourses(frequentTwoItemsetMap);
	}
	
	/**
	 * Store the frequent courses pairs into MongoDB mooc.frequentCourses.
	 * @param frequentTwoItemsetMap
	 */
	private static void storeFrequentCourses(Map<String, Integer> frequentTwoItemsetMap) {
		MongoCollection<Document> collection = MongoDBConn.getMongoCollection("mooc", "frequentCourses");
		
		// drop the old data
		collection.drop();
		collection = MongoDBConn.getMongoCollection("mooc", "frequentCourses");
		
		String course1 = "";
		String course2 = "";
		int count = 0;
		for(Map.Entry<String, Integer> entry : frequentTwoItemsetMap.entrySet()) {
			Document doc = new Document();
			
			course1 = entry.getKey().split(",")[0];
			course2 = entry.getKey().split(",")[1];
			count = entry.getValue();
			
			doc.append("course1", course1);
			doc.append("course1Name", GenFrequentCourses.findCourseName(course1));
			doc.append("course2", course2);
			doc.append("course2Name", GenFrequentCourses.findCourseName(course2));
			doc.append("count", count);
			
			collection.insertOne(doc);
			
			// Redundancy, convenient for generating the recommendation
			doc = new Document();
			doc.append("course1", course2);
			doc.append("course1Name", GenFrequentCourses.findCourseName(course2));
			doc.append("course2", course1);
			doc.append("course2Name", GenFrequentCourses.findCourseName(course1));
			doc.append("count", count);
			
			collection.insertOne(doc);
		}
	}
	
	/**
	 * Using CourseID to get CourseName from MongoDB mooc.courses.
	 * @param courseID
	 * @return courseName
	 */
	private static String findCourseName(String courseID) {
		String courseName = null;
		
		MongoCollection<Document> collection =  MongoDBConn.getMongoCollection("mooc", "courses");
		Document doc = collection.find(eq("CourseID", courseID)).first();
		
		courseName = doc.getString("CourseName");
		return courseName;
	}
}
