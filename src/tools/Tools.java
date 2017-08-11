package tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class Tools {
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
	public static void appendToFile(String newStr){
		appendToFile("c:\\xml.txt", newStr);
	}
	public static Calendar string2Calendar(String pattern){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = simpleDateFormat.parse(pattern);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}
	public static void writeXML2file(org.bson.Document mongoDoc, ArrayList<String[]> corresp, String rootName){
		org.dom4j.Document xmlDoc = DocumentHelper.createDocument();
		Element root = xmlDoc.addElement(rootName);
		for(String[] tagName_valField: corresp){
			String tagName  = tagName_valField[0];
			String valfield = tagName_valField[1];
			if(valfield != null && valfield.trim().length()>0){
				root.addElement(tagName).setText(mongoDoc.getString(valfield));
			}
			else{
				root.addElement(tagName);
			}
			System.out.println(xmlDoc.asXML());
		}
		appendToFile(xmlDoc.getRootElement().asXML());
	}
}



























