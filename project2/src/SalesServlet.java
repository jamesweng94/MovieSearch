
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.sql.PreparedStatement;

import com.google.gson.JsonObject;

/**
 * Servlet implementation class SalesServlet
 */
@WebServlet(name = "SalesServlet", urlPatterns = "/api/sales")
public class SalesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource; 
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String movieID = request.getParameter("movieID");
		String first_name = request.getParameter("firstName");
		String last_name = request.getParameter("lastName");
		String credit_id = request.getParameter("creditID");
		String todo = request.getParameter("todo");
		
		System.out.println("Sales movie ID: " + movieID);
		System.out.println("Sales first name: " + first_name);
		System.out.println("Sales last name: " + last_name);
		System.out.println("Sales credit ID: " + credit_id);
		System.out.println("Sales todo: " + todo);

		
		PrintWriter out = response.getWriter();
		
		try {
        	Connection dbcon = dataSource.getConnection();
        	//Statement statement = dbcon.createStatement();
        	if(todo == null) {
	        	String query = "INSERT INTO sales(customerID, movieId, saleDate) VALUES(\n" + 
	        					"(SELECT  C.id\n" + 
	        					"FROM customers C \n" + 
	        					"WHERE C.firstName = ? AND C.lastName = ? AND C.ccId = ?), ? ,(SELECT CURDATE() AS date));";
	        	
	        	PreparedStatement preparedStatement = dbcon.prepareStatement(query);
	        	
	        	preparedStatement.setString(1, first_name);
	        	preparedStatement.setString(2, last_name);
	        	preparedStatement.setString(3, credit_id);
	        	preparedStatement.setString(4, movieID);
	        	
	        	preparedStatement.executeUpdate(query);
        	}
        	else {
        		System.out.println("Inside of else todo: " + todo);
                JsonObject jsonObject = new JsonObject();
        		String findSales = "SELECT S.id\n" + 
        							"FROM sales S\n" + 
        							"WHERE movieID = ? ;";
        		
        		PreparedStatement preparedStatement = dbcon.prepareStatement(findSales);
        		preparedStatement.setString(1, movieID);
        		ResultSet rs = preparedStatement.executeQuery();
        		
        		if (rs.next()) {
        			String id = rs.getString("id");
        			System.out.println("Inside sale servlet ID: " + id);
        			jsonObject.addProperty("sale_id", id);
        		}
        		response.getWriter().write(jsonObject.toString()); 
        	}
        	
        	out.close();
            dbcon.close();		
		}
		catch (Exception e) {
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set response status to 500 (Internal Server Error)
			response.setStatus(500);
		}
		out.close();
	}

}
