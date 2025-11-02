package fpt.edu.vn.pizzaapp_prm392.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingSource;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;

import java.io.IOException;
import java.util.List;

import fpt.edu.vn.pizzaapp_prm392.dao.PizzaDAO;
import fpt.edu.vn.pizzaapp_prm392.models.Pizza;
import fpt.edu.vn.pizzaapp_prm392.services.PizzaApiService;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import retrofit2.Response;

public class RxProductPagingSource extends RxPagingSource<Integer, Pizza> {

    private final PizzaDAO pizzaDAO;
    private final String query;
    private final Double minPrice, maxPrice;
    private final String category;

    public RxProductPagingSource(PizzaDAO pizzaDAO, String query, Double minPrice, Double maxPrice, String category) {
        this.pizzaDAO = pizzaDAO;
        this.query = query == null ? "": query;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.category = category;
    }


    @NonNull
    @Override
    public Single<LoadResult<Integer, Pizza>> loadSingle(@NonNull LoadParams<Integer> loadParams) {
        return Single.create(emitter -> {
            try {
                Log.d("PAGING_DEBUG", ">>> loadSingle() bắt đầu với query=" + query);

                int page = loadParams.getKey() != null ? loadParams.getKey() : 1;
                int size = 20;

                List<Pizza> cached = pizzaDAO.search(query, minPrice, maxPrice, category, page, size);
                Integer prevKey = page > 1 ? page - 1 : null;
                Integer nextKey = cached.size() == size ? page + 1 : null;

                Log.d("TAG", "loadSingle: " + cached.size());

                Log.d("DB_DEBUG", "Page: " + page + " | Size: " + cached.size());
                for (Pizza p : cached) {
                    Log.d("DB_DEBUG", "Pizza: " + p.getName() + " - " + p.getPrice());
                }


                emitter.onSuccess(new LoadResult.Page<>(cached, prevKey, nextKey));

            } catch (Exception e) {
                Log.e("DB_ERROR", "Lỗi khi load từ DB: " + e.getMessage(), e);
                emitter.onSuccess(new LoadResult.Error<>(e));
            }
        });
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, Pizza> pagingState) {
        return 1;
    }
}
