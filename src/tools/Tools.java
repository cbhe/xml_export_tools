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

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class Tools {
	public static void appendToFile(String filePath, String newStr) {
		/**
		 * 向文件末尾追加内容
		 * 
		 * @param filePath
		 *            文件全路径 c:\temp\demo.txt
		 * @param newStr
		 *            新内容
		 * @return
		 */
		try {
			File file = new File(filePath);
			BufferedWriter bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(filePath, true), "GBK"));
			bufferedWriter.write(newStr + "\n");
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

	public static void appendToFile(String newStr) {
		appendToFile("xml.txt", newStr);
	}

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

	public static void writeXML2file(org.bson.Document mongoDoc, ArrayList<String[]> corresp, String rootName) {
		org.dom4j.Document xmlDoc = DocumentHelper.createDocument();
		Element root = xmlDoc.addElement(rootName);
		for (String[] tagName_valField : corresp) {
			String tagName = tagName_valField[0];
			String valfield = tagName_valField[1];
			if (valfield != null && valfield.trim().length() > 0) {
				Object value = mongoDoc.get(valfield);
				if (value == null) {
					value = "";
				}
				root.addElement(tagName).setText(value.toString());
			} else {
				root.addElement(tagName);
			}
			// 输出阶段性结果
			// System.out.println(xmlDoc.asXML());
		}
		appendToFile(formatXml(xmlDoc, "utf-8", false));
	}

	public static String formatXml(Document document, String charset, boolean istrans) {
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding(charset);
		StringWriter sw = new StringWriter();
		XMLWriter xw = new XMLWriter(sw, format);
		xw.setEscapeText(istrans);
		try {
			xw.write(document.getRootElement());
			xw.flush();
			xw.close();
		} catch (IOException e) {
			System.out.println("格式化XML文档发生异常，请检查！");
			e.printStackTrace();
		}
		return sw.toString().replaceAll("\n|  ", "");
	}
	
	/*
	 * 
	 */
	public void parseArgs(String args) {
		
	}
}





























