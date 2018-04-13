
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/MovieServlet")
public class MovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public MovieServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // change this to your own mysql username and password
        String loginUser = "james";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://13.58.209.21:3306/moviedb";
		
        // set response mime type
        response.setContentType("text/html"); 

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head><title>Fabflix</title></head>");
        
        
        try {
        		Class.forName("com.mysql.jdbc.Driver").newInstance();
        		// create database connection
        		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        		// declare statement
        		Statement statement = connection.createStatement();
        		// prepare query
        		String query = "SELECT M.title, M.year, M.director, G.name AS genres, S.name as stars, R.rating "
        				+ "FROM movies M LEFT JOIN stars_in_movies SM ON M.id = SM.movieId "
        				+ "LEFT JOIN stars S ON SM.starId = S.id "
        				+ "LEFT JOIN genres_in_movies RM ON M.id = RM.movieId "
        				+ "LEFT JOIN genres G ON RM.genreId = G.id "
        				+ "LEFT JOIN ratings R ON M.id = R.movieId "
        				+ "ORDER BY R.rating DESC LIMIT 50";
        		// execute query
        		ResultSet resultSet = statement.executeQuery(query);

        		out.println("<body>");
        		out.println("<h1>Movies List</h1>");
        		
        		out.println("<table border>");
        		
        		// add table header row
        		out.println("<tr>");
        		out.println("<td>title</td>");
        		out.println("<td>year</td>");
        		out.println("<td>director</td>");
        		out.println("<td>genres</td>");
        		out.println("<td>stars</td>");
        		out.println("<td>rating</td>");
        		out.println("</tr>");
        		
        		// add a row for every star result
        		while (resultSet.next()) {
        			// get a star from result set
        			String movieTitle = resultSet.getString("title");
        			String movieYear = resultSet.getString("year");
        			String movieDirector = resultSet.getString("director");
        			String movieGenres = resultSet.getString("genres");
        			String movieStars = resultSet.getString("stars");
        			String movieRating = resultSet.getString("rating");
        			
        			out.println("<tr>");
        			out.println("<td>" + movieTitle + "</td>");
        			out.println("<td>" + movieYear + "</td>");
        			out.println("<td>" + movieDirector + "</td>");
        			out.println("<td>" + movieGenres + "</td>");
        			out.println("<td>" + movieStars + "</td>");
        			out.println("<td>" + movieRating + "</td>");
        			out.println("</tr>");
        		}
        		
        		out.println("</table>");
        		
        		out.println("</body>");
        		
        		resultSet.close();
        		statement.close();
        		connection.close();
        		
        } catch (Exception e) {
        		/*
        		 * After you deploy the WAR file through tomcat manager webpage,
        		 *   there's no console to see the print messages.
        		 * Tomcat append all the print messages to the file: tomcat_directory/logs/catalina.out
        		 * 
        		 * To view the last n lines (for example, 100 lines) of messages you can use:
        		 *   tail -100 catalina.out
        		 * This can help you debug your program after deploying it on AWS.
        		 */
        		e.printStackTrace();
        		
        		out.println("<body>");
        		out.println("<p>");
        		out.println("Exception in doGet: " + e.getMessage());
        		out.println("</p>");
        		out.print("</body>");
        }
        
        out.println("</html>");
        out.close();
        
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
