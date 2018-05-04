

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
		String firstName = request.getParameter("first_name");
		
		PrintWriter out = response.getWriter();
		
		try {
        	Connection dbcon = dataSource.getConnection();
        	Statement statement = dbcon.createStatement();
        	
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
