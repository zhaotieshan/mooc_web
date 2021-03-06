package org.mooc.utility;

import java.util.Arrays;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
* @author : wuke
* @date   : 20161123 4:01:53
* Title   : MongoDBConn
* Description : 
*/
public class MongoConn {
	private static MongoClient MONGOCLIENT = null;
	private static String USERNAME = "mooc";
	private static String PASSWORD = "mooc123";
	
	/**
	 * Return MongoCollection
	 * @param databaseName
	 * @param mongodbCollectionName
	 * @return mongoCollection
	 */
    public static MongoCollection<Document> getMongoCollection(String databaseName, String mongodbCollectionName) {
    	MongoDatabase mongoDatabase = null;
    	MongoCollection<Document> mongoCollection = null;
    	
    	// Double Check Lock
    	if(MONGOCLIENT == null) {
    		synchronized(MongoConn.class) {
    		    if(MONGOCLIENT == null) {
    		    	MongoConn.initMongoClient();
    		        // MongoDBConn.initMongoClientNoAuthentication();
    		    }
    	    }
    	}
        mongoDatabase = MONGOCLIENT.getDatabase(databaseName);
        mongoCollection = mongoDatabase.getCollection(mongodbCollectionName);
        
        // System.out.println("Successfully get collection " + mongodbCollectionName + "!");
        return mongoCollection;
    }
    
    /**
     * Get MongoClient with authentication
     */
    private static void initMongoClient() {
    	ServerAddress ip = new ServerAddress("localhost",27017);
    	String databaseName = "mooc";
    	try {
    	    MongoCredential credential = MongoCredential.createCredential(USERNAME, databaseName, PASSWORD.toCharArray());
    	    MONGOCLIENT = new MongoClient(ip, Arrays.asList(credential));
    	} catch(Exception e) {
    		e.printStackTrace();
    	}  	
    }
    
    /**
     * Get MongoClient with no authentication
     */
	public static void initMongoClientNoAuthentication() {
		try {
			MONGOCLIENT = new MongoClient("localhost", 27017);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}