package org.mooc.utility;

import java.util.Arrays;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongodbConn {
    public static MongoCollection<Document> getMongoCollection(String databaseName, String mongodbCollectionName){
    	ServerAddress ip = new ServerAddress("localhost",27017);
    	String userName = "root";
    	String password = "root";
    	MongoCredential credential = MongoCredential.createCredential(userName, databaseName, password.toCharArray());
        
    	try{
			MongoClient mongoClient = new MongoClient(ip, Arrays.asList(credential));
			
            MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
            // System.out.println("Successfully connect to MongoDB" + databaseName + "!");
            
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(mongodbCollectionName);
            System.out.println("Successfully get collection " + mongodbCollectionName + "!");
            return mongoCollection;

        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    // no password
	public static MongoCollection<Document> getMongoCollectionNoAuthentication(String databaseName, 
			String mongodbCollectionName) {
		try {
			MongoClient mongoClient = new MongoClient("localhost", 27017);
			
			MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
			// System.out.println("Successfully connect to MongoDB " + databaseName + "!");
			
			MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(mongodbCollectionName);
			System.out.println("Successfully get collection " + mongodbCollectionName + "!");
			
			return mongoCollection;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}