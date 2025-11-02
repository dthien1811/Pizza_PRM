package fpt.edu.vn.pizzaapp_prm392.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryFilterAdapter extends RecyclerView.Adapter<CategoryFilterAdapter.ViewHolder> {
    private List<String> categories;
    private String selected;
    private OnCategorySelected listener;

    public CategoryFilterAdapter(List<String> categories, String selected, OnCategorySelected listener) {
        this.categories = categories;
        this.selected = selected;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_single_choice, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String cat = categories.get(position);
        holder.radio.setText(cat);
        holder.radio.setChecked(cat.equals(selected));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = cat;
                notifyDataSetChanged();
                listener.onSelected(cat);
            }
        });

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckedTextView radio;
        ViewHolder(@NonNull View v) {
            super(v);
            radio = (CheckedTextView) v;
        }
    }

    public interface OnCategorySelected {
        void onSelected(String category);
    }
}
