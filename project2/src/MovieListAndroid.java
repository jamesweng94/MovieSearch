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

@WebServlet(name = "MovieListAndroid", urlPatterns = "/api/android-list")

public class MovieListAndroid extends HttpServlet {
    private static final long serialVersionUID = 1L;
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); 		
        String title = (String) request.getSession().getAttribute("title");
        
        try {
        	Connection dbcon = dataSource.getConnection();
        	
        	/*if (title == null)
        		return;
        	
        	String[] titleTokens = null;
        	titleTokens = title.split("\\s+");
        	
        	String where = "";
        	for (int i = 0; i < titleTokens.length; ++i) {
				where += "+" + titleTokens[i] + "* ";
			}
        	where = where.trim();
        	
        	String query = "SELECT M.id, M.title, M.year, M.director, GROUP_CONCAT(DISTINCT G.name SEPARATOR ', ') AS genres , GROUP_CONCAT(DISTINCT S.name SEPARATOR ', ') AS stars, R.rating\n" + 
					"FROM movies M\n" +
					"LEFT JOIN stars_in_movies SM ON M.id = SM.movieId\n" + 
					"LEFT JOIN stars S ON SM.starId = S.id\n" +
					"LEFT JOIN genres_in_movies RM ON M.id = RM.movieId\n" +
					"LEFT JOIN genres G ON RM.genreId = G.id \n" +
					"LEFT JOIN ratings R ON M.id = R. movieId \n" +
					"WHERE MATCH (M.title) AGAINST( ? IN BOOLEAN MODE) \n" +
					"GROUP BY M.id, M.title, M.year, M.director, R.rating LIMIT 500;\n";
        	
        	PreparedStatement preparedStatement = dbcon.prepareStatement(query);
        	preparedStatement.setString(1, where);
        	ResultSet rs = preparedStatement.executeQuery();*/
        	
        	JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "success from android list");
            response.getWriter().write(responseJsonObject.toString());
            
        } catch (Exception e) {
        	JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			response.getWriter().write(jsonObject.toString());
			response.setStatus(500);
        }

    }
}