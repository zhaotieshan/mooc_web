package org.mooc.processing.users;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.bson.Document;
import org.mooc.utility.APICrawler;
import org.mooc.utility.MongodbConn;

import com.mongodb.client.MongoCollection;

/**
 * @author: wuke 
 * @date  : 2016Äê12ÔÂ7ÈÕ
 * Title  : CrawlerGetUsersStoreMongodb
 * Description : Get all users' data from "http://www.mooc2u.com/api/open/user/GetAllUserData", 
 *               and store them into MongoDB 'mooc.users'
 *               Until 20171226, totally 51127 users!
 *               Until 20170331, totally 71029 users!
 */
public class CrawlUsers {
	static String URL = "http://www.mooc2u.com/api/open/user/GetAllUserData";
	
	public static void main(String[] args) {
		CrawlUsers.crawlUsers();
	}
	
	/**
	 * Get users from the API of users.
	 */
	static void crawlUsers() {
		String strUsers = "";
		
		strUsers = APICrawler.getApiContent(URL, "utf-8");
		System.out.println(strUsers.length());
		
		storeUsersIntoMongodb(strUsers);
	}
	
	/**
	 * store the String, which contains a JSON array of users, into MongoDB 'mooc.users'
	 */
	static void storeUsersIntoMongodb(String strUsers) {
		JSONObject users = JSONObject.fromObject(strUsers); // transform the String into a JSON object
		JSONArray jsonArr = users.getJSONArray("Data"); // extract the JSON array from the JSON object
		
		/* get Collection 'mooc.users', need to delete the old data */
		MongoCollection<Document> collection = MongodbConn.getMongoCollection("mooc", "users");
        collection.drop();
        collection = MongodbConn.getMongoCollection("mooc", "users");
        
		// store the new data into the Collection 'mooc.users'
		Document document = null;
		for(int i = 0; i < jsonArr.size(); i++) {
			document = Document.parse(jsonArr.getJSONObject(i).toString());
			collection.insertOne(document);
		}
		
		System.out.println("Totally " + jsonArr.size() + " users!");
	}
}