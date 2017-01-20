package org.mooc.recommend.frequentPattern;

import java.util.ArrayList;
import java.util.Map;

import org.bson.Document;
import org.mooc.utility.MongodbConn;

import com.mongodb.client.MongoCollection;

/**
 * @author: wuke 
 * @date  : 2016年12月22日 上午9:41:33
 * Title  : GenerateFrequentCourses
 * Description : the document in mooc.frequentCourses is like:
 * {
 * 	"_id" : ObjectId("585b3403d51d2e0994796bd2"),
 * 	"course1" : "050fb0b8-bffe-4b28-994d-2190debef53b",
 * 	"course2" : "0d7973e0-67b1-4a0a-bbd4-fd2f0d6be887",
 * 	"count" : NumberInt("13")
 * }
 */
public class GenerateFrequentCourses {
	private static final String MONGODB_NAME = "mooc";
	private static final String COLLECTION_NAME = "frequentCourses";
	
	public static void main(String[] args) {
		// process the logs, then generate the records for Apriori
		ArrayList<String> dataList = GenerateAprioriDataset.generateRecords();
		
		// generate frequent_two_itemset
		Map<String, Integer> frequentOneItemsetMap = null;
		Map<String, Integer> frequentTwoItemsetMap = null;
		frequentOneItemsetMap = MyApriori.findFrequentOneItemset(dataList);
		frequentTwoItemsetMap = MyApriori.countCandidateTwoItemset(dataList, frequentOneItemsetMap);
		
		// store the frequent_two_itemset into Mongodb mooc.frequentCourses
		GenerateFrequentCourses.storeFrequentCoursesMongodb(frequentTwoItemsetMap);
	}
	
	static void storeFrequentCoursesMongodb(Map<String, Integer> frequentTwoItemsetMap) {
		MongoCollection<Document> collection = MongodbConn.getMongoCollection(MONGODB_NAME, COLLECTION_NAME);
		
		// drop the old data
		collection.drop();
		collection = MongodbConn.getMongoCollection(MONGODB_NAME, COLLECTION_NAME);
		
		String course1 = "";
		String course2 = "";
		int count = 0;
		for(Map.Entry<String, Integer> entry : frequentTwoItemsetMap.entrySet()) {
			Document doc = new Document();
			
			course1 = entry.getKey().split(",")[0];
			course2 = entry.getKey().split(",")[1];
			count = entry.getValue();
			
			doc.append("course1", course1);
			doc.append("course2", course2);
			doc.append("count", count);
			
			collection.insertOne(doc);
			
			// redundancy, convenient for the calculation of the frequent_courses recommendation
			doc = new Document();
			doc.append("course1", course2);
			doc.append("course2", course1);
			doc.append("count", count);
			
			collection.insertOne(doc);
		}
	}
}
