

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;

import mongodb.Connection;
import tools.Arguments;
import tools.Tools;

public class Main{
	public static void main(String args[]){
		//读取配置文件
		Arguments arguments = new Arguments("xet_properties.xml");//同一个目录下
		
		//解析命令行参数，用args中提供的参数替换Arguments中的特定参数配置
		arguments.modifyByCmd(args);
		
		//创建数据库链接，得到相应集合
		Connection connection = new Connection(arguments.ip, arguments.port, arguments.databaseName, arguments.collectionName);
		MongoCollection<org.bson.Document> collection = connection.getCollection();
		
		//输出重要参数信息到控制台，以便查看日志信息
		System.out.println("start-time: " + arguments.start.getTime());
		System.out.println("end-time: "   + arguments.end  .getTime());
		
		//创建一个集合保存已经导出的文档的dataid，以便去重
		HashSet<String> idSet = new HashSet<String>();
		
		//遍历所有集合中的文档并按时间【反向】排序，筛选满足条件的		
		for(org.bson.Document mongoDoc: collection.find().sort(Sorts.descending("modify_time"))){
			
			String id = mongoDoc.getString("dataid");//得到id用以去重操作
			String operation = mongoDoc.getString("operation");//operation用来确定是否导出该文档
			Calendar time = Tools.string2Calendar(mongoDoc.getString("modify_time"));//time用来确定是否导出该文档
			
			System.out.print(    "-------------" + "\n"
					           + "id  : " + id + "\n"
					           + "time: " + time.getTime() + "\n"
					           + "opt : " + operation + "\n"
					           + "res : ");
			
			if (time.before(arguments.start)) {
				//文档是按从新到旧排列的，如果当前文档的创建时间早于指定的开始时间，那么下面的就不用再找了
				System.out.println("remain");
				return;
			}
			
			if(!idSet.contains(id)//在此之前没有相同id的数据被导出
					&& time.before(arguments.end) //在结束时间之前
					&& arguments.operations.contains(operation)){
				
				System.out.println("export");
				
				idSet.add(id);				
				Object[] arr = arguments.fieldsCorresponding.get(operation);
				String rootName = (String)arr[0];
				ArrayList<String[]> corresp = (ArrayList<String[]>)arr[1];
				
				//转换成xml并写入文件
				Tools.writeXML2file(arguments.exportPath, mongoDoc, corresp, rootName);
				
			} else {
				System.out.println("remain");
			}
		}
	}
}