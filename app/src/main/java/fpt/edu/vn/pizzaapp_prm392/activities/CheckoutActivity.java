package fpt.edu.vn.pizzaapp_prm392.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fpt.edu.vn.pizzaapp_prm392.R;
import fpt.edu.vn.pizzaapp_prm392.adapter.CartAdapter;
import fpt.edu.vn.pizzaapp_prm392.dao.CartItemDAO;
import fpt.edu.vn.pizzaapp_prm392.dao.OrderDAO;
import fpt.edu.vn.pizzaapp_prm392.dao.PizzaDAO;
import fpt.edu.vn.pizzaapp_prm392.models.CartItem;
import fpt.edu.vn.pizzaapp_prm392.models.Pizza;
import fpt.edu.vn.pizzaapp_prm392.utils.SessionManager;

public class CheckoutActivity extends AppCompatActivity implements CartAdapter.OnCartActionListener {

    private RecyclerView rv;
    private TextView tvTotal;
    private TextInputEditText etName, etPhone, etAddress, etNotes;
    private MaterialButton btnPlace;

    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private final List<CartItem> items = new ArrayList<>();
    private CartAdapter adapter;
    private Map<Integer, Pizza> pizzaMap = new HashMap<>();

    private int userId;
    private static final double SHIPPING_FEE = 15000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Toolbar tb = findViewById(R.id.toolbar_checkout);
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tb.setNavigationOnClickListener(v -> onBackPressed());

        rv = findViewById(R.id.rv_checkout);
        tvTotal = findViewById(R.id.tv_summary_total);
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        etNotes = findViewById(R.id.et_notes);
        btnPlace = findViewById(R.id.btn_place_order);

        SessionManager sm = new SessionManager(this);
        userId = sm.getUserId();
        if (!TextUtils.isEmpty(sm.getUserName())) etName.setText(sm.getUserName());
        if (!TextUtils.isEmpty(sm.getUserPhone())) etPhone.setText(sm.getUserPhone());
        if (!TextUtils.isEmpty(sm.getUserAddress())) etAddress.setText(sm.getUserAddress());

//        adapter = new CartAdapter(this, items, this, true); // readOnly=true
        adapter = new CartAdapter(this, items, new PizzaDAO(this), this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        loadSummary(); // gọi sau khi đã gắn adapter


        btnPlace.setOnClickListener(v -> placeOrder());
    }

    private void loadSummary() {
        io.execute(() -> {
            List<CartItem> data = new CartItemDAO(this).getCartByUser(userId);

            // map Pizza cho hiển thị
            List<Integer> ids = new ArrayList<>();
            for (CartItem c: data) if (!ids.contains(c.getPizzaId())) ids.add(c.getPizzaId());
            pizzaMap = new PizzaDAO(this).getPizzasByIds(ids);

            runOnUiThread(() -> {
                items.clear();
                items.addAll(data);
                adapter.setPizzaMap(pizzaMap);
                adapter.notifyDataSetChanged();
                updateTotal();
            });
        });
    }

    private void updateTotal() {
        double subtotal = 0;
        for (CartItem c: items) {
            double unit = pizzaMap.containsKey(c.getPizzaId()) ? pizzaMap.get(c.getPizzaId()).getPrice() : c.getPrice();
            subtotal += unit * c.getQuantity();
        }
        double total = subtotal + (items.isEmpty() ? 0 : SHIPPING_FEE);
        tvTotal.setText("Tổng: " + fmt(total));
    }

    private void placeOrder() {
        String name = t(etName), phone = t(etPhone), address = t(etAddress), notes = t(etNotes);
        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Nhập đủ họ tên, điện thoại, địa chỉ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (items.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            return;
        }

        double subtotal = 0;
        for (CartItem c: items) {
            double unit = pizzaMap.containsKey(c.getPizzaId()) ? pizzaMap.get(c.getPizzaId()).getPrice() : c.getPrice();
            subtotal += unit * c.getQuantity();
        }
        double total = subtotal + SHIPPING_FEE;

        io.execute(() -> {
            long orderId = new OrderDAO(this).createOrderFromCartAndPayment(
                    userId,
                    name, phone, address, notes,
                    "COD",          // hoặc lấy từ radio/selector phương thức thanh toán
                    items,
                    total           // nhớ là total đã gồm shipping
            );
            runOnUiThread(() -> {
                if (orderId > 0) {
                    Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
                    finish(); // hoặc điều hướng sang màn hình “Chi tiết đơn hàng”
                } else {
                    Toast.makeText(this, "Đặt hàng thất bại!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private String t(TextInputEditText e){ return e.getText()==null? "" : e.getText().toString().trim(); }

    private String fmt(double v){
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,###");
        return df.format(Math.max(0, v)).replace(",", ".") + " đ";
    }

    // readOnly nên 3 callback không dùng
    @Override public void onQuantityChanged(CartItem item, int newQty) {}
    @Override public void onRemove(CartItem item, int position) {}
}
