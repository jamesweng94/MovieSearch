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

@WebServlet(name = "InsertStarServlet", urlPatterns = "/api/insert-star")
public class InsertStarServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Resource(name = "jdbc/moviedb")
    private DataSource dataSource; 

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		
		String starname = request.getParameter("starname");
		String birthyear = request.getParameter("birthyear");
		
		System.out.println("Starname: " + starname);
		System.out.println("Birthyear: " + birthyear);
		
		try { 
			
			if (starname != "")
			{
				Connection dbcon = dataSource.getConnection();
				
				String query = "SELECT MAX(id) as id FROM stars;";
				PreparedStatement id_ps = dbcon.prepareStatement(query);
				ResultSet rs = id_ps.executeQuery();
				String id = "";
				
				while (rs.next())
					id = rs.getString("id");
				
				String[] id_parts = id.split("(?<=\\D)(?=\\d)");
				String new_id_number = Integer.toString(Integer.parseInt(id_parts[1]) + 1);
				id = id_parts[0] + new_id_number;
				
				System.out.println("New ID: " + id);
					
				String insert = "";
				PreparedStatement preparedStatement = dbcon.prepareStatement("");
				
				if (birthyear != "")
				{
					insert = "INSERT INTO stars(id, name, birthYear) VALUES ( ?, ?, ?);";
					preparedStatement = dbcon.prepareStatement(insert);
					preparedStatement.setString(1, id);
		        	preparedStatement.setString(2, starname);
		        	Integer birthyr = Integer.parseInt(birthyear);
		        	preparedStatement.setInt(3, birthyr);
		        	System.out.println("Birthyear not null");
		        	
				}
				else
				{
					insert = "INSERT INTO stars(id, name) VALUES ( ?, ?);";
					preparedStatement = dbcon.prepareStatement(insert);
					preparedStatement.setString(1, id);
		        	preparedStatement.setString(2, starname);
		        	System.out.println("Birthyear null");
				}
				
	        	preparedStatement.executeUpdate();
	        	
				
	        	JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

                response.getWriter().write(responseJsonObject.toString());
				response.setStatus(200);
			}
			else {
				
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