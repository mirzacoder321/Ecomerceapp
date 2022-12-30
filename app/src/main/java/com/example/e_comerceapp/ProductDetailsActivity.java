package com.example.e_comerceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_comerceapp.Model.Products;
import com.example.e_comerceapp.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    int minteger = 1;
    private Button add_to_cart;
    private ImageView product_image_detail;
    private TextView number_of_products;
    private TextView productPrice_detail, productdescription_detail, productName_detail;
    private String productID = "", state = "Normal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productID = getIntent().getStringExtra("pid");

        add_to_cart = findViewById(R.id.add_to_cart);
        product_image_detail = findViewById(R.id.product_image_details);
        number_of_products = findViewById(R.id.no_of_products);
        productPrice_detail = findViewById(R.id.product_price_details);
        productdescription_detail = findViewById(R.id.product_description_details);
        productName_detail = findViewById(R.id.product_name_details);

        getProductDetails(productID);

        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (state.equals("Order Placed") || state.equals("Order Shipped")) {
                    Toast.makeText(ProductDetailsActivity.this, "you can add or purchase more products, once your order is shipped or confirmed", Toast.LENGTH_LONG).show();
                } else {
                    addingToCartList();
                }
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        CheckOrderState();
    }

    private void addingToCartList() {

        String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", productID);
        cartMap.put("pname", productName_detail.getText().toString());
        cartMap.put("price", productPrice_detail.getText().toString());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("quantity", number_of_products.getText().toString());
        cartMap.put("discount", "");

        cartListRef.child("User View").child(Prevalent.currentOnlineUsers.getPhone())
                .child("Products").child(productID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        cartListRef.child("Admin View").child(Prevalent.currentOnlineUsers.getPhone())
                                .child("Products").child(productID)
                                .updateChildren(cartMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ProductDetailsActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                });
                    }
                });

    }

    private void getProductDetails(String productID) {

        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        productsRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Products products = snapshot.getValue(Products.class);

                    productName_detail.setText(products.getPname());
                    productdescription_detail.setText(products.getDescription());
                    productPrice_detail.setText(products.getPrice());
                    Picasso.get().load(products.getImage()).into(product_image_detail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void increaseInteger(View view) {
        if (minteger <= 9) {
            minteger = minteger + 1;
            display(minteger);
        }


    }

    public void decreaseInteger(View view) {
        if (minteger > 1) {
            minteger = minteger - 1;
            display(minteger);
        }
    }

    private void display(int number) {
        TextView displayInteger = (TextView) findViewById(
                R.id.no_of_products);
        displayInteger.setText("" + number);
    }

    private void CheckOrderState() {
        DatabaseReference ordersRef;
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUsers.getPhone());

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String shippingState = snapshot.child("state").getValue().toString();


                    if (shippingState.equals("shipped")) {
                        state = "Order Shipped";
                    } else if (shippingState.equals("not shipped")) {
                        state = "Order Placed";
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}