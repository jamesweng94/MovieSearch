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
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "SingleStarServlet", urlPatterns = "/api/single-star")
public class SingleStarServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Resource(name = "jdbc/moviedb")
    private DataSource dataSource; 

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
		String name = request.getParameter("name");
		
		response.setContentType("application/json");
		
		PrintWriter out = response.getWriter();
		
		try { 
			Connection dbcon = dataSource.getConnection();
			Statement statement = dbcon.createStatement();
			JsonArray jsonArray = new JsonArray();
			
			String query = "SELECT T.name, T.birthYear, GROUP_CONCAT(DISTINCT M.title SEPARATOR', ') AS movies\n" +
							"FROM (SELECT * FROM stars S WHERE S.name = '" + name + "') AS T\n" +
							"LEFT JOIN stars_in_movies SM ON SM.starId = T.id\n" +
							"LEFT JOIN movies M ON M.id = SM.movieId\n" +
							"GROUP BY T.name, T.birthYear;\n";
		
			
			ResultSet rs = statement.executeQuery(query);
			
			
			while (rs.next()) {
				String star_name = rs.getString("name");
				String star_birthyear = rs.getString("birthYear");
				String star_movies = rs.getString("movies");
				
				String [] movies = star_movies.split(", ");
				JsonArray movies_array = new JsonArray();
				
				for (int i = 0; i < movies.length; ++i) {
			        movies_array.add(movies[i]);
				}
				
                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("star_name", star_name);
                jsonObject.addProperty("star_birthyear", star_birthyear);
                jsonObject.add("star_movies", movies_array);
                 
                jsonArray.add(jsonObject);
			}
			
			out.write(jsonArray.toString());
			response.setStatus(200);
			
				
		} catch (Exception e) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			response.setStatus(500);

        }
        out.close();
		
	}
}