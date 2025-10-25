package fpt.edu.vn.pizzaapp_prm392.models;

/**
 * Order Model - Đại diện cho đơn hàng
 */
public class Order {
    private int orderId;
    private String orderCode;
    private int userId;
    private double totalPrice;
    private String address;
    private String phone;
    private String notes;
    private String status;  // PENDING, CONFIRMED, SHIPPING, DELIVERED, CANCELLED
    private String paymentMethod;  // COD, CREDIT_CARD, BANK_TRANSFER
    private String createdAt;
    private String updatedAt;
    
    // Constructor không có tham số
    public Order() {}
    
    // Constructor đầy đủ
    public Order(int orderId, String orderCode, int userId, double totalPrice, String address, 
                 String phone, String notes, String status, String paymentMethod, 
                 String createdAt, String updatedAt) {
        this.orderId = orderId;
        this.orderCode = orderCode;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.address = address;
        this.phone = phone;
        this.notes = notes;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Constructor cho tạo mới
    public Order(String orderCode, int userId, double totalPrice, String address, 
                 String phone, String paymentMethod) {
        this.orderCode = orderCode;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.address = address;
        this.phone = phone;
        this.paymentMethod = paymentMethod;
        this.status = "PENDING";
    }
    
    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }
    
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    
    public String getOrderCode() {
        return orderCode;
    }
    
    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public double getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", orderCode='" + orderCode + '\'' +
                ", userId=" + userId +
                ", totalPrice=" + totalPrice +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", status='" + status + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
