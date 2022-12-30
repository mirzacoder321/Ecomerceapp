package com.example.e_comerceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
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

public class LoginActivity extends AppCompatActivity {

    private EditText phone_num,login_pass;
    private Button login;
    private ProgressDialog loadingBar;
    private String parentdbName = "Users";
    private CheckBox chkBoxRememberMe;
//    private ImageView show_pass;
    private TextView Admin,User;
    boolean passwordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phone_num=findViewById(R.id.phone_num);
        login_pass=findViewById(R.id.login_pass);
        login=findViewById(R.id.login);
        User=findViewById(R.id.user);
        Admin=findViewById(R.id.admin);
//        show_pass=findViewById(R.id.show_pass);
        loadingBar = new ProgressDialog(this);
        chkBoxRememberMe=findViewById(R.id.checkBox);
        Paper.init(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });

        //for hide and unhide password
        login_pass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                final int Right=2;
                if (motionEvent.getAction()==MotionEvent.ACTION_UP){
                    if(motionEvent.getRawX()>login_pass.getRight()-login_pass.getCompoundDrawables()[Right].getBounds().width()){
                        int selection=login_pass.getSelectionEnd();
                        if (passwordVisible){
                            //set drawable image
                            login_pass.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.hide_password,0);
                            // hide password
                            login_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible=false;
                        }else {
                            //set drawable image
                            login_pass.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.show_pass,0);
                            // for show password
                            login_pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible=true;

                        }
                        login_pass.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        Admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login.setText("Login Admin");
                Admin.setVisibility(View.INVISIBLE);
                User.setVisibility(View.VISIBLE);
                parentdbName = "Admins";
            }
        });
        User.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login.setText("Login User");
                Admin.setVisibility(View.VISIBLE);
                User.setVisibility(View.INVISIBLE);
                parentdbName = "Users";
            }
        });




    }

    private void LoginUser() {

        String phone = phone_num.getText().toString();
        String password = login_pass.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Plz Enter your phone number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Plz Enter your password", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Plz wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAcessToAccount(phone, password);
        }
    }

    private void AllowAcessToAccount(String phone, String password) {

        if (chkBoxRememberMe.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey,phone);
            Paper.book().write(Prevalent.UserPasswordKey,password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(parentdbName).child(phone).exists()){

                    Users usersData = snapshot.child(parentdbName).child(phone).getValue(Users.class);
                    if (usersData.getPhone().equals(phone)){

                        if (usersData.getPassword().equals(password)){

                            if (parentdbName.equals("Admins")){
                                Toast.makeText(LoginActivity.this, "Welcome Admin, You are Logged in Successfully", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent intent=new Intent(LoginActivity.this,AdminCategoryActivity.class);
                                startActivity(intent);

                            }else if(parentdbName.equals("Users")){
                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                                Prevalent.currentOnlineUsers = usersData;
                                startActivity(intent);
                            }
                        }
                        else {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                        }

                    }

                }else {
                    Toast.makeText(LoginActivity.this, "Account with this " + phone + "number do not exit", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "You need to create a new account", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}



// logout=findViewById(R.id.logout);
//
//logout.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View view) {
//
//        Paper.book().destroy();
//        Intent intent=new Intent(HomeActivity.this,MainActivity.class);
//        startActivity(intent);
//        }
//        });