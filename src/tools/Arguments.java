package tools;

import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.text.ElementIterator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

public class Arguments {
	private String configFilePath;
	public Calendar start;
	public Calendar end;
	public HashMap<String, Object[]> fieldsCorresponding;
	public String ip;
	public String port;
	public String collectionName;
	public String databaseName;
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
		this.databaseName = connection_set.elementTextTrim("database-name");
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
		
		//fields-corresponding  ---->  {(operation),[rootName, {(tagName,valField)}]}
		//map(operation -> arr[2]{rootName, ArrayList{(tagName, valField)}})
		this.fieldsCorresponding = new HashMap<String, Object[]>();
		Element fields_corresponding = root.element("fields-corresponding");
		for(Iterator<Element> iterator = fields_corresponding.elementIterator();iterator.hasNext();){
			String operation;
			Object[] arr = new Object[2];
			String rootName;
			ArrayList<String[]> list = new ArrayList<String[]>();
			
			Element table = iterator.next();
			operation = table.getName();
			rootName  = table.elementTextTrim("root-name");
			Element fields = table.element("fields");
			for(Iterator<Element> iter = fields.elementIterator();iter.hasNext();){
				Element element = iter.next();
				String tagName = element.getName();
				String valField = element.getTextTrim();
				list.add(new String[]{tagName, valField});
			}
			arr[0] = rootName;
			arr[1] = list;
			this.fieldsCorresponding.put(operation, arr);
		}
	}
	public void modifyByCmd(String[] args) {
		if(args == null || args.length == 0) {
			return ;
		}
				
		//Use apache commons cli <http://commons.apache.org/proper/commons-cli/usage.html>
		Options options = new Options();
		options.addOption("s", "start", true, "start time, ex. 2017-05-21 20:30:10");
		options.addOption("e", "end", true, "end time, ex. 2017-05-21 20:30:10");
		options.addOption("o", "operations", true, "the operations selected to export, ex. AUCDS");
		
		CommandLineParser parser = new DefaultParser();
		CommandLine commandLine = null;
		try{
			commandLine = parser.parse(options, args);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		if (commandLine.hasOption("s") && commandLine.hasOption("e")) {
			String[] tmp;
			String startStr = commandLine.getOptionValue("s");
			String endStr = commandLine.getOptionValue("e");
			startStr = (tmp = startStr.split("_"))[0] + " " + tmp[1];
			endStr   = (tmp = endStr.split("_"))[0]   + " " + tmp[1];
			this.start = startStr == null ? Tools.string2Calendar("2017-01-01 00:00:00")
										  : Tools.string2Calendar(startStr);
			this.end   = endStr   == null ? Tools.string2Calendar("2100-01-01 00:00:00")
					                      : Tools.string2Calendar(endStr);
		}
		
		if (commandLine.hasOption("o")) {
			String opts = commandLine.getOptionValue("o");
			this.operations.clear();
			for(char c: opts.toCharArray()) {
				this.operations.add(String.valueOf(c));
			}
		}
	}
}



































