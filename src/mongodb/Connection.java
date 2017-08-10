package mongodb;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

public class Connection {
	private static MongoClient mongoClient;
	public static MongoClient getClient(){
		if(mongoClient == null){
			mongoClient = new MongoClient("10.142.102.33", 27017);
		}
		return mongoClient;
	}
	public static MongoCollection<Document> getMongoCollection(String collectionName){
		return getClient().getDatabase("powerlistings").getCollection(collectionName);
	}
	public static void close(){
		if(mongoClient != null){
			mongoClient.close();
			mongoClient = null;
		}
	}
}
