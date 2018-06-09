import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;

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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


@WebServlet(name = "SuggestionServlet", urlPatterns = "/api/suggestion")
public class SuggestionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String title = request.getParameter("title");
		PrintWriter out = response.getWriter();
		
		System.out.println("title: " + title);
        JsonArray jsonArray = new JsonArray();
        
		if(title == null || title.trim().isEmpty()) {
			out.write(jsonArray.toString());
			return;
		}
		
		String[] title_token = null;
		if(title != null) {
			title_token = title.split("\\s+");
		}
						
		String where = "";
		for(int i = 0; i < title_token.length; ++i) {
			where += "+" + title_token[i] + "* ";
		}
		where = where.trim();
		
		
		try {
			Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            dataSource = (DataSource) envCtx.lookup("jdbc/TestDB");
            
			Connection dbcon = dataSource.getConnection();
			
			if (dbcon == null)
                out.println("dbcon is null.");
			
			String query = "SELECT title \n "+
							"FROM movies \n" +
							"WHERE MATCH(title) AGAINST ( ? IN BOOLEAN MODE) LIMIT 10;";
			
			PreparedStatement preparedStatement = dbcon.prepareStatement(query);
            preparedStatement.setString(1, where);
            ResultSet rs = preparedStatement.executeQuery();
			
            
            while (rs.next()) {
            	
            	System.out.println("results: " + rs.getString("title"));
            	
            	JsonObject jsonObject = new JsonObject();      	           	
            	jsonObject.addProperty("value", rs.getString("title"));
            	JsonObject additionalDataJsonObject = new JsonObject();
            	additionalDataJsonObject.addProperty("category", "Movie");
            	jsonObject.add("data", additionalDataJsonObject);
            	
            	jsonArray.add(jsonObject);
            }
            
            out.write(jsonArray.toString());
            
            response.setStatus(200);
            rs.close();
            dbcon.close();	
			
		}catch (Exception e) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			response.setStatus(500);			
		}
		out.close();	
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
