package fpt.edu.vn.pizzaapp_prm392.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.paging.LoadState;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.RangeSlider;

import java.util.Arrays;
import java.util.List;

import fpt.edu.vn.pizzaapp_prm392.R;
import fpt.edu.vn.pizzaapp_prm392.adapter.CategoryFilterAdapter;
import fpt.edu.vn.pizzaapp_prm392.adapter.ProductAdapter;
import fpt.edu.vn.pizzaapp_prm392.adapter.RxProductPagingSource;
import fpt.edu.vn.pizzaapp_prm392.dao.PizzaDAO;
import fpt.edu.vn.pizzaapp_prm392.models.Pizza;
import fpt.edu.vn.pizzaapp_prm392.services.PizzaApiService;
import fpt.edu.vn.pizzaapp_prm392.services.RetrofitClient;
import fpt.edu.vn.pizzaapp_prm392.services.SyncService;
import fpt.edu.vn.pizzaapp_prm392.utils.SessionManager;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends AppCompatActivity {
    private RecyclerView rv;
    private SearchView search;
    private ChipGroup filterBar;
    private ProgressBar pb;
    private LinearLayout empty;
    private ProductAdapter adapter;
    private PizzaApiService api;
    private PizzaDAO dao;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Double minP = 0.0, maxP = Double.MAX_VALUE;
    private String cat = "";
    private ListPopupWindow suggestionsPopup;
    private PizzaDAO pizzaDAO;
    private Pager<Integer, Pizza> pager;

    private Double currentMinPrice = 0.0;
    private Double currentMaxPrice = Double.MAX_VALUE;
    private String currentCategory = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        SessionManager sm = new SessionManager(this);
        if (!sm.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        init();
        setupRV();

        rv.post(() -> Log.d("PAGING_DEBUG", "RecyclerView đã attach adapter"));
        pizzaDAO = new PizzaDAO(this);


        new SyncService(this).syncFromApi(new SyncService.SyncCallback() {
            @Override
            public void onSyncComplete() {
                Log.d("UI", "Đồng bộ xong → LOAD danh sách");

                runOnUiThread(() -> {
                    Log.d("UI", "Đồng bộ xong → LOAD danh sách bên trong");
                    loadPizzasFromDB("", null, null, ""); // BÂY GIỜ MỚI LOAD
                });
            }
        });

        setupSearch();
        setupFilter();
    }

    private void init() {
        rv = findViewById(R.id.rv_products);
        search = findViewById(R.id.search_view);
        filterBar = findViewById(R.id.filter_bar);
        pb = findViewById(R.id.progress_bar);
        empty = findViewById(R.id.tv_empty);
        api = RetrofitClient.getClient().create(PizzaApiService.class);
        dao = new PizzaDAO(this);
    }

    private void setupRV() {
        adapter = new ProductAdapter(this, p -> {
            startActivity(new Intent(this, ProductDetailActivity.class).putExtra("pizza", p));
        });

        rv.setLayoutManager(new GridLayoutManager(this, 2));
        rv.setHasFixedSize(true);
        rv.setAdapter(adapter);
    }

    private void setupSearch() {
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(() -> {
                    fetchSuggestions(newText);
                    loadPizzasFromDB(newText, minP, maxP, cat);
                }, 400);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String q) {
                loadPizzasFromDB(q, minP, maxP, cat);
                if (suggestionsPopup != null) suggestionsPopup.dismiss();
                return true;
            }
        });

        FloatingActionButton btnCart = findViewById(R.id.btn_cart);
        btnCart.setOnClickListener(v -> {
            SessionManager sm = new SessionManager(this);
            if (!sm.isLoggedIn()) {
                Toast.makeText(this, "Vui lòng đăng nhập để xem giỏ hàng", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }

            Intent i = new Intent(this, CartActivity.class);
            startActivity(i);
        });

    }

    private void setupFilter() {
//        addChip("Giá", this::showPrice);
        addChip("Loại", this::showCat);
    }

    private void addChip(String text, Runnable action) {
        Chip c = new Chip(this);
        c.setText(text);
        c.setOnClickListener(v -> action.run());
        filterBar.addView(c);
    }

    private void showPrice() {
        if (getLayoutInflater() == null) return;

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_filter_price, null);
        if (dialogView == null) {
            Toast.makeText(this, "Lỗi layout", Toast.LENGTH_SHORT).show();
            return;
        }

        RangeSlider slider = dialogView.findViewById(R.id.range_slider);
        TextView tvMin = dialogView.findViewById(R.id.tv_min_price);
        TextView tvMax = dialogView.findViewById(R.id.tv_max_price);
        MaterialButton btnApply = dialogView.findViewById(R.id.btn_apply);
        MaterialButton btnClear = dialogView.findViewById(R.id.btn_clear);

        if (slider == null || tvMin == null || tvMax == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy RangeSlider", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Float> current = Arrays.asList(currentMinPrice.floatValue(), currentMaxPrice.floatValue());
        slider.setValues(current);
        tvMin.setText(String.format("%,.0f đ", currentMinPrice));
        tvMax.setText(String.format("%,.0f đ", currentMaxPrice));

        slider.addOnChangeListener((s, v, b) -> {
            List<Float> values = s.getValues();
            tvMin.setText(String.format("%,.0f đ", values.get(0)));
            tvMax.setText(String.format("%,.0f đ", values.get(1)));
        });

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Float> values = slider.getValues();
                currentMinPrice = values.get(0).doubleValue();
                currentMaxPrice = values.get(1).doubleValue();

                minP = currentMinPrice;
                maxP = currentMaxPrice;

                loadPizzasFromDB(search.getQuery().toString(), minP, maxP, cat);
                dialog.dismiss();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentMinPrice = 0.0;
                currentMaxPrice = Double.MAX_VALUE;

                minP = currentMinPrice;
                maxP = currentMaxPrice;

                slider.setValues(0f, 500000f);
                loadPizzasFromDB(search.getQuery().toString(), minP, maxP, cat);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showCat() {
        try {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_filter_category, null);
            RecyclerView rv = dialogView.findViewById(R.id.rv_categories);
            MaterialButton btnApply = dialogView.findViewById(R.id.btn_apply);
            MaterialButton btnClear = dialogView.findViewById(R.id.btn_clear);

            List<String> categories = pizzaDAO.getAllCategories();
            final String[] selectedTemp = {currentCategory};

            CategoryFilterAdapter adapter =
                    new CategoryFilterAdapter(categories, selectedTemp[0], cat -> {
                        selectedTemp[0] = cat;
                    });

            rv.setLayoutManager(new LinearLayoutManager(this));
            rv.setAdapter(adapter);

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setView(dialogView)
                    .create();

            btnApply.setOnClickListener(v -> {
                currentCategory = selectedTemp[0];
                cat = currentCategory;
                loadPizzasFromDB(search.getQuery().toString(), minP, maxP, cat);
                dialog.dismiss();
            });

            btnClear.setOnClickListener(v -> {
                currentCategory = "";
                cat = "";
                loadPizzasFromDB(search.getQuery().toString(), minP, maxP, cat);
                dialog.dismiss();
            });

            dialog.show();
        } catch (Exception e) {
            Log.e("CRASH", "Lỗi trong showPrice(): " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void fetchSuggestions(String text) {
        if (text.length() < 2) {
            if (suggestionsPopup != null) suggestionsPopup.dismiss();
            return;
        }

        List<String> suggestions = pizzaDAO.getSuggestions(text);
        showSuggestionsDropdown(suggestions);
    }

    private void showSuggestionsDropdown(List<String> suggestions) {
        if (suggestions.isEmpty()) {
            if (suggestionsPopup != null) suggestionsPopup.dismiss();
            return;
        }

        if (suggestionsPopup == null) {
            suggestionsPopup = new ListPopupWindow(this);
            suggestionsPopup.setAnchorView(search);
            suggestionsPopup.setWidth(ListPopupWindow.MATCH_PARENT);
            suggestionsPopup.setHeight(400);
            suggestionsPopup.setModal(false);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, suggestions);

        suggestionsPopup.setAdapter(adapter);
        suggestionsPopup.setOnItemClickListener((parent, view, position, id) -> {
            String selected = suggestions.get(position);
            search.setQuery(selected, true);
            suggestionsPopup.dismiss();
        });

        if (!suggestionsPopup.isShowing()) {
            suggestionsPopup.show();
        }
    }

    @SuppressLint("CheckResult")
    private void load(String q) {
        pb.setVisibility(View.VISIBLE);
        empty.setVisibility(View.GONE);

        RxProductPagingSource src = new RxProductPagingSource(dao, q, minP, maxP, cat);

        Pager<Integer, Pizza> pager = new Pager<>(new PagingConfig(20), () -> src);

        Flowable<PagingData<Pizza>> flowable = PagingRx.getFlowable(pager);

        flowable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> adapter.submitData(getLifecycle(), data), error -> {
                    pb.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                });
    }

    @SuppressLint("CheckResult")
    public void loadPizzasFromDB(String query, Double min, Double max, String cat) {
        Log.d("PAGING_DEBUG", "Tạo Pager mới...");

        if (pb == null || empty == null || adapter == null) return;

        pb.setVisibility(View.VISIBLE);
        empty.setVisibility(View.GONE);

        RxProductPagingSource pagingSource = new RxProductPagingSource(pizzaDAO, query, min, max, cat);

        pager =
                new Pager<>(
                        new PagingConfig(20, 3, false, 20),
                        () -> pagingSource);

        Flowable<PagingData<Pizza>> flowable = PagingRx.getFlowable(pager);

        flowable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pagingData -> {
                    Log.d("PAGING_DEBUG", "submitData() gọi xong");
                    adapter.submitData(getLifecycle(), pagingData);
                    Log.d("PAGING_DEBUG", "pagingData size = " + pagingData.toString());

                    pb.setVisibility(View.GONE);
                });

        adapter.addLoadStateListener(combinedLoadStates -> {
            LoadState refresh = combinedLoadStates.getRefresh();
            if (refresh instanceof LoadState.NotLoading && adapter.getItemCount() == 0) {
                empty.setVisibility(View.VISIBLE);
            } else {
                empty.setVisibility(View.GONE);
            }
            return null;
        });
    }
}
