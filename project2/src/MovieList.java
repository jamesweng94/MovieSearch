import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

@WebServlet(name = "MovieList", urlPatterns = "/api/list")
public class MovieList extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type
		//String target = request.getParameter("search");
		//String findGenres = request.getParameter("genres");
		
		String action = request.getParameter("action");
		System.out.println(action);
		
		
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        
      //  System.out.println("target:" + target);
       // System.out.println("genres:" + findGenres);
        
        try {
        	
        	Connection dbcon = dataSource.getConnection();
			Statement statement = dbcon.createStatement();
			JsonArray jsonArray = new JsonArray();
			
			String query = "";
			
			if (action.equals("browse")) {
				
				String browseby = request.getParameter("by");
				String value = request.getParameter("value");
				
				if (browseby.equals("genre")) {
					
					query = "SELECT M.id, M.title, M.year, M.director, GROUP_CONCAT(DISTINCT G.name SEPARATOR ', ') AS genres, GROUP_CONCAT(DISTINCT S.name SEPARATOR', ') AS stars, R.rating\n"+
							"FROM (SELECT M.id FROM genres_in_movies GM INNER JOIN genres G ON G.id = GM.genreId AND G.name = '"+value+"' LEFT JOIN movies M ON M.id = GM.movieId) AS T\n" +
							"LEFT JOIN movies M ON M.id = T.id\n"+
							"LEFT JOIN stars_in_movies SM ON SM.movieId = M.id\n"+
							"LEFT JOIN stars S ON S.id = SM.starId\n"+
							"LEFT JOIN genres_in_movies GM ON GM.movieId = T.id\n"+
							"LEFT JOIN genres G ON G.id = GM.genreId\n"+
							"LEFT JOIN ratings R ON R.movieId = M.id\n"+
							"GROUP BY M.id, M.title, M.year, M.director, R.rating;"; }
				else {
					
					query = "SELECT M.id, M.title, M.year, M.director, GROUP_CONCAT(DISTINCT G.name SEPARATOR ', ') AS genres, GROUP_CONCAT(DISTINCT S.name SEPARATOR', ') AS stars, R.rating\n" + 
							"FROM (SELECT M.id FROM movies M WHERE M.title LIKE '"+ value + "%') AS T\n" +
							"LEFT JOIN movies M ON M.id = T.id\n" + 
							"LEFT JOIN stars_in_movies SM ON SM.movieId = M.id\n" +
							"LEFT JOIN stars S ON S.id = SM.starId\n" +
							"LEFT JOIN genres_in_movies GM ON GM.movieId = T.id\n" +
							"LEFT JOIN genres G ON G.id = GM.genreId\n" +
							"LEFT JOIN ratings R ON R.movieId = M.id\n" +
							"GROUP BY M.id, M.title, M.year, M.director, R.rating\n";
				}
			}
			
			else {
				
				String target = request.getParameter("search");
				query = "SELECT DISTINCT M.id, M.title, M.year, M.director, GROUP_CONCAT(DISTINCT G.name SEPARATOR ', ') AS genres, GROUP_CONCAT(DISTINCT S.name SEPARATOR ', ') AS stars, R.rating \n" + 
	            		"FROM movies M \n" + 
	            		"LEFT JOIN stars_in_movies SM ON M.id = SM.movieId \n" + 
	            		"LEFT JOIN stars S ON SM.starId = S.id\n" + 
	            		"LEFT JOIN genres_in_movies RM ON M.id = RM.movieId \n" + 
	            		"LEFT JOIN genres G ON RM.genreId = G.id\n" + 
	            		"LEFT JOIN ratings R ON M.id = R. movieId \n" + 
	            		"WHERE M.title LIKE '%" + target + "%' OR\n" + 
	            		"M.year LIKE '%" + target + "%' OR\n" +
	            		"M.director LIKE '%" + target + "%'\n" + 
	            		"GROUP BY M.id, M.title, M.year, M.director, R.rating;";
				
			}
			
			
        	
        	/*
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Declare our statement
            Statement statement = dbcon.createStatement();

            String query = "SELECT DISTINCT M.id, M.title, M.year, M.director, GROUP_CONCAT(DISTINCT G.name SEPARATOR ', ') AS genres, GROUP_CONCAT(DISTINCT S.name SEPARATOR ', ') AS stars, R.rating \n" + 
            		"FROM movies M \n" + 
            		"LEFT JOIN stars_in_movies SM ON M.id = SM.movieId \n" + 
            		"LEFT JOIN stars S ON SM.starId = S.id\n" + 
            		"LEFT JOIN genres_in_movies RM ON M.id = RM.movieId \n" + 
            		"LEFT JOIN genres G ON RM.genreId = G.id\n" + 
            		"LEFT JOIN ratings R ON M.id = R. movieId \n" + 
            		"WHERE M.title LIKE '%" + target + "%' OR\n" + 
            		"M.year LIKE '%" + target + "%' OR\n" +
            		"M.director LIKE '%" + target + "%'\n" + 
            		"GROUP BY M.id, M.title, M.year, M.director, R.rating;";

            if(findGenres != null) {
            	query = "SELECT DISTINCT M.id, M.title, M.year, M.director, GROUP_CONCAT(DISTINCT G.name SEPARATOR ', ') AS genres , GROUP_CONCAT(DISTINCT S.name SEPARATOR ', ') AS stars,GROUP_CONCAT(DISTINCT S.id SEPARATOR ', ') AS starsID, R.rating\n" + 
            			"FROM movies M\n" + 
            			"LEFT JOIN stars_in_movies SM ON M.id = SM.movieId\n" + 
            			"LEFT JOIN stars S ON SM.starId = S.id\n" + 
            			"LEFT JOIN genres_in_movies RM ON M.id = RM.movieId\n" + 
            			"LEFT JOIN genres G ON RM.genreId = G.id\n" + 
            			"LEFT JOIN ratings R ON M.id = R. movieId\n" + 
            			"WHERE G.name = '"+findGenres +"'\n" + 
            			"GROUP BY M.id, M.title, M.year, M.director, R.rating LIMIT 400;";
            }
	*/
            // Perform the query
            ResultSet rs = statement.executeQuery(query);

           // JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
				String movieId = rs.getString("id");
    			String movieTitle = rs.getString("title");
    			String movieYear = rs.getString("year");
    			String movieDirector = rs.getString("director");
    			String movieGenres = rs.getString("genres");
    			String movieStars = rs.getString("stars");
    			String movieRating = rs.getString("rating");
    			
    			String [] stars = movieStars.split(", ");
    			String [] genres_tokens = movieGenres.split(", ");
    			
    			JsonArray star_array = new JsonArray();
    			JsonArray generes_array = new JsonArray();
    			
    			for (int i = 0; i < stars.length; ++i) {
    			        star_array.add(stars[i]);
    			}
    			for (int i = 0; i < genres_tokens.length; ++i) {
    				generes_array.add(genres_tokens[i]);
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
            statement.close();
            dbcon.close();
        } catch (Exception e) {
        	
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
