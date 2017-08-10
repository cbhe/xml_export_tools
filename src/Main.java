

import java.util.Calendar;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import mongodb.Connection;
import tools.Arguments;
import tools.Tools;

public class Main{
	public static void main(String args[]){
		Arguments arguments = new Arguments("xet_properties.xml");//同一个目录下
		Connection connection = new Connection(arguments.ip, arguments.port, arguments.collectionName);
		MongoCollection<org.bson.Document> collection = connection.getCollection();
		
		FindIterable<org.bson.Document> findIterable = collection.find();
		for(org.bson.Document mongoDoc: findIterable){
			Calendar time = Tools.string2Calendar(mongoDoc.getString("modify_time"));
			String[] operationAndRoot = mongoDoc.getString("operation").split("_");
			String operation = operationAndRoot[0];
			String rootName  = operationAndRoot[1];
			if(time.before(arguments.end) && time.after(arguments.start) && arguments.operations.contains(operation)){
				Tools.writeXML2file(mongoDoc, arguments.fieldsCorresponding.get(operation), rootName);
			}
		}
	}
}