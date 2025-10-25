package fpt.edu.vn.pizzaapp_prm392.models;

/**
 * Pizza Model - Đại diện cho sản phẩm Pizza
 */
public class Pizza {
    private int pizzaId;
    private String name;
    private String description;
    private double price;
    private String image;
    private String size;
    private String category;
    private double rating;
    private int stock;
    
    // Constructor không có tham số
    public Pizza() {}
    
    // Constructor đầy đủ
    public Pizza(int pizzaId, String name, String description, double price, String image, 
                 String size, String category, double rating, int stock) {
        this.pizzaId = pizzaId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.size = size;
        this.category = category;
        this.rating = rating;
        this.stock = stock;
    }
    
    // Constructor cho tạo mới
    public Pizza(String name, String description, double price, String size, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.size = size;
        this.category = category;
    }
    
    // Getters and Setters
    public int getPizzaId() {
        return pizzaId;
    }
    
    public void setPizzaId(int pizzaId) {
        this.pizzaId = pizzaId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public String getImage() {
        return image;
    }
    
    public void setImage(String image) {
        this.image = image;
    }
    
    public String getSize() {
        return size;
    }
    
    public void setSize(String size) {
        this.size = size;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public double getRating() {
        return rating;
    }
    
    public void setRating(double rating) {
        this.rating = rating;
    }
    
    public int getStock() {
        return stock;
    }
    
    public void setStock(int stock) {
        this.stock = stock;
    }
    
    @Override
    public String toString() {
        return "Pizza{" +
                "pizzaId=" + pizzaId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", size='" + size + '\'' +
                ", category='" + category + '\'' +
                ", rating=" + rating +
                ", stock=" + stock +
                '}';
    }
}
