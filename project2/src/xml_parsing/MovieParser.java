package xml_parsing;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
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
import java.util.HashMap;
import java.util.HashSet;

public class MovieParser extends DefaultHandler{
	
	
	List<MovieData> movie_data;	
	private String tempVal;
    private MovieData tempData;
    private HashMap<String, String> genresMap;
    private HashSet<String> existGenres;
    private HashSet<String> newGenres;
    private HashMap<String, String> genres_in_movie;
    private HashMap<String, Integer> updatedGenre;
	PrintWriter inc_report = null;
	
    public MovieParser() throws Exception{
    	movie_data = new ArrayList<MovieData>();
    	genresMap = new HashMap<String, String>();
    	existGenres = new HashSet<String>();
    	newGenres = new HashSet<String>();
    	genres_in_movie = new HashMap<String, String>();
    	updatedGenre = new HashMap<String, Integer>();
    	inc_report = new PrintWriter(new File("inc_report.txt"));
    }
    
    public void genresInit() throws Exception{
    	
    	// connect db
    	String loginUser = "james";
    	String loginPasswd = "mypassword";
    	String loginUrl = "jdbc:mysql://13.58.209.21:3306/moviedb";

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        
    	// genres conversion
    	genresMap.put("Susp", "Thriller");
    	genresMap.put("CnRb", "Cops and Robbers");
    	genresMap.put("Dram", "Drama");
    	genresMap.put("West", "Western");
    	genresMap.put("Myst", "Mystery");
    	genresMap.put("S.F.", "Sci-Fi");
    	genresMap.put("Advt", "Adventure");
    	genresMap.put("Horr", "Horror");
    	genresMap.put("Romt", "Romance");
    	genresMap.put("Comd", "Comedy");
    	genresMap.put("Musc", "Musical");
    	genresMap.put("Docu", "Documentary");
    	genresMap.put("Porn", "Adult");
    	genresMap.put("Noir", "Black");
    	genresMap.put("BioP", "Biographical Picture");
    	genresMap.put("TV", "TV Show");
    	genresMap.put("TVs", "TV Series");
    	genresMap.put("TVmini", "TV Miniseries"); 	
    	
    	//get all exist genres
    	Statement statement = connection.createStatement();
        String getAllGenres = "SELECT * FROM genres; ";
        ResultSet allGenres = statement.executeQuery(getAllGenres);
        while (allGenres.next()) {
        	existGenres.add(allGenres.getString("name"));
        }
        allGenres.close();
        
        
    }
    
    public void run() throws Exception{
    	genresInit();
    	parseDocument();
    	printData();
    	insertMovie();
    	inc_report.close();
    }
    
    private void parseDocument() {
        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            SAXParser sp = spf.newSAXParser();
            sp.parse("./stanford-movies/mains243.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }
    
    private void printData() {
    	
    	PrintWriter pw = null;
    	PrintWriter genresWriter = null;
    	
    	try {
    	    pw = new PrintWriter(new File("movies.csv"));
    	    genresWriter = new PrintWriter(new File("genres.csv"));
    	} catch (FileNotFoundException e) {
    	    e.printStackTrace();
    	}
    	
        System.out.println("No of Movie Data '" + movie_data.size() + "'.");
        Iterator<MovieData> it = movie_data.iterator();
        while (it.hasNext()) {
            pw.write(it.next().toString());
        }
        
        for(String item: newGenres) {
        	genresWriter.write(item + ",\n");
        }
        
        pw.close();
        genresWriter.close();
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
    	if(qName.equalsIgnoreCase("film")) {
    		tempData = new MovieData();
    	}
    }
    
    public void characters(char ch[], int start, int length) throws SAXException {
    		tempVal = new String(ch, start, length).trim();
    }
    
    public void endElement(String uri, String localName, String qName) throws SAXException {
    	if(qName.equalsIgnoreCase("film")) {
    		movie_data.add(tempData);
    		if(tempData.getGenres() != null) {
    			genres_in_movie.put(tempData.getMovieID(),tempData.getGenres());
    		}
    	} else if(qName.equalsIgnoreCase("fid")) {
    		tempData.setMovieID(tempVal);
    	} else if(qName.equalsIgnoreCase("t")) {
    		tempData.setMovieTitle(tempVal);
    	} else if(qName.equalsIgnoreCase("year")) {
    		if(isValidYear(tempVal)) {
    			tempData.setYear(Integer.parseInt(tempVal));
    		}else {
    			inc_report.write("Inconsistent year: " + tempVal + "\n");
    			tempData.setYear(0);
    		}
    	} else if(qName.equalsIgnoreCase("dirn")) {
    		tempData.setDirector(tempVal);
    	} else if(qName.equalsIgnoreCase("cat")) {
    		if(tempVal.equals("Ctxx")) {
    			inc_report.write("Error Genres: " + tempVal + "\n");
    			tempData.setGenres(null);
    		}else{
    			String genresTemp = genresMap.get(tempVal);
    			inc_report.write("Inconsistent genres: " + tempVal + " convert to " + genresTemp +"\n");
    			tempData.setGenres(genresTemp);
    			if(!existGenres.contains(genresTemp) && (genresTemp != null)) {
    				newGenres.add(genresTemp);
    			}
    		}
    	}	
    }
    
    public void insertMovie() throws Exception{
    	String loginUser = "james";
    	String loginPasswd = "mypassword";
    	String loginUrl = "jdbc:mysql://13.58.209.21:3306/moviedb";

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        Statement statement = connection.createStatement();

        
        //movie table update
        String uniqueKey = "ALTER TABLE movies \n" + 
				"ADD UNIQUE KEY (title , year, director) ;";
        statement.executeUpdate(uniqueKey);
        
        String moviePath = "./movies.csv";
        String query = " LOAD DATA LOCAL INFILE '" + moviePath +
                	"' INTO TABLE movies " +
                	" FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY \'\"'" +
                	" LINES TERMINATED BY \'\\n\'";
        statement.executeUpdate(query);
        System.out.println("updating movie table completed");
        
        
        // genres update
        String genresUniqueKey = "ALTER TABLE genres \n" + 
				"ADD UNIQUE KEY (id, name) ;";
        statement.executeUpdate(genresUniqueKey);

        String genrePath = "./genres.csv";
        String updateGenres = " LOAD DATA LOCAL INFILE '" + genrePath +
                	"' INTO TABLE genres " +
                	" FIELDS TERMINATED BY ','" +
                	" LINES TERMINATED BY \'\\n\'" + 
                	"(name)" + 
                	"SET id = NULL;";
        statement.executeUpdate(updateGenres);
        System.out.println("updating genres table completed");       
        
        // genres_in_movie update
        String getAllGenres = "SELECT * FROM genres; ";
        ResultSet allGenres = statement.executeQuery(getAllGenres);
        while (allGenres.next()) {
        	updatedGenre.put(allGenres.getString("name"), Integer.parseInt(allGenres.getString("id")));
        }
        allGenres.close();
        
    	PrintWriter genres_in = null;
	    genres_in = new PrintWriter(new File("genres_in_movies.csv"));
        for (Entry<String, String> entry : genres_in_movie.entrySet()) {
      	  String key = entry.getKey();
      	  String value = entry.getValue();
      	  int genreID = updatedGenre.get(value);
      	  genres_in.write(genreID + ","+ key+"\n");
      	}
        String genresInUniqueKey = "ALTER TABLE genres_in_movies \n" + 
				"ADD UNIQUE KEY (genreId, movieId) ;";
        statement.executeUpdate(genresInUniqueKey);

        String genreInPath = "./genres_in_movies.csv";
        String updateGenresIn = " LOAD DATA LOCAL INFILE '" + genreInPath +
                	"' INTO TABLE genres_in_movies " +
                	" FIELDS TERMINATED BY ','" +
                	" LINES TERMINATED BY \'\\n\'";
        statement.executeUpdate(updateGenresIn);
        System.out.println("updating genres_in_movies table completed"); 
        
        
        System.out.println("updating db completed");
        genres_in.close();
        statement.close();
    }    
}
