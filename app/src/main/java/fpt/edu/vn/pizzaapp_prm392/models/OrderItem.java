package fpt.edu.vn.pizzaapp_prm392.models;

/** OrderItem – 1 dòng sản phẩm trong đơn hàng */
public class OrderItem {
    private int orderItemId;
    private int orderId;
    private int pizzaId;
    private int quantity;
    private double price; // đơn giá tại thời điểm đặt

    public OrderItem() {}

    public OrderItem(int orderId, int pizzaId, int quantity, double price) {
        this.orderId = orderId;
        this.pizzaId = pizzaId;
        this.quantity = quantity;
        this.price = price;
    }

    public int getOrderItemId() { return orderItemId; }
    public void setOrderItemId(int orderItemId) { this.orderItemId = orderItemId; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getPizzaId() { return pizzaId; }
    public void setPizzaId(int pizzaId) { this.pizzaId = pizzaId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
