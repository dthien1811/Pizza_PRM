package fpt.edu.vn.pizzaapp_prm392.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import fpt.edu.vn.pizzaapp_prm392.R;
import fpt.edu.vn.pizzaapp_prm392.models.Pizza;

public class ProductAdapter extends PagingDataAdapter<Pizza, ProductAdapter.ViewHolder> {
    private Context context;
    private OnClick listener;

    public ProductAdapter(Context context, OnClick listener) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.listener = listener;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pizza pizza = getItem(position);
        if (pizza != null) holder.bind(pizza);
    }

    private static final DiffUtil.ItemCallback<Pizza> DIFF_CALLBACK = new DiffUtil.ItemCallback<Pizza>() {
        @Override
        public boolean areItemsTheSame(@NonNull Pizza oldItem, @NonNull Pizza newItem) {
            return oldItem.getPizzaId() == newItem.getPizzaId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Pizza oldItem, @NonNull Pizza newItem) {
            return oldItem.equals(newItem);
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView name, price;
        RatingBar rating;

        ViewHolder(@NonNull View v) {
            super(v);
            iv = v.findViewById(R.id.iv_image);
            name = v.findViewById(R.id.tv_name);
            price = v.findViewById(R.id.tv_price);
            rating = v.findViewById(R.id.rating_bar);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Pizza p = getItem(pos);
                        if (p != null) listener.onClick(p);
                    }
                }
            });
        }

        void bind(Pizza p) {
            name.setText(p.getName());
            price.setText(String.format("%,.0f đ", p.getPrice()));
            rating.setRating((float) p.getRating());
            Glide.with(context).load(p.getImage())
                    .override(300, 300)
                    .placeholder(R.drawable.placeholder)
                    .into(iv);

        }
    }

    public interface OnClick {
        void onClick(Pizza pizza);
    }

}
