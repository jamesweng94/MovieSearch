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

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource; 
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String email = request.getParameter("email");
        String password = request.getParameter("password");
        PrintWriter out = response.getWriter();
        
        /*
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);
        
        
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
        
        response.setContentType("application/json");
        */
        
        try {   	
        	Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            dataSource = (DataSource) envCtx.lookup("jdbc/TestDB");
        	
        	Connection dbcon = dataSource.getConnection();
        	
        	if (dbcon == null)
                out.println("dbcon is null.");
        	
        	String query = "SELECT * from customers WHERE email = ? ;";
        	PreparedStatement preparedStatement = dbcon.prepareStatement(query);
        	preparedStatement.setString(1, email);
        	ResultSet resultSet = preparedStatement.executeQuery();
        	
    		boolean success = false;
    		
        	if(resultSet.next()) {
        		
    			String encryptedPassword = resultSet.getString("password");
    			System.out.println("Password: " + password);
    			System.out.println("Encrypted Password: " + encryptedPassword);
    			success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);

        		if(success) {   		
	        		System.out.println("Login success");
	                request.getSession().setAttribute("email", new User(email));
	
	                JsonObject responseJsonObject = new JsonObject();
	                responseJsonObject.addProperty("status", "success");
	                responseJsonObject.addProperty("message", "success");

	                response.getWriter().write(responseJsonObject.toString());  
        		}
        		if(!success) {
	
	                JsonObject responseJsonObject = new JsonObject();
	                responseJsonObject.addProperty("status", "fail");
	                responseJsonObject.addProperty("message", "Login error: Email " + email + " doesn't exist or incorrect password");
	     
	                response.getWriter().write(responseJsonObject.toString());
	            }
        	} else
        	{

                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Login error: Email " + email + " doesn't exist or incorrect password");
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
