import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import mongodb.Connection;
import tools.Arguments;

public class Test {
	public static void main(String...args){
		String path = "c:\\xet_properties.xml";
		Arguments arguments = new Arguments(path);
	}
}
