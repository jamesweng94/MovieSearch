
public class CartItem {
	private String id;
	private String title;
	private int qty;
	
	public CartItem(String id, String title, int qty) {
		this.id = id;
		this.title = title;
		this.qty = qty;
	}
	
	public String getID() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public int getQty() {
		return qty;
	}
	
   public void setQty(int qty) {
	      this.qty = qty;
	   }
	
}
