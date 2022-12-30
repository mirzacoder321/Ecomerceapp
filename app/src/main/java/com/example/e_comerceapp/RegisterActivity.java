package com.example.e_comerceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText reg_name, reg_phon, reg_pass;
    private Button btn_reg;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn_reg = findViewById(R.id.btn_reg);
        reg_name = findViewById(R.id.reg_name);
        reg_phon = findViewById(R.id.reg_phon);
        reg_pass = findViewById(R.id.reg_pass);
        loadingBar = new ProgressDialog(this);

        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount() {

        String nam = reg_name.getText().toString();
        String phon = reg_phon.getText().toString();
        String pass = reg_pass.getText().toString();

        if (TextUtils.isEmpty(nam)) {
            Toast.makeText(this, "Plz Enter your name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phon)) {
            Toast.makeText(this, "Plz Enter your phone number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Plz Enter your password", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Plz wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatephoneNumber(nam, phon, pass);
        }
    }

    private void ValidatephoneNumber(String nam, String phon, String pass) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!(snapshot.child("Users").child(phon).exists()))
                {
                    HashMap<String,Object> UserDataMap = new HashMap<>();
                    UserDataMap.put("Phone",phon);
                    UserDataMap.put("Password",pass);
                    UserDataMap.put("Name",nam);

                    RootRef.child("Users").child(phon).updateChildren(UserDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "Congratulation, your account have been created", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                    }
                                    else {
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Network Error: Plz try again after some time...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
                else
                {
                    Toast.makeText(RegisterActivity.this, "This" + phon + "already exit", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Please try again with another phone number", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}