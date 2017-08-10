package tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.text.ElementIterator;

import org.dom4j.*;
import org.dom4j.io.SAXReader;

public class Arguments {
	private String configFilePath;
	public Calendar start;
	public Calendar end;
	public HashMap<String, HashMap<String, String>> fieldsCorresponding;
	public String ip;
	public String port;
	public String collectionName;
	public Set<String> operations;
	
	public Arguments(String configFilePath){
		this.configFilePath = configFilePath;
		try {
			initAttrs();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void initAttrs() throws DocumentException{
		SAXReader saxReader = new SAXReader();
		File xmlFile = new File(this.configFilePath);
		Document document = saxReader.read(xmlFile);
		Element root = document.getRootElement();
		
		//connection-set
		Element connection_set = root.element("connection-set");
		this.ip = connection_set.elementTextTrim("ip");
		this.port = connection_set.elementTextTrim("port");
		this.collectionName = connection_set.elementTextTrim("collection-name");
		
		
		//time-set
		Element time_set = root.element("time-set");
		String startTime = time_set.elementTextTrim("start-time");
		if(startTime==null || startTime.length()==0){
			this.start = Tools.string2Calendar("2017-01-01 00:00:00");
		}
		else{
			this.start = Tools.string2Calendar(startTime);
		}
		String endTime = time_set.elementTextTrim("end-time");
		if(endTime==null || endTime.length()==0){
			this.end = Tools.string2Calendar("2100-01-01 00:00:00");
		}
		else{
			this.end = Tools.string2Calendar(endTime);
		}
		
		//operation-select
		this.operations = new HashSet<String>();
		Element operation_select = root.element("operation-select");
		Iterator<Element> iterable = operation_select.elementIterator();
		while(iterable.hasNext()){
			this.operations.add(iterable.next().getTextTrim());
		}
		
		//fields-corresponding  ---->  [(operation_root)->(tagName->valfield)]
		this.fieldsCorresponding = new HashMap<String, HashMap<String,String>>();
		Element fields_corresponding = root.element("fields-corresponding");
		for(Iterator<Element> iterator = fields_corresponding.elementIterator();iterator.hasNext();){
			Element table = iterator.next();
			String operation = table.getName()+"_"+table.elementTextTrim("root");//operation_rootName
			HashMap<String, String> map = new HashMap<String, String>();
			Element fields = table.element("fields");
			for(Iterator<Element> iter = fields.elementIterator();iter.hasNext();){
				Element element = iter.next();
				String tagName = element.getName();
				String valfield= element.getTextTrim();
				map.put(tagName, valfield);
			}
			this.fieldsCorresponding.put(operation, map);
		}
	}
}



































