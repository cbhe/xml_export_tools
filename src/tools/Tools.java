package tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class Tools {
	
	/**
	 * 向文件末尾追加内容
	 * 
	 * @param filePath
	 *            文件全路径 c:\temp\demo.txt
	 * @param newStr
	 *            新内容
	 * @return
	 */
	public static void appendToFile(String filePath, String newStr) {
		//System.out.println(filePath);
		try {
			File file = new File(filePath);
			BufferedWriter bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(filePath, true), "GBK"));
			bufferedWriter.write(newStr + "\n");
			bufferedWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/*
	 * 生成对应pattern字符串时间的Calendar对象
	 */
	public static Calendar string2Calendar(String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = simpleDateFormat.parse(pattern);
		} catch (ParseException e) {
			System.out.println("格式不正确：" + pattern);
			System.exit(1);
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	
	
	/*
	 * parameter exportPath
	 * parameter mongoDoc: get from mongoBD.powerlistings.lists
	 * parameter corresp: ArrayList<String[2]> , for example: {"DATAID", "dataid"},"DATAID" is tagName, "dataid" is a key of mongoDocument 
	 * parameter rootName: "POI" in this case
	 */
	public static void writeXML2file(String exportPath, org.bson.Document mongoDoc, ArrayList<String[]> corresp, String rootName) {
		org.dom4j.Document xmlDoc = DocumentHelper.createDocument();
		Element root = xmlDoc.addElement(rootName);//"POI"
		
		for (String[] tagName_valField : corresp) {
			
			String tagName = tagName_valField[0];
			String valfield = tagName_valField[1];
			
			if (valfield != null && valfield.trim().length() > 0) {
				Object value = mongoDoc.get(valfield);
				if (value == null) {//mongo中只有该字段，没有值（可能是yext没有发过来对应值）
					value = "";
				}
				root.addElement(tagName).setText(value.toString());//example: <DATAID>12345678</DATAID>
			} else {
				root.addElement(tagName);//mongo has no such field, then only generate a empty tag, for example: <KEYWORDS/>
			}
		}
		
		String xmlStr = xmlDoc.getRootElement().asXML();    	
		
		//<deep></deep>里面的内容本来是不用转义的，所以这里将已经转义的deep中的内容反转义
		if (xmlStr.contains("DEEP")) {
			//得到<deep></deep>里面的内容
			Pattern pattern = Pattern.compile(".*?<DEEP>(.*?)</DEEP>.*");
	    	Matcher matcher = pattern.matcher(xmlStr);
	    	matcher.matches();
	    	String inDeepStr = matcher.group(1);
	    	
	    	//反转义
	    	String orginalStr = xmlUnescape(inDeepStr);
	    	
	    	//再将反转义后的数据加入到原位置：
	    	xmlStr = xmlStr.replaceAll("<DEEP>.*?</DEEP>", "<DEEP>" + orginalStr + "</DEEP>");
		}// if contains deep
		
		
		appendToFile(exportPath + getDynamicName(), xmlStr);
	}

	
	
	/*
	 *Unescape fields of the xml.
	 *XML Escape : "&" -> "&amp;"
	 *XML Unescape : "&amp;" -> "&"  (this function makes it) 
	 */
	public static String xmlUnescape(String afterEscape){
		String beforeEscape = afterEscape.replaceAll("&amp;", "&")
					 .replaceAll("&lt;", "<")
					 .replaceAll("&gt;", ">")
					 .replaceAll("&apos;", "'")
					 .replaceAll("&quto;", "\"");
		return beforeEscape;
	}

	
	
	//获取导出文件的名字。根据约定，文件名为Yext_YYYYMMDDHHmm.xml
	//注意补0，2017年9月1日7点5分，需要补足为2017年09月01日07点05分
	public static String getDynamicName() {		
		//get every field of date: int
    	Calendar calendar = Calendar.getInstance();
    	int yearInt   = calendar.get(Calendar.YEAR);
    	int monthInt  = calendar.get(Calendar.MONTH) + 1;//month starts from 0
    	int dayInt    = calendar.get(Calendar.DATE);
    	int hourInt   = calendar.get(Calendar.HOUR_OF_DAY);//24 hour clock
    	int minuteInt = calendar.get(Calendar.MINUTE);
    	
    	//parse int to String, meanwhile, add "0" if need. (9 -> "09")
    	String yearStr   = "" + yearInt;
    	String monthStr  = monthInt < 10 ? "0" + monthInt : "" + monthInt;
    	String dayStr    = dayInt < 10 ? "0" + dayInt : "" + dayInt;
    	String hourStr   = hourInt < 10 ? "0" + hourInt : "" + hourInt;
    	String minuteStr = minuteInt < 10 ? "0" + minuteInt : "" + minuteInt;
    	
    	String fileName = "Yext_" + yearStr + monthStr + dayStr + hourStr + minuteStr + ".xml";
    	
    	return fileName;
	}
}





























