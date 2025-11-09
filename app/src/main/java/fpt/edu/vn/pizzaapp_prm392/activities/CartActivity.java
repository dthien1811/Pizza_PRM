package fpt.edu.vn.pizzaapp_prm392.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fpt.edu.vn.pizzaapp_prm392.R;
import fpt.edu.vn.pizzaapp_prm392.adapter.CartAdapter;
import fpt.edu.vn.pizzaapp_prm392.dao.CartItemDAO;
import fpt.edu.vn.pizzaapp_prm392.dao.PizzaDAO;
import fpt.edu.vn.pizzaapp_prm392.models.CartItem;
import fpt.edu.vn.pizzaapp_prm392.models.Pizza;
import fpt.edu.vn.pizzaapp_prm392.utils.SessionManager;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartActionListener {

    private Toolbar toolbar;
    private RecyclerView rvCart;
    private View emptyView;
    private TextView tvSubtotal, tvShipping, tvTotal;
    private Button btnCheckout;

    private CartAdapter adapter;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    private int userId; // FIELD của class (KHÔNG khai báo đè trong onCreate)
    private final List<CartItem> items = new ArrayList<>();

    // cấu hình phí ship demo
    private static final double SHIPPING_FEE = 15000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // <-- gọi super trước
        setContentView(R.layout.activity_cart);

        // Lấy userId từ session (GÁN VÀO FIELD this.userId)
        SessionManager sm = new SessionManager(this);
        this.userId = sm.getUserId();
        if (this.userId == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        bindViews();
        setupToolbar();
        setupRecycler();

        // Lần đầu vào màn hình, load dữ liệu
        loadCart();

        btnCheckout.setOnClickListener(v -> {
            if (items.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, CheckoutActivity.class));
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Mỗi lần quay lại màn hình cart → reload DB để có dữ liệu mới nhất
        loadCart();
    }

    private void bindViews() {
        toolbar = findViewById(R.id.toolbar_cart);
        rvCart = findViewById(R.id.rv_cart);
        emptyView = findViewById(R.id.layout_empty_cart);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvShipping = findViewById(R.id.tv_shipping_fee);
        tvTotal = findViewById(R.id.tv_total);
        btnCheckout = findViewById(R.id.btn_checkout);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Giỏ hàng");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecycler() {
        adapter = new CartAdapter(this, items, new PizzaDAO(this), this);
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        rvCart.setAdapter(adapter);
    }

    private void loadCart() {
        io.execute(() -> {
            CartItemDAO cartDao = new CartItemDAO(this);
            List<CartItem> data = cartDao.getCartByUser(this.userId);

            // Gom danh sách pizzaId có trong giỏ
            List<Integer> ids = new ArrayList<>();
            for (CartItem ci : data) {
                if (!ids.contains(ci.getPizzaId())) ids.add(ci.getPizzaId());
            }

            // Lấy thông tin pizza 1 lần → map
            PizzaDAO pdao = new PizzaDAO(this);
            Map<Integer, Pizza> pizzaMap = pdao.getPizzasByIds(ids);

            runOnUiThread(() -> {
                items.clear();
                items.addAll(data);
                adapter.setPizzaMap(pizzaMap);   // >>> quan trọng <<<
                adapter.notifyDataSetChanged();
                updateSummary(pizzaMap);
                toggleEmpty();
            });
        });
    }


    private void toggleEmpty() {
        boolean isEmpty = items.isEmpty();
        rvCart.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        if (emptyView != null) emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        btnCheckout.setEnabled(!isEmpty);
    }

    private void updateSummary() {
        updateSummary(null);
    }

    private void updateSummary(Map<Integer, fpt.edu.vn.pizzaapp_prm392.models.Pizza> pizzaMap) {
        double subtotal = 0;
        for (CartItem c : items) {
            // Nếu có Pizza trong map thì lấy giá mới nhất; nếu không dùng giá lưu trong CartItem
            double unit = c.getPrice();
            if (pizzaMap != null && pizzaMap.containsKey(c.getPizzaId())) {
                unit = pizzaMap.get(c.getPizzaId()).getPrice();
            }
            subtotal += unit * c.getQuantity();
        }
        double shipping = items.isEmpty() ? 0 : SHIPPING_FEE;
        double total = subtotal + shipping;

        tvSubtotal.setText(fmt(subtotal));
        tvShipping.setText(fmt(shipping));
        tvTotal.setText(fmt(total));
    }

    private String fmt(double v) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(Math.max(0, v)).replace(",", ".") + " đ";
    }

    /* ==== Callbacks từ Adapter ==== */

    @Override
    public void onQuantityChanged(CartItem item, int newQty) {
        io.execute(() -> {
            new CartItemDAO(this).updateCartQuantity(item.getCartId(), newQty);
            item.setQuantity(newQty);
            runOnUiThread(this::updateSummary);
        });
    }

    @Override
    public void onRemove(CartItem item, int position) {
        io.execute(() -> {
            new CartItemDAO(this).removeFromCart(item.getCartId());
            runOnUiThread(() -> {
                items.remove(position);
                adapter.notifyItemRemoved(position);
                updateSummary();
                toggleEmpty();
                Toast.makeText(this, "Đã xoá khỏi giỏ", Toast.LENGTH_SHORT).show();
            });
        });
    }
}
