package fpt.edu.vn.pizzaapp_prm392.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

import fpt.edu.vn.pizzaapp_prm392.R;
import fpt.edu.vn.pizzaapp_prm392.models.Pizza;

public class ProductDetailActivity extends AppCompatActivity {
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

        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });


        Pizza p = getIntent().getParcelableExtra("pizza");
        if (p != null) {
            ImageView iv = findViewById(R.id.iv_image);
            TextView name = findViewById(R.id.tv_name);
            TextView desc = findViewById(R.id.tv_desc);
            TextView price = findViewById(R.id.tv_price);
            RatingBar rating = findViewById(R.id.rating_bar);

            name.setText(p.getName());
            desc.setText(p.getDescription());
            price.setText(String.format("%,.0f đ", p.getPrice()));
            rating.setRating((float) p.getRating());
            Glide.with(this).load(p.getImage()).into(iv);
        }

    }
}
