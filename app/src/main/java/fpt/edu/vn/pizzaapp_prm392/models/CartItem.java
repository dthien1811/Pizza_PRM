package fpt.edu.vn.pizzaapp_prm392.models;

/**
 * CartItem Model - Đại diện cho mục trong giỏ hàng
 */
public class CartItem {
    private int cartId;
    private int userId;
    private int pizzaId;
    private int quantity;
    private double price;
    private String notes;
    private String addedAt;
    
    // Constructor không có tham số
    public CartItem() {}
    
    // Constructor đầy đủ
    public CartItem(int cartId, int userId, int pizzaId, int quantity, double price, 
                    String notes, String addedAt) {
        this.cartId = cartId;
        this.userId = userId;
        this.pizzaId = pizzaId;
        this.quantity = quantity;
        this.price = price;
        this.notes = notes;
        this.addedAt = addedAt;
    }
    
    // Constructor cho tạo mới
    public CartItem(int userId, int pizzaId, int quantity, double price) {
        this.userId = userId;
        this.pizzaId = pizzaId;
        this.quantity = quantity;
        this.price = price;
    }
    
    // Getters and Setters
    public int getCartId() {
        return cartId;
    }
    
    public void setCartId(int cartId) {
        this.cartId = cartId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getPizzaId() {
        return pizzaId;
    }
    
    public void setPizzaId(int pizzaId) {
        this.pizzaId = pizzaId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getAddedAt() {
        return addedAt;
    }
    
    public void setAddedAt(String addedAt) {
        this.addedAt = addedAt;
    }
    
    /**
     * Tính tổng giá cho mục này (price * quantity)
     */
    public double getTotalPrice() {
        return price * quantity;
    }
    
    @Override
    public String toString() {
        return "CartItem{" +
                "cartId=" + cartId +
                ", userId=" + userId +
                ", pizzaId=" + pizzaId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", totalPrice=" + getTotalPrice() +
                ", notes='" + notes + '\'' +
                '}';
    }
}
