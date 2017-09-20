

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
		//��ȡ�����ļ�
		Arguments arguments = new Arguments("c://xet_properties.xml");//ͬһ��Ŀ¼��
		//���������в�������args���ṩ�Ĳ����滻Arguments�е��ض���������
		arguments.modifyByCmd(args);
		//�������ݿ����ӣ��õ���Ӧ����
		Connection connection = new Connection(arguments.ip, arguments.port, arguments.databaseName, arguments.collectionName);
		MongoCollection<org.bson.Document> collection = connection.getCollection();
		//�����Ҫ������Ϣ������̨���Ա�鿴��־��Ϣ
		System.out.println("start-time: "+arguments.start.getTime());
		System.out.println("end-time: "+arguments.end.getTime());
		//�������м����е��ĵ���ɸѡ����������
		for(org.bson.Document mongoDoc: collection.find()){
			String operation = mongoDoc.getString("operation");
			System.out.println("operation: " + operation);
			Calendar time = Tools.string2Calendar(mongoDoc.getString("modify_time"));
			System.out.println("this.doc.time: "+time.getTime());

			if(time.before(arguments.end) && time.after(arguments.start) && arguments.operations.contains(operation)){
				Object[] arr = arguments.fieldsCorresponding.get(operation);
				String rootName = (String)arr[0];
				ArrayList<String[]> corresp = (ArrayList<String[]>)arr[1];
				//ת����xml��д���ļ�
				Tools.writeXML2file(mongoDoc, corresp, rootName);
			}
		}
	}
}