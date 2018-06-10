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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;

@WebServlet(name = "MetadaServlet", urlPatterns = "/api/metadata")
public class MetadataServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;
	
	@Resource(name = "jdbc/LocalDB")
	private DataSource dataSource;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json"); 
		PrintWriter out = response.getWriter();
		
		try {
				Context initCtx = new InitialContext();
	
	            Context envCtx = (Context) initCtx.lookup("java:comp/env");
	            if (envCtx == null)
	                out.println("envCtx is NULL");
	
	            dataSource = (DataSource) envCtx.lookup("jdbc/LocalDB");
	            
				Connection dbcon = dataSource.getConnection();
				
				if (dbcon == null)
	                out.println("dbcon is null.");
			 
			 	String query_tables = "SELECT table_name FROM information_schema.tables WHERE table_type='base table' AND table_schema='moviedb';";
			 	PreparedStatement preparedStatement = dbcon.prepareStatement(query_tables);
			 	ResultSet result_set = preparedStatement.executeQuery();
			 	
			 	JsonArray jsonArray = new JsonArray();
			 	
			 	while (result_set.next()) {
			 		String table_names = result_set.getString("table_name");
			 		String [] tables = table_names.split(", ");
			 		
			 		for (int i = 0; i < tables.length; i++) {
			 			JsonObject jsonObject = new JsonObject();
			 			
			 			String table_name = tables[i];
			 			System.out.println("Table: " + table_name);
			 			jsonObject.addProperty("table_name", table_name);
			 			
			 			String query = "SELECT GROUP_CONCAT(column_name SEPARATOR ', ') AS column_name," + 
			 							"GROUP_CONCAT(column_type SEPARATOR ', ') AS column_type " + 
			 							"FROM information_schema.columns WHERE table_name = ? ;";

			 			PreparedStatement ps = dbcon.prepareStatement(query);
			 			ps.setString(1, table_name);
			 			ResultSet rs = ps.executeQuery();
			 			
			 			while (rs.next()) {
			 				String column_name = rs.getString("column_name");
			 				String column_type = rs.getString("column_type");
			 				
			 				System.out.println("Column Names: " + column_name);
			 				System.out.println("Column Types: " + column_type);
			 				
			 				jsonObject.addProperty("column_name", column_name);
			 				jsonObject.addProperty("column_type", column_type);
			 			
			 			}
			 			jsonArray.add(jsonObject);
			 		}
			 	}
			 	
	            out.write(jsonArray.toString());
	            response.setStatus(200);

	            result_set.close();
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
