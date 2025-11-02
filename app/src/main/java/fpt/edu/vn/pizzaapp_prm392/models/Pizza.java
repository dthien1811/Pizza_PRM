package fpt.edu.vn.pizzaapp_prm392.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Pizza Model - Đại diện cho sản phẩm Pizza
 */
public class Pizza implements Parcelable {
    private int pizzaId;
    private String name;
    private String description;
    private double price = 0.0D;
    private String image;
    private String size = "M";
    private String category;
    private double rating;
    private int stock = 0;
    
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

    protected Pizza(Parcel in) {
        pizzaId = in.readInt();
        name = in.readString();
        description = in.readString();
        price = in.readDouble();
        image = in.readString();
        size = in.readString();
        category = in.readString();
        rating = in.readDouble();
        stock = in.readInt();
    }

    public static final Creator<Pizza> CREATOR = new Creator<Pizza>() {
        @Override
        public Pizza createFromParcel(Parcel in) {
            return new Pizza(in);
        }

        @Override
        public Pizza[] newArray(int size) {
            return new Pizza[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(pizzaId);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeDouble(price);
        parcel.writeString(image);
        parcel.writeString(size);
        parcel.writeString(category);
        parcel.writeDouble(rating);
        parcel.writeInt(stock);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pizza pizza = (Pizza) o;
        return pizzaId == pizza.pizzaId &&
                Objects.equals(name, pizza.name) &&
                Objects.equals(description, pizza.description) &&
                Objects.equals(image, pizza.image) &&
                Objects.equals(category, pizza.category);

//                Double.compare(pizza.rating, rating) == 0 &&
//                Double.compare(pizza.price, price) == 0 &&
//                Objects.equals(size, pizza.size) &&
//                Objects.equals(stock, pizza.stock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pizzaId, name, description, image, category);
//        return Objects.hash(pizzaId, name, description, price, image, size, category, rating, stock);
        }

}
