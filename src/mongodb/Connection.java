package mongodb;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Connection {
	private String ip;
	private String port;
	private String databaseName;
	private String collectionName;
	public Connection(String ip, String port, String databaseName, String collectionName){
		this.ip = ip;
		this.port = port;
		this.databaseName = databaseName;
		this.collectionName = collectionName;
	}
	public MongoCollection<Document> getCollection(){
		MongoClient mongoClient = new MongoClient(this.ip, Integer.parseInt(this.port));
		MongoDatabase database = mongoClient.getDatabase(this.databaseName);
		MongoCollection<Document> mongoCollection= database.getCollection(this.collectionName);
		return mongoCollection;
	}
}
