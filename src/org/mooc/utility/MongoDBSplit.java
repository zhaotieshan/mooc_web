package org.mooc.utility;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

/**
* @author : wuke
* @date   : 2017年4月10日下午4:01:53
* Title   : MongoDBSplit
* Description : Paging query.
*/
public class MongoDBSplit {

	public static void main(String[] args) {
		List<Document> docs = MongoDBSplit.page(7, 10);
		for(Document doc : docs)
			System.out.println(doc);
	}

	static List<Document> page(int page, int pageSize) {
		List<Document> docs = new ArrayList<Document>();
		MongoCollection<Document> collection = MongoDBConn.getMongoCollection("mooc", "frequentCourses");
		
		BasicDBObject sort = new BasicDBObject();
		sort.put("count", -1);
		
		MongoCursor<Document> cursor = 
				collection.find().sort(sort).skip((page -1) * pageSize).limit(pageSize).iterator();
		
		while(cursor.hasNext()) {
			Document doc = cursor.next();
			docs.add(doc);
		}
		
		return docs;
	}
}