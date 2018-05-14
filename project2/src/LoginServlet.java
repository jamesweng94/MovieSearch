import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.jasypt.util.password.StrongPasswordEncryptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	
    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource; 
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//UpdateSecurePassword update = new UpdateSecurePassword();
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);
        PrintWriter out = response.getWriter();

        // Verify reCAPTCHA
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", e.getMessage());
 
            response.getWriter().write(responseJsonObject.toString());
            return;
        }
        
        //response.setContentType("application/json");
                
        try {   	
        	Connection dbcon = dataSource.getConnection();
        	Statement statement = dbcon.createStatement();
        	
        	/*
        	String query ="SELECT C.email, C.password\n" + 
        				   "FROM customers C\n" + 
        				  "WHERE C.email = " + "'"+ email + "'"+ " and C.password = " + "'"+password + "';";
        				  */
        	
    		String query = String.format("SELECT * from customers where email='%s'", email);
        	ResultSet resultSet = statement.executeQuery(query);
        	
    		boolean success = false;
        	if(resultSet.next()) {
        		
    			String encryptedPassword = resultSet.getString("password");
    			System.out.println("Password: " + password);
    			System.out.println("Encrypted Password: " + encryptedPassword);
    			success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);

        		if(success) {   		
	                // Login success:
	        		System.out.println("Login success");
	                // set this user into the session
	                request.getSession().setAttribute("email", new User(email));
	
	                JsonObject responseJsonObject = new JsonObject();
	                responseJsonObject.addProperty("status", "success");
	                responseJsonObject.addProperty("message", "success");

	                response.getWriter().write(responseJsonObject.toString());  
        		}
        		if(!success) {
	                // Login fail
	                JsonObject responseJsonObject = new JsonObject();
	                responseJsonObject.addProperty("status", "fail");
	                responseJsonObject.addProperty("message", "Login error: Email " + email + " doesn't exist or incorrect password");
	     
	                response.getWriter().write(responseJsonObject.toString());
	            }
        	}else
        	{
                // Login fail
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Login error: Email " + email + " doesn't exist or incorrect password");
                response.getWriter().write(responseJsonObject.toString());
        	}
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
