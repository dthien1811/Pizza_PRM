package fpt.edu.vn.pizzaapp_prm392.models;

/**
 * Payment Model - Đại diện cho thông tin thanh toán
 */
public class Payment {
    private int paymentId;
    private int orderId;
    private String paymentMethod;  // COD, CREDIT_CARD, BANK_TRANSFER
    private double amount;
    private String status;  // PENDING, COMPLETED, FAILED, REFUNDED
    private String transactionId;
    private String createdAt;
    
    // Constructor không có tham số
    public Payment() {}
    
    // Constructor đầy đủ
    public Payment(int paymentId, int orderId, String paymentMethod, double amount, 
                   String status, String transactionId, String createdAt) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.status = status;
        this.transactionId = transactionId;
        this.createdAt = createdAt;
    }
    
    // Constructor cho tạo mới
    public Payment(int orderId, String paymentMethod, double amount) {
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.status = "PENDING";
    }
    
    // Getters and Setters
    public int getPaymentId() {
        return paymentId;
    }
    
    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }
    
    public int getOrderId() {
        return orderId;
    }
    
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", orderId=" + orderId +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
