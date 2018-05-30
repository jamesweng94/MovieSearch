import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;



@WebServlet(name = "MovieList", urlPatterns = "/api/list")
public class MovieList extends HttpServlet {
    private static final long serialVersionUID = 1L;
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); 
        PrintWriter out = response.getWriter();
		
		String action = request.getParameter("action");
        System.out.println("Action: " + action);
        
        try {
        	
        	Connection dbcon = dataSource.getConnection();
			Statement statement = dbcon.createStatement();
			JsonArray jsonArray = new JsonArray();


			String query = "";
			PreparedStatement preparedStatement = dbcon.prepareStatement("");
			
			// BROWSING
			if (action.equals("browse")) {
				
				String browseby = request.getParameter("by");
				String value = request.getParameter("value");
				
				if (browseby.equals("genre")) {
					
					query = "SELECT M.id, M.title, M.year, M.director, GROUP_CONCAT(DISTINCT G.name SEPARATOR ', ') AS genres, GROUP_CONCAT(DISTINCT S.name SEPARATOR', ') AS stars, R.rating "+
							"FROM (SELECT M.id FROM genres_in_movies GM INNER JOIN genres G ON G.id = GM.genreId AND G.name = ? LEFT JOIN movies M ON M.id = GM.movieId) AS T " +
							"LEFT JOIN movies M ON M.id = T.id "+
							"LEFT JOIN stars_in_movies SM ON SM.movieId = M.id "+
							"LEFT JOIN stars S ON S.id = SM.starId "+
							"LEFT JOIN genres_in_movies GM ON GM.movieId = T.id "+
							"LEFT JOIN genres G ON G.id = GM.genreId "+
							"LEFT JOIN ratings R ON R.movieId = M.id "+
							"GROUP BY M.id, M.title, M.year, M.director, R.rating LIMIT 500;"; 
					preparedStatement = dbcon.prepareStatement(query);
					preparedStatement.setString(1, value);
				}

				else {
					
					query = "SELECT M.id, M.title, M.year, M.director, GROUP_CONCAT(DISTINCT G.name SEPARATOR ', ') AS genres, GROUP_CONCAT(DISTINCT S.name SEPARATOR', ') AS stars, R.rating " + 
							"FROM (SELECT M.id FROM movies M WHERE M.title LIKE ? ) AS T " +
							"LEFT JOIN movies M ON M.id = T.id " + 
							"LEFT JOIN stars_in_movies SM ON SM.movieId = M.id " +
							"LEFT JOIN stars S ON S.id = SM.starId " +
							"LEFT JOIN genres_in_movies GM ON GM.movieId = T.id " +
							"LEFT JOIN genres G ON G.id = GM.genreId " +
							"LEFT JOIN ratings R ON R.movieId = M.id " +
							"GROUP BY M.id, M.title, M.year, M.director, R.rating LIMIT 500";
					preparedStatement = dbcon.prepareStatement(query);
					preparedStatement.setString(1, value + "%");
				}
			}
			
			
			// SEARCHING
			else {

				String title = request.getParameter("title");
				String year = request.getParameter("year");
				String director = request.getParameter("director");
				String star = request.getParameter("star");
				
				
				query = "SELECT DISTINCT M.id, M.title, M.year, M.director, GROUP_CONCAT(DISTINCT G.name SEPARATOR ', ') AS genres, GROUP_CONCAT(DISTINCT S.name SEPARATOR ', ') AS stars, R.rating\n" + 
						"FROM (	SELECT M.id\n" + 
						" 	FROM stars_in_movies SM \n" + 
						"	INNER JOIN stars S ON S.id = SM.starId AND S.name LIKE ?\n" + 
						"	LEFT JOIN movies M ON M.id = SM.movieId ) AS T \n" + 
						"LEFT JOIN movies M ON M.id = T.id\n" + 
						"LEFT JOIN stars_in_movies SM ON M.id = SM.movieId\n" + 
						"LEFT JOIN stars S ON SM.starId = S.id\n" + 
						"LEFT JOIN genres_in_movies GM ON M.id = GM.movieId\n" + 
						"LEFT JOIN genres G ON GM.genreId = G.id\n" + 
						"LEFT JOIN ratings R ON M.id = R.movieId\n" + 
						"WHERE M.title LIKE ? AND M.year LIKE ? AND M.director LIKE ?\n" + 
						"GROUP BY M.id, M.title, M.year, M.director, R.rating LIMIT 500;\n";
				
				preparedStatement = dbcon.prepareStatement(query);
				preparedStatement.setString(1, "%" + star + "%");
				preparedStatement.setString(2, "%" + title + "%");
				preparedStatement.setString(3, "%" + year + "%");
				preparedStatement.setString(4, "%" + director + "%");
			}
           
            ResultSet rs = preparedStatement.executeQuery();

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

            out.write(jsonArray.toString());
            response.setStatus(200);

            rs.close();
            statement.close();
            dbcon.close();
            
        } catch (Exception e) {

			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
			response.setStatus(500);

        }
        out.close();

    }
}