

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@WebServlet(name = "ShoppingCart", urlPatterns = "/api/cart")
public class ShoppingCart extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession(true);
		User user;
		synchronized (session) {
			user = (User) session.getAttribute("email");
		}
		
		//System.out.println("Cart size: " + user.cartSize());
		
        PrintWriter out = response.getWriter();     
		JsonArray jsonArray = new JsonArray();

        try {
        	String movie_id = request.getParameter("movieID");
        	String todo = request.getParameter("todo");
        	String title = request.getParameter("title");
        	String new_qty = request.getParameter("qty");
        	int newQty = 0;
        	
        	if(new_qty != null) {
        		 newQty = Integer.parseInt(new_qty);
        		 if(newQty<0) {
        			 newQty = 1;
        		 }
        	}
        	System.out.println("To-do: " + todo);
        	System.out.println("Movie Title: " + title);
        	System.out.println("New qty: " + newQty);
        	if(todo == null) { 
        		todo = "view";
        	}
        	if (todo.equals("add")) {
        		user.addItem(movie_id,title, 1);
        	}
        	else if(todo.equals("update")) {
        		user.updateItem(title, newQty);
        	}
        	else if(todo.equals("remove")) {
        		user.removeItem(title);
        	}
        	
        	if(user.cartIsEmpty()) {
    			JsonObject jsonObject = new JsonObject();
    			jsonObject.addProperty("message", "Your shopping cart is empty");
    			jsonArray.add(jsonObject);
    			
        	} 
        	else if(todo.equals("getId")) {
        		for(CartItem item : user.getCartItems()) {
        			String movieID = item.getID();
        			JsonObject jsonObject = new JsonObject();
        			jsonObject.addProperty("movieID", movieID);
        			jsonArray.add(jsonObject);
        		}
        	}
        	else {
        		for(CartItem item : user.getCartItems()) {
        			String m_id = item.getID();
        			System.out.println("m_id: " + m_id);
        			String m_title = item.getTitle();
        			int m_qty = item.getQty();
        			JsonObject jsonObject = new JsonObject();
        			jsonObject.addProperty("movieID", m_id);
        			jsonObject.addProperty("movie_title", m_title);
        			jsonObject.addProperty("movie_qty", m_qty);

        			jsonArray.add(jsonObject);
        		}
        	}
        	
            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);
        	
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
