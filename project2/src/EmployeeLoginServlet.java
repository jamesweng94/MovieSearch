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

import org.jasypt.util.password.StrongPasswordEncryptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "EmployeeLoginServlet", urlPatterns = "/api/employee-login")
public class EmployeeLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    @Resource(name = "jdbc/moviedb")
    
    private DataSource dataSource; 
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
        String email = request.getParameter("email");
        String password = request.getParameter("password");

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
        	
        	String query = "SELECT * from employees WHERE email = ? ;";
        	PreparedStatement preparedStatement = dbcon.prepareStatement(query);
        	preparedStatement.setString(1, email);
        	ResultSet resultSet = preparedStatement.executeQuery();
        	
    		boolean success = false;
    		
        	if(resultSet.next()) {
        		
    			String encryptedPassword = resultSet.getString("password");
    			String fullname = resultSet.getString("fullname");
    			
    			System.out.println("Password: " + password);
    			System.out.println("Encrypted Password: " + encryptedPassword);
    			success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);

        		if(success) {   		
	        		System.out.println("Employee login success");
	                request.getSession().setAttribute("email", new User(email));
	
	                JsonObject responseJsonObject = new JsonObject();
	                responseJsonObject.addProperty("status", "success");
	                responseJsonObject.addProperty("message", "success");
	                responseJsonObject.addProperty("fullname", fullname);

	                response.getWriter().write(responseJsonObject.toString());  
        		}
        		if(!success) {
	                JsonObject responseJsonObject = new JsonObject();
	                responseJsonObject.addProperty("status", "fail");
	                responseJsonObject.addProperty("message", "Employee login error: Email " + email + " doesn't exist or incorrect password");
	     
	                response.getWriter().write(responseJsonObject.toString());
	            }
        	} else
        	{
                // Login fail
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Employee login error: Email " + email + " doesn't exist or incorrect password");
                response.getWriter().write(responseJsonObject.toString());
        	}
        }
        catch (Exception e) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			response.setStatus(500);
		}
		out.close();
	}

}

