package org.mooc.recommend.frequentPattern;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.bson.Document;
import org.mooc.utility.MongodbConn;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

/**
 * @author: wuke 
 * @date  : 2016年12月19日 下午9:29:03
 * Title  : GenerateAprioriDataset
 * Description : Read the records from mooc.userCourses, generate dataset for apriori
 * 
 * {
 *     "_id" : ObjectId("5857a528d51d2e46cc5ea752"),
 * 	   "userId" : "a90749a2-0fbf-42b7-b1dd-9d5beb8be0e9",
 * 	   "coursesSet" : [
 * 	       "417cc764-ec96-4251-8200-1033ac256b93",
 * 		   "7c731203-fef5-4b28-95cb-fea534210f97",
 * 		   "a5fa3d7c-b633-429c-9d58-09e44cfe60de"
 * 	   ]
 * }     
 * -> 
 * 417cc764-ec96-4251-8200-1033ac256b93,7c731203-fef5-4b28-95cb-fea534210f97,a5fa3d7c-b633-429c-9d58-09e44cfe60de
 */
public class GenerateAprioriDataset {

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		
		ArrayList<String> apripriRecords = generateRecords();
		
		long stopTime = System.currentTimeMillis();
		long cost = stopTime-startTime;
		System.out.println("cost " + cost + " !");
		
		/*System.out.println(apripriRecords.size());
		for(String str : apripriRecords) {
			System.out.println(str);
		}*/
	}
	
	static ArrayList<String> generateRecords() {
		ArrayList<String> apripriRecords = new ArrayList<String>();
		
		ArrayList<String> temp = new ArrayList<String>();
		StringBuilder stringBuilder = null;
		
		MongoCollection<Document> collection = MongodbConn.getMongoCollection("mooc", "userCourses");
		Document doc = null;
		MongoCursor<Document> cursor = collection.find().iterator();
		try {
			while(cursor.hasNext()) {
				doc = cursor.next();
				temp = (ArrayList<String>) doc.get("coursesSet");
				
				stringBuilder = new StringBuilder();
				/*for(String str : temp) {
					stringBuilder.append(str);
					stringBuilder.append(",");
				}*/
				int i;
				String courseId = "";
				for(i = 0; i < (temp.size()-1); i++) {
					courseId = temp.get(i);
					if(!(courseId.equals("undefined"))) {
						stringBuilder.append(courseId);
						stringBuilder.append(",");
					}
				}
				if(!(temp.get(i).equals("undefined")))
				    stringBuilder.append(temp.get(i));
				else
					stringBuilder.deleteCharAt(stringBuilder.length()-1);
				
				apripriRecords.add(stringBuilder.toString());
			}
		} finally {
			cursor.close();
		}
		
		return apripriRecords;
	}
}
