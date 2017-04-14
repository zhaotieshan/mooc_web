package org.mooc.utility;

import java.util.Arrays;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDBConn {
	private static MongoClient MONGOCLIENT = null;
	
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
    		synchronized(MongoDBConn.class) {
    		    if(MONGOCLIENT == null)
    		    	MongoDBConn.initMongoClient();
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
    	String userName = "root";
    	String password = "root";
    	String databaseName = "mooc";
    	try {
    	    MongoCredential credential = MongoCredential.createCredential(userName, databaseName, password.toCharArray());
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