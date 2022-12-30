package com.example.e_comerceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.e_comerceapp.Model.Users;
import com.example.e_comerceapp.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button btn_join,btn_login;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_join=findViewById(R.id.btn_join);
        btn_login=findViewById(R.id.btn_login);
        loadingBar = new ProgressDialog(this);

        Paper.init(this);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);


            }
        });

        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

        if (UserPhoneKey != "" && UserPasswordKey != ""){
            if (!TextUtils.isEmpty(UserPhoneKey)  &&  !TextUtils.isEmpty(UserPasswordKey))
            {
                AllowAcess(UserPhoneKey,UserPasswordKey);

                loadingBar.setTitle("Already Login");
                loadingBar.setMessage("Plz wait.....");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }

        }

    }

    private void AllowAcess(String phone, String password) {


        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Users").child(phone).exists()){

                    Users usersData = snapshot.child("Users").child(phone).getValue(Users.class);
                    if (usersData.getPhone().equals(phone)){

                        if (usersData.getPassword().equals(password)){
                            Toast.makeText(MainActivity.this, "Please wait, your are already logged in....", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent intent=new Intent(MainActivity.this,HomeActivity.class);
                            Prevalent.currentOnlineUsers = usersData;
                            startActivity(intent);
                        }
                        else {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                        }

                    }

                }else {
                    Toast.makeText(MainActivity.this, "Account with this " + phone + "number do not exit", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this, "You need to create a new account", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}