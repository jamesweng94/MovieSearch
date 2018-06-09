import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SingleMovieServlet
 */
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;
	
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json"); // Response mime type

		String name = request.getParameter("name");

		PrintWriter out = response.getWriter();
	
		
		try {
				Context initCtx = new InitialContext();
	
	            Context envCtx = (Context) initCtx.lookup("java:comp/env");
	            if (envCtx == null)
	                out.println("envCtx is NULL");
	
	            dataSource = (DataSource) envCtx.lookup("jdbc/TestDB");
	            
			 	Connection dbcon = dataSource.getConnection();
			 	
			 	if (dbcon == null)
	                out.println("dbcon is null.");

	            String query = "SELECT DISTINCT M.id, M.title, M.year, M.director, GROUP_CONCAT(DISTINCT G.name SEPARATOR ', ') AS genres , GROUP_CONCAT(DISTINCT S.name SEPARATOR ', ') AS stars, R.rating \n" + 
	            		"FROM movies M \n" + 
	            		"LEFT JOIN stars_in_movies SM ON M.id = SM.movieId \n" + 
	            		"LEFT JOIN stars S ON SM.starId = S.id\n" + 
	            		"LEFT JOIN genres_in_movies RM ON M.id = RM.movieId \n" + 
	            		"LEFT JOIN genres G ON RM.genreId = G.id\n" + 
	            		"LEFT JOIN ratings R ON M.id = R. movieId \n" + 
	            		"WHERE  M.title = ? \n" +  
	            		"GROUP BY M.id, M.title, M.year, M.director, R.rating;";


	            PreparedStatement preparedStatement = dbcon.prepareStatement(query);
	            preparedStatement.setString(1, name);
	            ResultSet rs = preparedStatement.executeQuery();

	            JsonArray jsonArray = new JsonArray();

	            // Iterate through each row of rs
	            while (rs.next()) {
					String movieId = rs.getString("id");
	    			String movieTitle = rs.getString("title");
	    			String movieYear = rs.getString("year");
	    			String movieDirector = rs.getString("director");
	    			String movieGenres = rs.getString("genres");
	    			String movieStars = rs.getString("stars");
	    			String movieRating = rs.getString("rating");


	    			String [] stars = null;
	    			if (movieStars != null)
	    				stars = movieStars.split(", ");
	    			
	    			String [] genres_tokens = null;
	    			if (movieGenres != null)
	    				genres_tokens = movieGenres.split(", ");
	    			
	    			
	    			JsonArray star_array = new JsonArray();
	    			JsonArray generes_array = new JsonArray();
	    			
	    			
	    			if (stars != null) {
		    			for (int i = 0; i < stars.length; ++i) {
		    			        star_array.add(stars[i]);
		    			}
	    			}
	    			
	    			if (genres_tokens != null) {
		    			for (int i = 0; i < genres_tokens.length; ++i) {
		    				generes_array.add(genres_tokens[i]);
		    			}
	    			}
	    			
	                // Create a JsonObject based on the data we retrieve from rs
	                JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("movie_id", movieId);
					jsonObject.addProperty("movie_title", movieTitle);
					jsonObject.addProperty("movie_year", movieYear);
					jsonObject.addProperty("movie_dir", movieDirector);
					jsonObject.add("movie_genres", generes_array);
					jsonObject.add("movie_star", star_array);
					jsonObject.addProperty("movie_rating", movieRating);

	                jsonArray.add(jsonObject);
	            }
	            
	            // write JSON string to output
	            out.write(jsonArray.toString());
	            // set response status to 200 (OK)
	            response.setStatus(200);

	            rs.close();
	            dbcon.close();			
			

		}catch (Exception e) {
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
		}
		out.close();
	}
}