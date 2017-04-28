package org.mooc.recommend.frequentPattern;

import java.util.ArrayList;

import org.bson.Document;
import org.mooc.utility.MongoConn;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

/**
 * @author: wuke 
 * @date  : 2016年12月19日 下午9:29:03
 * Title  : GenAprioriDataset
 * Description : Read records from MongoDB mooc.userCourses, generate dataset for Apriori.
 * An example, need to transform the former into the latter, 
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
 * "417cc764-ec96-4251-8200-1033ac256b93,7c731203-fef5-4b28-95cb-fea534210f97,a5fa3d7c-b633-429c-9d58-09e44cfe60de"
 */
public class GenAprioriDataset {	
	/**
	 * Notice, there are "undefined" course in the user-learned-courses, like 
	 * {
	 *     "_id" : ObjectId("58e89dab47a3cc3af811cc9a"),
	 *     "userId" : "2c7b318f-e26e-4a3e-9ac2-e961a8e095af",
	 *     "coursesSet" : [
	 *         "4f5244dd-8610-44ca-a8b2-3cf435011b0f",
	 *     	   "a8d60e27-95f3-450d-b50e-07e68208e86f",
	 *     	   "undefined"
	 *     ]
     * }
	 * @return apripriRecords ArrayList<String>
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<String> generateRecords() {
		ArrayList<String> apripriRecords = new ArrayList<String>();
		
		ArrayList<String> temp = new ArrayList<String>();
		StringBuilder stringBuilder = null;
		
		MongoCollection<Document> collection = MongoConn.getMongoCollection("mooc", "userCourses");
		Document doc = null;
		MongoCursor<Document> cursor = collection.find().iterator();
		try {
			while(cursor.hasNext()) {
				doc = cursor.next();
				temp = (ArrayList<String>) doc.get("coursesSet");
				
				stringBuilder = new StringBuilder();

				int i;
				String courseId = "";
				for(i = 0; i < (temp.size()-1); i++) {
					courseId = temp.get(i);
					if(!(courseId.equals("undefined"))) {
						stringBuilder.append(courseId);
						stringBuilder.append(",");
					}
				}
				// process the last course
				if(!(temp.get(i).equals("undefined")))
				    stringBuilder.append(temp.get(i));
				else // the last course is "undefined", need to delete the ','
					stringBuilder.deleteCharAt(stringBuilder.length()-1);
				
				apripriRecords.add(stringBuilder.toString());
			}
		} finally {
			cursor.close();
		}
		
		return apripriRecords;
	}
}
