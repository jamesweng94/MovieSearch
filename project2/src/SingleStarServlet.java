import javax.servlet.annotation.WebServlet;
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
import java.sql.PreparedStatement;
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
			Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            dataSource = (DataSource) envCtx.lookup("jdbc/TestDB");
            
			Connection dbcon = dataSource.getConnection();
			
			if (dbcon == null)
                out.println("dbcon is null.");
			
			JsonArray jsonArray = new JsonArray();
			
			String query = "SELECT T.name, T.birthYear, GROUP_CONCAT(DISTINCT M.title SEPARATOR', ') AS movies\n" +
							"FROM (SELECT * FROM stars S WHERE S.name = ? ) AS T\n" +
							"LEFT JOIN stars_in_movies SM ON SM.starId = T.id\n" +
							"LEFT JOIN movies M ON M.id = SM.movieId\n" +
							"GROUP BY T.name, T.birthYear;\n";
		
			
			PreparedStatement preparedStatement = dbcon.prepareStatement(query);
			preparedStatement.setString(1, name);
			ResultSet rs = preparedStatement.executeQuery();
			
			
			while (rs.next()) {
				String star_name = rs.getString("name");
				String star_birthyear = rs.getString("birthYear");
				String star_movies = rs.getString("movies");
				
				String [] movies = star_movies.split(", ");
				JsonArray movies_array = new JsonArray();
				
				for (int i = 0; i < movies.length; ++i) {
			        movies_array.add(movies[i]);
				}
				
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