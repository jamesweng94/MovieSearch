package xml_parsing;

public class SAXParser {

    public static void main(String[] args) throws Exception  {
        long movieStart;
        long movieEnd;
        long starStart;
        long starEnd;
        
    	MovieParser m_parser = new MovieParser();
        movieStart = System.currentTimeMillis(); 
        m_parser.run();
        movieEnd = System.currentTimeMillis(); 
        System.out.println("Time in Seconds for Movie Parser: " + ((movieEnd - movieStart) / 1000.0));
        
        StarParser star_parser = new StarParser();
        starStart = System.currentTimeMillis();
        star_parser.run();
        starEnd =System.currentTimeMillis();
        System.out.println("Time in Seconds for Star Parser: " + ((starEnd - starStart) / 1000.0));
    }
}
