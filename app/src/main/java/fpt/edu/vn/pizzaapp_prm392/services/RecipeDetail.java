package fpt.edu.vn.pizzaapp_prm392.services;

import java.util.List;

public class RecipeDetail {
    public int id;
    public String name;
    public String image;
    public String cuisine;  // Map to category
    public List<String> instructions; // Map to description (join strings)
    public double rating;
    public double price = 0;
    public int stock = 0;

    // Thêm
    public String difficulty; // Map to size
    public List<String> tags;
    public List<String> ingredients; // Map to ingredients (join strings)
    public int prepTimeMinutes;
    public int cookTimeMinutes;
    public int servings;
    public double caloriesPerServing;
    public int reviewCount;
    public List<String> mealType;
}
