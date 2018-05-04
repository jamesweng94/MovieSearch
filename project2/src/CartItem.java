
public class CartItem {
	private String title;
	private int qty;
	
	public CartItem(String title, int qty) {
		this.title = title;
		this.qty = qty;
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
