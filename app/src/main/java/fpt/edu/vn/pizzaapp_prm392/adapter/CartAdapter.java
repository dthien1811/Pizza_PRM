package fpt.edu.vn.pizzaapp_prm392.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fpt.edu.vn.pizzaapp_prm392.R;
import fpt.edu.vn.pizzaapp_prm392.dao.PizzaDAO;
import fpt.edu.vn.pizzaapp_prm392.models.CartItem;
import fpt.edu.vn.pizzaapp_prm392.models.Pizza;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.VH> {

    public interface OnCartActionListener {
        void onQuantityChanged(CartItem item, int newQty);
        void onRemove(CartItem item, int position);
    }

    private final Context ctx;
    private final List<CartItem> items;
    private final OnCartActionListener listener;
    private Map<Integer, Pizza> pizzaMap = new HashMap<>();

    public CartAdapter(Context ctx, List<CartItem> items, PizzaDAO pizzaDAO, OnCartActionListener l) {
        this.ctx = ctx;
        this.items = items;
        this.listener = l;
    }

    public void setPizzaMap(Map<Integer, Pizza> map) {
        this.pizzaMap = (map != null) ? map : new HashMap<>();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_cart, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        CartItem ci = items.get(position);
        Pizza p = pizzaMap.get(ci.getPizzaId());

        // Tên, size, ảnh
        h.tvName.setText(p != null ? p.getName() : ("#" + ci.getPizzaId()));
        h.tvOptions.setText(p != null && p.getSize() != null ? "Size: " + p.getSize() : "Size: M");
        if (p != null && p.getImage() != null && !p.getImage().isEmpty()) {
            Glide.with(ctx).load(p.getImage()).into(h.ivThumb);
        } else {
            h.ivThumb.setImageResource(android.R.color.darker_gray);
        }

        // Giá
        double unit = (p != null ? p.getPrice() : ci.getPrice());
        h.tvUnitPrice.setText(fmt(unit));
        h.tvLineTotal.setText(fmt(unit * ci.getQuantity()));
        h.tvQty.setText(String.valueOf(ci.getQuantity()));

        // Nút trừ/cộng
        h.btnMinus.setOnClickListener(v -> {
            int q = Math.max(1, ci.getQuantity() - 1);
            if (q != ci.getQuantity()) {
                listener.onQuantityChanged(ci, q);
                h.tvQty.setText(String.valueOf(q));
                h.tvLineTotal.setText(fmt(unit * q));
            }
        });
        h.btnPlus.setOnClickListener(v -> {
            int q = ci.getQuantity() + 1;
            listener.onQuantityChanged(ci, q);
            h.tvQty.setText(String.valueOf(q));
            h.tvLineTotal.setText(fmt(unit * q));
        });

        h.btnRemove.setOnClickListener(v -> listener.onRemove(ci, h.getBindingAdapterPosition()));
    }

    @Override
    public int getItemCount() { return items.size(); }

    private String fmt(double v) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(Math.max(0, v)).replace(",", ".") + " đ";
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvName, tvOptions, tvUnitPrice, tvLineTotal, tvQty;
        ImageButton btnMinus, btnPlus, btnRemove;

        VH(@NonNull View v) {
            super(v);
            ivThumb = v.findViewById(R.id.iv_thumb);
            tvName = v.findViewById(R.id.tv_item_name);
            tvOptions = v.findViewById(R.id.tv_item_options);
            tvUnitPrice = v.findViewById(R.id.tv_unit_price);
            tvLineTotal = v.findViewById(R.id.tv_line_total);
            tvQty = v.findViewById(R.id.tv_quantity);
            btnMinus = v.findViewById(R.id.btn_minus);
            btnPlus = v.findViewById(R.id.btn_plus);
            btnRemove = v.findViewById(R.id.btn_remove);
        }
    }
}
