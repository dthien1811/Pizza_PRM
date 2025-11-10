package fpt.edu.vn.pizzaapp_prm392.services;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fpt.edu.vn.pizzaapp_prm392.dao.PizzaDAO;
import fpt.edu.vn.pizzaapp_prm392.models.Pizza;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SyncService {
    private PizzaApiService api;
    private PizzaDAO pizzaDAO;
    private Context context;

    Random random = new Random();
    String[] sizes = {"S", "M", "L", "XL"};


    public interface SyncCallback {
        void onSyncComplete();
    }
    private SyncCallback callback;

    public SyncService(Context context) {
        this.context = context;
        api = RetrofitClient.getClient().create(PizzaApiService.class);
        pizzaDAO = new PizzaDAO(context);
    }

    public void syncFromApi(SyncCallback callback) {
        Log.d("SYNC_DEBUG", "Bắt đầu syncFromApi()");
        this.callback = callback;
        syncPage(0, 30);
    }

    private void syncPage(int skip, int limit) {
        api.getRecipes(skip, limit).enqueue(new Callback<RecipesResponse>() {

            @Override
            public void onResponse(Call<RecipesResponse> call, Response<RecipesResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().recipes != null) {
                    List<Pizza> pizzas = new ArrayList<>();
                    for (RecipeDetail r : response.body().recipes) {
                        Pizza p = new Pizza();
                        p.setPizzaId(r.id);
                        p.setName(r.name != null ? r.name : "Pizza");
                        p.setImage(r.image != null ? r.image : "");
                        p.setCategory(r.cuisine != null ? r.cuisine : "Italian");
                        p.setRating(r.rating > 0 ? r.rating : 4.5);

                        String desc = r.instructions != null && !r.instructions.isEmpty()
                                ? String.join(" ", r.instructions)
                                .substring(0, Math.min(150, String.join(" ", r.instructions).length())) + "..."
                                : "Pizza ngon từ Ý";

                        p.setDescription(desc);

                        p.setPrice(50000 + (500000 - 50000) * random.nextDouble());
                        p.setSize(sizes[random.nextInt(sizes.length)]);
                        p.setStock(random.nextInt(101));

                        pizzas.add(p);

                    }

                    pizzaDAO.insertOrUpdate(pizzas);

                    Log.d("SYNC", "Đồng bộ: " + pizzas.size() + " pizza");

                    if (skip + limit < response.body().total) {
                        syncPage(skip + limit, limit);
                    } else {
                        if (callback != null) {
                            Log.d("SYNC_DEBUG", "Đồng bộ hoàn tất → gọi callback");
                            callback.onSyncComplete();
                        }
                    }
                } else {
                    Log.e("SYNC", "API lỗi: " + response.code() + " → Dùng dữ liệu giả");
                    insertDummyData();
                }
            }

            @Override
            public void onFailure(Call<RecipesResponse> call, Throwable t) {
                Log.e("SYNC", "Lỗi: " + t.getMessage() + " → Dùng dữ liệu giả");
                insertDummyData();
            }
        });
    }
    private void insertDummyData() {
        if (pizzaDAO.getAllPizzas().isEmpty()) {
            List<Pizza> dummy = new ArrayList<>();
            String[] names = {"Margherita", "Pepperoni", "Hawaiian", "Vegetarian", "Seafood"};
            for (int i = 0; i < names.length; i++) {
                Pizza p = new Pizza();
                p.setPizzaId(i + 1);
                p.setName(names[i] + " Pizza");
                p.setImage("https://cdn.dummyjson.com/recipe-images/" + (i + 1) + ".webp");
                p.setCategory("Italian");
                p.setRating(4.5 + (i * 0.1));
                p.setPrice(150000 + (i * 20000));
                p.setSize("M");
                p.setStock(10);
                p.setDescription("Pizza ngon, giao nhanh trong 30 phút.");
                dummy.add(p);
            }
            pizzaDAO.insertOrUpdate(dummy);
            Log.d("SYNC", "ĐÃ TẠO DỮ LIỆU GIẢ: " + dummy.size() + " pizza");
        }
    }
}


