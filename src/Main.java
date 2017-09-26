

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
		//��ȡ�����ļ�
		Arguments arguments = new Arguments("xet_properties.xml");//ͬһ��Ŀ¼��
		
		//���������в�������args���ṩ�Ĳ����滻Arguments�е��ض���������
		arguments.modifyByCmd(args);
		
		//�������ݿ����ӣ��õ���Ӧ����
		Connection connection = new Connection(arguments.ip, arguments.port, arguments.databaseName, arguments.collectionName);
		MongoCollection<org.bson.Document> collection = connection.getCollection();
		
		//�����Ҫ������Ϣ������̨���Ա�鿴��־��Ϣ
		System.out.println("start-time: " + arguments.start.getTime());
		System.out.println("end-time: "   + arguments.end  .getTime());
		
		//����һ�����ϱ����Ѿ��������ĵ���dataid���Ա�ȥ��
		HashSet<String> idSet = new HashSet<String>();
		
		//�������м����е��ĵ�����ʱ�䡾��������ɸѡ����������		
		for(org.bson.Document mongoDoc: collection.find().sort(Sorts.descending("modify_time"))){
			
			String id = mongoDoc.getString("dataid");//�õ�id����ȥ�ز���
			String operation = mongoDoc.getString("operation");//operation����ȷ���Ƿ񵼳����ĵ�
			Calendar time = Tools.string2Calendar(mongoDoc.getString("modify_time"));//time����ȷ���Ƿ񵼳����ĵ�
			
			System.out.print(    "-------------" + "\n"
					           + "id  : " + id + "\n"
					           + "time: " + time.getTime() + "\n"
					           + "opt : " + operation + "\n"
					           + "res : ");
			
			if (time.before(arguments.start)) {
				//�ĵ��ǰ����µ������еģ������ǰ�ĵ��Ĵ���ʱ������ָ���Ŀ�ʼʱ�䣬��ô����ľͲ���������
				System.out.println("remain");
				return;
			}
			
			if(!idSet.contains(id)//�ڴ�֮ǰû����ͬid�����ݱ�����
					&& time.before(arguments.end) //�ڽ���ʱ��֮ǰ
					&& arguments.operations.contains(operation)){
				
				System.out.println("export");
				
				idSet.add(id);				
				Object[] arr = arguments.fieldsCorresponding.get(operation);
				String rootName = (String)arr[0];
				ArrayList<String[]> corresp = (ArrayList<String[]>)arr[1];
				
				//ת����xml��д���ļ�
				Tools.writeXML2file(arguments.exportPath, mongoDoc, corresp, rootName);
				
			} else {
				System.out.println("remain");
			}
		}
	}
}