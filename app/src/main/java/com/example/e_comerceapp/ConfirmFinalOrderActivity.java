package com.example.e_comerceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.e_comerceapp.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText nameEdittext,phoneEdittext,addressEdittext,cityEdittext;
    private Button confirmOrderBtn;

    private String totalAmount = "" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalAmount = getIntent().getStringExtra("Total Price");
        Toast.makeText(ConfirmFinalOrderActivity.this, "Total Price = Rs " + totalAmount, Toast.LENGTH_SHORT).show();

        nameEdittext = findViewById(R.id.shipment_name);
        phoneEdittext = findViewById(R.id.shipment_phone_num);
        addressEdittext = findViewById(R.id.shipment_address);
        cityEdittext = findViewById(R.id.shipment_city);
        confirmOrderBtn =findViewById(R.id.confirm_final_order_btn);
        
        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                CheckField();
            }
        });

    }

    private void CheckField() {
        
        if (TextUtils.isEmpty(nameEdittext.getText().toString()))
        {
            Toast.makeText(ConfirmFinalOrderActivity.this, "Plz provide your name", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(phoneEdittext.getText().toString()))
        {
            Toast.makeText(ConfirmFinalOrderActivity.this, "Plz provide your phone number", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(addressEdittext.getText().toString()))
        {
            Toast.makeText(ConfirmFinalOrderActivity.this, "Plz provide your address", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(cityEdittext.getText().toString()))
        {
            Toast.makeText(ConfirmFinalOrderActivity.this, "Plz provide your city name", Toast.LENGTH_SHORT).show();
        }else
        {
            ConfirmOrder();            
        }
    }

    private void ConfirmOrder()
    {

        final String saveCurrentTime,saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUsers.getPhone());

        HashMap<String,Object> ordersMap = new HashMap<>();

        ordersMap.put("totalAmount", totalAmount);
        ordersMap.put("name", nameEdittext.getText().toString());
        ordersMap.put("phone", phoneEdittext.getText().toString());
        ordersMap.put("address", addressEdittext.getText().toString());
        ordersMap.put("city", cityEdittext.getText().toString());
        ordersMap.put("date", saveCurrentDate);
        ordersMap.put("time", saveCurrentTime);
        ordersMap.put("state" , "not shipped");

//  when the user confirm the order we have to empty the cart activity
        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Cart List")
                            .child("User View")
                            .child(Prevalent.currentOnlineUsers.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(ConfirmFinalOrderActivity.this, "Your final order have been replaced successfully.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ConfirmFinalOrderActivity.this,HomeActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });

    }
}