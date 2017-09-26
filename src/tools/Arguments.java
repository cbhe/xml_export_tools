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
	public Calendar start;//要导出的起始时间
	public Calendar end;//结束时间
	public HashMap<String, Object[]> fieldsCorresponding;//字段匹配表，下面初始化的地方有结构分析
	public String ip;//
	public String port;//
	public String collectionName;//
	public String databaseName;//
	public Set<String> operations;//注意：都是大写字母，例如 U A S D C
	public String exportPath;//只包括文件夹的路径名称，具体文件名称在导出时刻动态生成
	
	public Arguments(String configFilePath){
		this.configFilePath = configFilePath;
		try {
			initAttrs();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	//parse xet_properties.xml for initialization
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
		
		//if no start time, set it by default, export all records before end time.
		if(startTime==null || startTime.length()==0){
			this.start = Tools.string2Calendar("2017-01-01 00:00:00");
		}
		else{
			this.start = Tools.string2Calendar(startTime);
		}
		
		//if no end time, set it by default, export all records from start time to current time. 
		String endTime = time_set.elementTextTrim("end-time");
		if(endTime==null || endTime.length()==0){
			this.end = Tools.string2Calendar("2100-01-01 00:00:00");
		}
		else{
			this.end = Tools.string2Calendar(endTime);
		}
		
		//path-set
		Element path_set = root.element("path-set");
		Element export_path = path_set.element("export-path");
		this.exportPath = export_path.getStringValue();
		
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
		/*
		 * data struct disassemble (inner to outer, total 4 levels)
		 * level#1: String[], is a String[2] contains tagName and valField, for example: {"DATAID", "dataid"}
		 * level#2: ArrayList<String[]>, for example: [{"DATAID", "dataid"}, {"SRC_NAME", "name"}, ...]
		 * level#3: Object[], is {rootName, ArrayList}, {"POI", [{"DATAID", "dataid"}, {"SRC_NAME", "name"}, ...]}
		 * level#4: HashMap<String, Object[]>, operation -> Object[]:
		 * 		      {"A" -> {"POI", [{"DATAID", "dataid"}, {"SRC_NAME", "name"}, ...]}}
		 */
		Element fields_corresponding = root.element("fields-corresponding");
		Iterator<Element> iterator = fields_corresponding.elementIterator();
		while (iterator.hasNext()) {
			//For reading those codes, please refer to xet_properties.xml 
			Element table = iterator.next();
			
			String operation = table.getName();//level#4. operation
			String rootName  = table.elementTextTrim("root-name");//level#3. rootName
			
			ArrayList<String[]> list = new ArrayList<String[]>(); //level#2. ArrayList
			
			Element fields = table.element("fields");
			
			Iterator<Element> fieldIter = fields.elementIterator();
			while (fieldIter.hasNext()) {
				Element element = fieldIter.next();
				
				//level#1:
				String[] tagName_valField = new String[2];//level#1.String[]
				tagName_valField[0] = element.getName();
				tagName_valField[1] = element.getTextTrim();
				
				//level#1 => level#2
				list.add(tagName_valField);
			}

			//level#2 => level#3
			Object[] arr = {rootName, list};//level 3. Object[]
			
			//level#3 => level#4
			this.fieldsCorresponding.put(operation, arr);
		}
	}
	
	/**
	 * 通过命令行参数来修改配置中的开始时间、结束时间和要导出的操作
	 * 
	 * @param args //命令行参数
	 */
	public void modifyByCmd(String[] args) {
		if(args == null || args.length == 0) {
			return ;
		}
				
		//Use apache commons cli <http://commons.apache.org/proper/commons-cli/usage.html>
		Options options = new Options();
		//--start 或者 -s 后接开始时间
		options.addOption("s", "start", true, "start time, ex. 2017-05-21 20:30:10");
		//--end 或者 -e 后接结束时间
		options.addOption("e", "end", true, "end time, ex. 2017-05-21 20:30:10");
		//--operations 或者 -o 后接要导出的操作(全部大写)
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



































