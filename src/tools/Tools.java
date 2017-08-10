package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

import com.go2map.lsp.local.engine.mapservice.fulltextindex.POIManager2;
import com.google.gson.Gson;

import berkeleydb.util.LocalStruct;

public class Tools {
	public static boolean policyStatus = false;
	private static Logger logger = Logger.getLogger(Tools.class);
	public static void setPolicy(Object obj){
		String path = obj.getClass().getResource("").getPath();
		String policyPath = "policy.txt";
		System.setProperty("java.security.policy", policyPath);
		System.setSecurityManager(new SecurityManager());
		policyStatus = true;
	}
	public static void appendToFile(String filePath, String newStr) {
		/**
		 * 向文件末尾追加内容
		 * @param filePath 文件全路径 c:\temp\demo.txt
		 * @param newStr 新内容
		 * @return 
		 */
		try {
			File file = new File(filePath);
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, true), "GBK"));
			bufferedWriter.write(newStr+"\n");
			bufferedWriter.flush();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public static Object jsonToBean(String jsonString, Class class1){
		Gson gson = new Gson();
		return gson.fromJson(jsonString, class1);
	}
	public static String formatedTimeString(String formater){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formater);
		Date date = new Date();
		String time = simpleDateFormat.format(date);
		return time;
	}
	
	static class Bean{
		String uniqueid;
		String dataid;
	}
	public static String[] getDataIdAndUid(String name, double x, double y, String category, String subcategory){
		try {//中文需要进行URL编码
			 //《URL编码与解码》：http://www.cnblogs.com/liuhongfeng/p/5006341.html
			name        = URLEncoder.encode(name, "utf-8");
			category    = URLEncoder.encode(category, "utf-8");
			subcategory = URLEncoder.encode(subcategory, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String urlStr = "http://10.146.33.48:8080/EditPlatformServer/generateid?"
					   +"name="+name+"&"//例如，北京西站
					   +"x="+x+"&"//例如，12948289.09
					   +"y="+y+"&"//例如，4822978.43
					   +"category="+category+"&"//例如，交通出行
					   +"subcategory="+subcategory//例如，火车站
					   +"&usertag=haomaiyys&returnmode=2";
		String responseStr = sendGet(urlStr);
		Bean bean = (Bean)jsonToBean(responseStr, Bean.class);
		String[] strings = new String[2];
		strings[0] = bean.dataid;
		strings[1] = bean.uniqueid;
		return strings;
	}
	
	/*
	 * 接受一个url参数，然后发起一个get方法到服务器
	 * 然后以字符串形式(String)返回response的内容
	 */
	public static String sendGet(String urlStr){
		String result = "";
		try {
			URL url = new URL(urlStr);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();	
			connection.connect();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				result += line;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			result = new String(result.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	public static ArrayList<Object> getUidAndlocalStructByDateId (String dataId, Object obj){
		ArrayList<Object> res = new ArrayList<Object>();
		if(policyStatus == false){
			setPolicy(obj);
		}
		String uid;
		try {
			uid = POIManager2.getUIDbyDataid(dataId);
			LocalStruct localStruct = POIManager2.getSearchResult_by_UID(uid)
												 .getInnerResultSet()
												 .getResult(0)
												 .getLocalStruct();
			res.add(uid);
			res.add(localStruct);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e){
			e.printStackTrace();
		}
		return res;
	}
}



























