package xml_parsing;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

public class StarParser extends DefaultHandler {
	
    public final static char MIN_DIGIT = '0';
    public final static char MAX_DIGIT = '9';
    public final static char MIN_LETTER = 'A';
    public final static char MAX_LETTER = 'Z';
    
	List<star> starList;	
	private String tempVal;
    private star starData;
    CastParser cast_parser;
    
    public StarParser() throws Exception {
    	starList = new ArrayList<star>();
    	cast_parser = new CastParser();
    }
    
    public void run() throws Exception{
    	cast_parser.run();
    	parseDocument();
    	printData();
    	insertStar();
    }
    
    private void parseDocument() {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            SAXParser sp = spf.newSAXParser();
            sp.parse("./stanford-movies/actors63.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    public String incrementedAlpha(String original) {
        StringBuilder buf = new StringBuilder(original);
        int i = buf.length() - 1;
        while (i >= 0) {
            char c = buf.charAt(i);
            c++;
            if ((c - 1) >= MIN_LETTER && (c - 1) <= MAX_LETTER) {
                if (c > MAX_LETTER) { // overflow, carry one
                    buf.setCharAt(i, MIN_LETTER);
                    i--;
                    continue;
                }

            } else {
                if (c > MAX_DIGIT) { 
                    buf.setCharAt(i, MIN_DIGIT);
                    i--;
                    continue;
                }
            }
            buf.setCharAt(i, c);
            return buf.toString();
        }
        buf.insert(0, MIN_DIGIT);
        return buf.toString();
    }
    
    private void printData() throws Exception {
    	PrintWriter starWriter = null;
    	PrintWriter starIn = null;
    	try {
    		starWriter = new PrintWriter(new File("stars.csv"));
    		starIn = new PrintWriter(new File("stars_in_movies.csv"));
    		
    	} catch (FileNotFoundException e) {
    	    e.printStackTrace();
    	}
    	
    	String max_id = getMaxId();
    	System.out.println("max ID: " + max_id);
        System.out.println("No of actors '" + starList.size() + "'.");        
        Iterator<star> it = starList.iterator();
        while (it.hasNext()) {
        	max_id = incrementedAlpha(max_id);
            starWriter.write(max_id + "," + it.next().toString());
            try {
        	Set<String> movieIDs = cast_parser.getKeyIDs(it.next().getStageName());
        	for(String a : movieIDs) {
        		starIn.write(max_id + "," + a + "\n");
        	}
            }catch(Exception e) {
            	System.out.println("Stagename not in actor xml file");
            }
        }
    	
        starWriter.close();
        starIn.close();
    }
    
    private boolean isValidYear(String year) {
        for (int i = 0; i < year.length(); i++){
            if (!Character.isDigit(year.charAt(i)))
                return false;
        }
        return true;
    }
    
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
    	if(qName.equalsIgnoreCase("actor")) {
    		starData = new star();
    	}
    }
    
    public void characters(char ch[], int start, int length) throws SAXException {
		tempVal = new String(ch, start, length).trim();
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
    	if(qName.equalsIgnoreCase("actor")) {
    		if(starData.getFirstName().trim().length() == 0 || starData.getLastName().trim().length()  == 0 || starData.getStageName().trim().length() == 0) {
    			return;
    		}
    		starList.add(starData);
    	}
    	else if(qName.equalsIgnoreCase("stagename")) {
    		starData.setStageName(tempVal);	
    	}
    	else if(qName.equalsIgnoreCase("familyname")) {
    		starData.setLastName(tempVal);
    	}
    	else if(qName.equalsIgnoreCase("firstname")) {
    		starData.setFirstName(tempVal);
    	}
    	
    	else if(qName.equalsIgnoreCase("dob")) {
    		if(tempVal.trim().length()== 0) {
    			return;
    		}
    		if(isValidYear(tempVal)) {
    			try {
    				starData.setBirthYear(Integer.parseInt(tempVal));
    			}catch(NumberFormatException ex){ 
    				return;
    			}
    		}
    	} 	
    }
    
    public void insertStar() throws Exception{
    	String loginUser = "james";
    	String loginPasswd = "mypassword";
    	String loginUrl = "jdbc:mysql://13.58.209.21:3306/moviedb";

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        Statement statement = connection.createStatement();
        
        
        //updating star table
        
        String starPath = "./stars.csv";
        String updateStar = " LOAD DATA LOCAL INFILE '" + starPath +
            	"' INTO TABLE stars " +
            	" FIELDS TERMINATED BY ','" +
            	" LINES TERMINATED BY \'\\n\'";
        statement.executeUpdate(updateStar);
        System.out.println("updating star table completed");
        
        //update star_in_movie table
        String starInPath = "./stars.csv";
        String updateStarIn = " LOAD DATA LOCAL INFILE '" + starInPath +
            	"' INTO TABLE stars " +
            	" FIELDS TERMINATED BY ','" +
            	" LINES TERMINATED BY \'\\n\'";
        statement.executeUpdate(updateStarIn);
        System.out.println("updating stars_in_movies table completed");  
        
        statement.close();
    }
    
    public String getMaxId() throws Exception{
    	/*
    	String loginUser = "mytestuser";
    	String loginPasswd = "mypassword";
    	String loginUrl = "jdbc:mysql://localhost/moviedb";
    	*/
    	String loginUser = "james";
    	String loginPasswd = "mypassword";
    	String loginUrl = "jdbc:mysql://13.58.209.21:3306/moviedb";

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        Statement statement = connection.createStatement();
        String query = "SELECT max(id) as m FROM stars;";
        ResultSet rs = statement.executeQuery(query);
        
        String maxid = null;
        if(rs.next())
        {
        	 maxid = rs.getString("m");
        }
        rs.close();
        statement.close();
        return maxid;
    }
}
