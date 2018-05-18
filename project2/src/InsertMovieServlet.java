import javax.servlet.annotation.WebServlet;
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
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "InsertMovieServlet", urlPatterns = "/api/insert-movie")
public class InsertMovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Resource(name = "jdbc/moviedb")
    private DataSource dataSource; 

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		
		String title = request.getParameter("title");
		String year = request.getParameter("year");
		String director = request.getParameter("director");
		String star = request.getParameter("star");
		String genre = request.getParameter("genre");
		
		
		
		System.out.println("Title: " + title);
		System.out.println("Year: " + year);
		System.out.println("Director: " + director);
		System.out.println("Star: " + star);
		System.out.println("Genre: " + genre);
		
		try { 
			
			if ((title != "") && (year != "") && (director != "") && (star != "") && (genre != ""))
			{
				Connection dbcon = dataSource.getConnection();
				Integer new_year = Integer.parseInt(year);
				
				// Check if movie already exists 
				String check_unique = "SELECT id FROM movies WHERE title = ? AND year = ? AND director = ?; ";
				PreparedStatement check_ps = dbcon.prepareStatement(check_unique);
				check_ps.setString(1, title);
				check_ps.setInt(2, new_year);
				check_ps.setString(3, director);
				ResultSet rs_check = check_ps.executeQuery();
				
				String unique_movie_id = "";
				while (rs_check.next())
					unique_movie_id = rs_check.getString("id");
				
				if (unique_movie_id == "") { // Movie does not exist
					System.out.println("Movie does not exist");
					// Handle Movie ID
					String movie_query = "SELECT MAX(id) as id FROM movies;";
					PreparedStatement movie_ps = dbcon.prepareStatement(movie_query);
					ResultSet movie_rs = movie_ps.executeQuery();
					String movie_id = "";
					
					while (movie_rs.next())
						movie_id = movie_rs.getString("id");
					
					String[] id_parts = movie_id.split("(?<=\\D)(?=\\d)");
					String new_id_number = Integer.toString(Integer.parseInt(id_parts[1]) + 1);
					movie_id = id_parts[0] + new_id_number;
					
					System.out.println("New Movie ID: " + movie_id);

					
					// Handle Star
					String star_query = "SELECT id FROM stars WHERE name = ? ";
					PreparedStatement star_ps = dbcon.prepareStatement(star_query);
					star_ps.setString(1, star);
					ResultSet star_rs = star_ps.executeQuery();
					String star_id = "";
					
					while (star_rs.next())
						star_id = star_rs.getString("id");
					
					if (star_id == "") { // Doesn't exist -> Create new star
						String max_query = "SELECT MAX(id) as id FROM stars;";
						PreparedStatement max_ps = dbcon.prepareStatement(max_query);
						ResultSet max_rs = max_ps.executeQuery();
						String new_id = "";
						
						while (max_rs.next())
							new_id = max_rs.getString("id");
						
						String[] new_id_parts = new_id.split("(?<=\\D)(?=\\d)");
						String new_idno = Integer.toString(Integer.parseInt(new_id_parts[1]) + 1);
						new_id = id_parts[0] + new_idno;
						String max_insert = "INSERT INTO stars(id, name) VALUES ( ?, ?);";
						PreparedStatement max_insert_ps = dbcon.prepareStatement(max_insert);
						max_insert_ps.setString(1, new_id);
			        	max_insert_ps.setString(2, star);
			        	max_insert_ps.executeUpdate();
			        	
			        	star_id = new_id;
					}
					
					System.out.println("New Star ID: " + star_id);
					
					// Handle Genre
					String genre_query = "SELECT id FROM genres WHERE name = ? ";
					PreparedStatement genre_ps = dbcon.prepareStatement(genre_query);
					genre_ps.setString(1, genre);
					ResultSet genre_rs = genre_ps.executeQuery();
					String genre_id = "";
					
					while (genre_rs.next())
						genre_id = genre_rs.getString("id");
					
					
					if (genre_id == "") { // Doesn't exist -> Create new genre
						String max_query = "SELECT MAX(id) as id FROM genres;";
						PreparedStatement max_ps = dbcon.prepareStatement(max_query);
						ResultSet max_rs = max_ps.executeQuery();
						String new_id = "";
						//
						while (max_rs.next())
							new_id = max_rs.getString("id");
						
						new_id = Integer.toString(Integer.parseInt(new_id) + 1);
						String max_insert = "INSERT INTO genres(id, name) VALUES ( ?, ?);";
						PreparedStatement max_insert_ps = dbcon.prepareStatement(max_insert);
						max_insert_ps.setString(1, new_id);
			        	max_insert_ps.setString(2, genre);
			        	max_insert_ps.executeUpdate();
			        	
			        	genre_id = new_id;
					}
					
					Integer new_genre_id = Integer.parseInt(genre_id);
					System.out.println("New Genre ID: " + genre_id);
					
					// Call Procedure
					String procedure = "CALL add_movie(?, ?, ?, ?, ?, ?, ?, ?)";
					PreparedStatement preparedStatement = dbcon.prepareStatement(procedure);
					
					preparedStatement.setString(1, movie_id);
					preparedStatement.setString(2, title);
					preparedStatement.setInt(3, new_year);
					preparedStatement.setString(4, director);
					preparedStatement.setString(5, star);
					preparedStatement.setString(6, star_id);
					preparedStatement.setString(7, genre);
					preparedStatement.setInt(8, new_genre_id);
					
		        	preparedStatement.executeQuery();
		        	System.out.println("Executed Procedure");
		        	
		        	
		        	JsonObject responseJsonObject = new JsonObject();
	                responseJsonObject.addProperty("status", "success");
	                responseJsonObject.addProperty("message", "New movie inserted successfully!");

	                response.getWriter().write(responseJsonObject.toString());
					response.setStatus(200);
				}
				
				else { // Success, but movie already exists
					System.out.println("Movie already exists");
					JsonObject responseJsonObject = new JsonObject();
	                responseJsonObject.addProperty("status", "success");
	                responseJsonObject.addProperty("message", "Movie already exists. No changes have been made.");

	                response.getWriter().write(responseJsonObject.toString());
					response.setStatus(200);
				}
				
	        	
			}
			else {
				System.out.println("ERROR");
				JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Insert Error: Please complete all required fields.");
                response.getWriter().write(responseJsonObject.toString());
			}

				
		} catch (Exception e) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
			response.setStatus(500);
        }
        out.close();
		
	}
}