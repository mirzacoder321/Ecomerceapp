package com.example.e_comerceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class AdminMaintainProductsActivity extends AppCompatActivity {

    private Button apply_changes;
    private EditText name,price,description;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_products);

        apply_changes = findViewById(R.id.apply_changes);
        name= findViewById(R.id.product_name_maintain);
        price = findViewById(R.id.product_price_maintain);
        description = findViewById(R.id.product_description_maintain);
        imageView =findViewById(R.id.product_image_maintain);

    }
}