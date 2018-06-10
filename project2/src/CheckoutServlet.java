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
import java.sql.PreparedStatement;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;



@WebServlet(name = "CheckoutServlet", urlPatterns = "/api/checkout")
public class CheckoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/LocalDB")
    private DataSource dataSource; 
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String firstName = request.getParameter("first_name");
		String lastName = request.getParameter("last_name");
		String creditId = request.getParameter("credit_id");
		String creditDate = request.getParameter("credit_date");
		
        PrintWriter out = response.getWriter();
        
        System.out.println("first name: " + firstName);
        System.out.println("last name: " + lastName);
        System.out.println("credit ID: " + creditId);
        System.out.println("credit date: " + creditDate);
        
        try {
        	Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            dataSource = (DataSource) envCtx.lookup("jdbc/LocalDB");
            
			Connection dbcon = dataSource.getConnection();
			
			if (dbcon == null)
                out.println("dbcon is null.");
        	
        	String query ="SELECT C.firstName, C.lastName, C.id, C.expiration\n" + 
 				   "FROM creditcards C\n" + 
 				   "WHERE C.firstName = ? AND C.lastName = ? AND C.id = ? AND C.expiration= ? ;";
        	
        	PreparedStatement preparedStatement = dbcon.prepareStatement(query);
        	preparedStatement.setString(1, firstName);
        	preparedStatement.setString(2, lastName);
        	preparedStatement.setString(3, creditId);
        	preparedStatement.setString(4, creditDate);
        	
        	ResultSet resultSet = preparedStatement.executeQuery();
        		
        	if(resultSet.next()) {
        		System.out.println("Customer verfied success");
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("first_name", firstName);
                responseJsonObject.addProperty("last_name", lastName);
                responseJsonObject.addProperty("credit_id", creditId);
                responseJsonObject.addProperty("message", "success");
                
                response.getWriter().write(responseJsonObject.toString());       		
        	}else {
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Incorrect information, please try again!");
     
                response.getWriter().write(responseJsonObject.toString());
            }
        	
        	resultSet.close();
            dbcon.close();	
            
        }        
        catch (Exception e) {
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
