/**
 * This User class only has the username field in this example.
 * <p>
 * However, in the real project, this User class can contain many more things,
 * for example, the user's shopping cart items.
 */

import java.io.*;
import java.util.*;

public class User {

    private final String email;
    private List<CartItem> cart; 
    
    public User(String email) {
        this.email = email;
        cart = new ArrayList<CartItem>();
    }
    
    public String getEmail() {
        return this.email;
    }
    
    public void addItem(String movieID, String title, int qtyOrdered) {
        // Check if the id is already in the shopping cart
        Iterator<CartItem> iter = cart.iterator();
        while (iter.hasNext()) {
           CartItem item = iter.next();
           if (item.getID().equals(movieID)) {
              // id found, increase qtyOrdered
              item.setQty(item.getQty() + qtyOrdered);
              return;
           }
        }
        // id not found, create a new CartItem
        cart.add(new CartItem(movieID, title, qtyOrdered));
     }
    
    public boolean updateItem(String title, int newQty) {
        Iterator<CartItem> iter = cart.iterator();
        while (iter.hasNext()) {
           CartItem item = iter.next();
           if (item.getTitle().equals(title)) {
              // id found, increase qtyOrdered
              item.setQty(newQty);
              return true;
           }
        }
        return false;
     }
    
    public void removeItem(String title) {
        Iterator<CartItem> iter = cart.iterator();
        while (iter.hasNext()) {
           CartItem item = iter.next();
           if (item.getTitle().equals(title)) {
              cart.remove(item);
              return;
           }
        }
     }
    
    public int cartSize() {
    	return cart.size();
    }
    
    public boolean cartIsEmpty() {
    	return cart.size() == 0;
    }
    
    public List<CartItem> getCartItems(){
    	return cart;
    }
    
    public void clearCart() {
    	cart.clear();
    }
}