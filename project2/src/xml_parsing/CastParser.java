package xml_parsing;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;
import java.sql.*; 

public class CastParser extends DefaultHandler {
	private String tempVal;
	private String tempKey;
    private ArrayList<String> stageNames;    
    private HashMap<String, ArrayList<String>> movieMap;
    
    public CastParser() {
    	movieMap = new HashMap<String, ArrayList<String>>();
    	
    }
    
    public void run() throws Exception{
    	
    	parseDocument();
    	printData();
    }
    
    private void parseDocument() {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            SAXParser sp = spf.newSAXParser();
            sp.parse("./stanford-movies/casts124.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }
    
    private void printData() {
    	for (Map.Entry<String, ArrayList<String>> entry : movieMap.entrySet()) {
    	    String key = entry.getKey();
    	    ArrayList<String> value = entry.getValue();
    	    /*
    	    for(String aString : value){
    	        System.out.println("key : " + key + " value : " + aString);
    	    }
    	    */
    	}
    }
    
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
    	if(qName.equalsIgnoreCase("filmc")) {
    		stageNames = new ArrayList<String>();
    	}
    }
    
    public void characters(char ch[], int start, int length) throws SAXException {
		tempVal = new String(ch, start, length).trim();
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
    	if(qName.equalsIgnoreCase("filmc")) {
    		movieMap.put(tempKey, stageNames);
    	}
    	else if(qName.equalsIgnoreCase("f")) {
    		tempKey = tempVal;
    	}
    	else if(qName.equalsIgnoreCase("a")) {
    		stageNames.add(tempVal);
    	}
    }
    
    public Set<String> getKeyIDs(String value) {
    	Set<String> keys = new HashSet<String>();
        for(Map.Entry<String, ArrayList<String>> entry: movieMap.entrySet()){
    	    ArrayList<String> stageNames = entry.getValue();
    	    for(String aString : stageNames){
                if(value.equals(aString)){
                    keys.add(entry.getKey()); //no break, looping entire hashtable
                } 	    
            }
        }
        
        /*
       for(String s : keys) {
    	   System.out.println(s);
       }
       */
        
        return keys;    
    }
}
