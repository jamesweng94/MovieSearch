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
import java.util.ArrayList;
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
		
		
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        
      //  System.out.println("target:" + target);
       // System.out.println("genres:" + findGenres);
        
        try {
        	
        	Connection dbcon = dataSource.getConnection();
			Statement statement = dbcon.createStatement();
			JsonArray jsonArray = new JsonArray();
			
			int current_page = Integer.parseInt(request.getParameter("page"));
			int current_limit = Integer.parseInt(request.getParameter("limit"));

			System.out.println("current page: " + current_page);
			System.out.println("current limit: " + current_limit);

			
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
							"GROUP BY M.id, M.title, M.year, M.director, R.rating LIMIT 500;"; }
	//						"GROUP BY M.id, M.title, M.year, M.director, R.rating LIMIT " + (current_page - 1) * current_limit + ", " + current_limit+";"; }
				else {
					
					query = "SELECT M.id, M.title, M.year, M.director, GROUP_CONCAT(DISTINCT G.name SEPARATOR ', ') AS genres, GROUP_CONCAT(DISTINCT S.name SEPARATOR', ') AS stars, R.rating\n" + 
							"FROM (SELECT M.id FROM movies M WHERE M.title LIKE '"+ value + "%') AS T\n" +
							"LEFT JOIN movies M ON M.id = T.id\n" + 
							"LEFT JOIN stars_in_movies SM ON SM.movieId = M.id\n" +
							"LEFT JOIN stars S ON S.id = SM.starId\n" +
							"LEFT JOIN genres_in_movies GM ON GM.movieId = T.id\n" +
							"LEFT JOIN genres G ON G.id = GM.genreId\n" +
							"LEFT JOIN ratings R ON R.movieId = M.id\n" +
							"GROUP BY M.id, M.title, M.year, M.director, R.rating LIMIT 500";
				}
			}
			
			else {
				String search = request.getParameter("search");
				
				String[] params = new String[] {"title","year","director","star"};
				ArrayList<String> search_by = new ArrayList<>();
				
				for (int i = 0; i < params.length; ++i) {
					String param = request.getParameter(params[i]);
					if (param != null) {
						search_by.add(params[i]);
					}		
				}
				

				String query_limit = "WHERE ";
				String query_from = "FROM movies M ";
				
				if (search_by.size() == 0) {
					query_limit += "M.title LIKE '%" + search + "%' OR M.year LIKE '%" + search +
									"%' OR M.director LIKE '%" + search + "%'\n" ;
				}
				
				else {
					for (int i = 0; i < search_by.size(); ++i) {
						String p = search_by.get(i);
						
						if (p.equals("star")) {
							if (search_by.size() == 1)
								query_limit = "";
							query_from = "FROM (SELECT M.id FROM stars_in_movies SM "+ "INNER JOIN stars S ON S.id = SM.starId "+
										 		"AND S.name LIKE '%" + search + "%' " + "LEFT JOIN movies M ON M.id = SM.movieId) AS T \n" +
										 "LEFT JOIN movies M ON M.id = T.id \n";
						}
						else
							query_limit += "M." + p + " LIKE '%" + search + "%' ";
						if ((search_by.size() > 1) && (i < search_by.size()-1) && search_by.get(i+1).equals("star") == false) 
							query_limit += "AND ";
					}
				}
				
				query = "SELECT DISTINCT M.id, M.title, M.year, M.director, GROUP_CONCAT(DISTINCT G.name SEPARATOR ', ') AS genres, GROUP_CONCAT(DISTINCT S.name SEPARATOR ', ') AS stars, R.rating \n" + 
	            		query_from + 
	            		"LEFT JOIN stars_in_movies SM ON M.id = SM.movieId \n" + 
	            		"LEFT JOIN stars S ON SM.starId = S.id\n" + 
	            		"LEFT JOIN genres_in_movies RM ON M.id = RM.movieId \n" + 
	            		"LEFT JOIN genres G ON RM.genreId = G.id\n" + 
	            		"LEFT JOIN ratings R ON M.id = R.movieId \n" + 
	            		query_limit +
	            		"GROUP BY M.id, M.title, M.year, M.director, R.rating LIMIT 500";				
			}
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