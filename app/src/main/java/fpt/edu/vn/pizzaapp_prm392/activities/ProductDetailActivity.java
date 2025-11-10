package fpt.edu.vn.pizzaapp_prm392.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fpt.edu.vn.pizzaapp_prm392.R;
import fpt.edu.vn.pizzaapp_prm392.dao.CartItemDAO;
import fpt.edu.vn.pizzaapp_prm392.models.CartItem;
import fpt.edu.vn.pizzaapp_prm392.models.Pizza;
import fpt.edu.vn.pizzaapp_prm392.utils.SessionManager;

public class ProductDetailActivity extends AppCompatActivity {

    private Pizza pizza;                 // giữ pizza để dùng lại
    private Button btnAddToCart;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Bind UI
        ImageView iv = findViewById(R.id.iv_image);
        TextView name = findViewById(R.id.tv_name);
        TextView desc = findViewById(R.id.tv_desc);
        TextView price = findViewById(R.id.tv_price);
        RatingBar rating = findViewById(R.id.rating_bar);
        TextView stock = findViewById(R.id.tv_stock);
        TextView size = findViewById(R.id.tv_size);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);

        // Nhận pizza
        pizza = getIntent().getParcelableExtra("pizza");
        if (pizza != null) {
            name.setText(pizza.getName());
            desc.setText(pizza.getDescription());
            price.setText(String.format("%,.0f đ", pizza.getPrice()).replace(",", "."));
            rating.setRating((float) pizza.getRating());
            stock.setText(String.format("Còn %d sản phẩm", pizza.getStock()));
            size.setText(String.format("Kích thước: %s", pizza.getSize()));

            Glide.with(this).load(pizza.getImage()).into(iv);
        }

        // CLICK: Thêm vào giỏ
        btnAddToCart.setOnClickListener(v -> addToCart());
    }

    private void addToCart() {
        if (pizza == null) {
            Toast.makeText(this, "Thiếu thông tin sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        SessionManager sm = new SessionManager(this);
        int userId = sm.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập để thêm giỏ hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        // đơn giản: quantity = 1
        CartItem item = new CartItem(userId, pizza.getPizzaId(), 1, pizza.getPrice());

        io.execute(() -> {
            long id = new CartItemDAO(this).addToCart(item); // DAO tự cộng quantity nếu đã có
            runOnUiThread(() -> {
                if (id > 0) {
                    Toast.makeText(this, "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show();
                    // Mở màn giỏ để thấy cập nhật (CartActivity tự reload ở onResume)
                    startActivity(new Intent(this, CartActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                } else {
                    Toast.makeText(this, "Thêm giỏ thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
