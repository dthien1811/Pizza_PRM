package fpt.edu.vn.pizzaapp_prm392.services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PizzaApiService {
    @GET("recipes")
    Call<RecipesResponse> getRecipes(@Query("skip") int skip, @Query("limit") int limit);
}

