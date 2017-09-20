

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import mongodb.Connection;
import tools.Arguments;
import tools.Tools;

public class Main{
	public static void main(String args[]){
		//读取配置文件
		Arguments arguments = new Arguments("c://xet_properties.xml");//同一个目录下
		//解析命令行参数，用args中提供的参数替换Arguments中的特定参数配置
		arguments.modifyByCmd(args);
		//创建数据库链接，得到相应集合
		Connection connection = new Connection(arguments.ip, arguments.port, arguments.databaseName, arguments.collectionName);
		MongoCollection<org.bson.Document> collection = connection.getCollection();
		//输出重要参数信息到控制台，以便查看日志信息
		System.out.println("start-time: "+arguments.start.getTime());
		System.out.println("end-time: "+arguments.end.getTime());
		//遍历所有集合中的文档，筛选满足条件的
		for(org.bson.Document mongoDoc: collection.find()){
			String operation = mongoDoc.getString("operation");
			System.out.println("operation: " + operation);
			Calendar time = Tools.string2Calendar(mongoDoc.getString("modify_time"));
			System.out.println("this.doc.time: "+time.getTime());

			if(time.before(arguments.end) && time.after(arguments.start) && arguments.operations.contains(operation)){
				Object[] arr = arguments.fieldsCorresponding.get(operation);
				String rootName = (String)arr[0];
				ArrayList<String[]> corresp = (ArrayList<String[]>)arr[1];
				//转换成xml并写入文件
				Tools.writeXML2file(mongoDoc, corresp, rootName);
			}
		}
	}
}