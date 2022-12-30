package com.example.e_comerceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigator;

import com.example.e_comerceapp.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.jar.Attributes;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText fullNameEditText,userPhoneEditText,addressEditText;
    private TextView profileChangeTextBtn, closeTextBtn;
    private Button update_profile;

    private Uri imageUri;
    private String myUrl = "";
    private StorageReference storageProfilePictureRef;
    private StorageTask uploadTask;
    private String checker = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Profile Pictures");

        profileImageView =findViewById(R.id.setting_profile_ImageView);
        fullNameEditText=findViewById(R.id.setting_full_name);
        userPhoneEditText=findViewById(R.id.setting_phone_num);
        addressEditText=findViewById(R.id.setting_address);
        profileChangeTextBtn=findViewById(R.id.setting_profile_imgChange_btn);
        closeTextBtn=findViewById(R.id.setting_close);
        update_profile=findViewById(R.id.setting_update_profile_btn);

        userinfodisply(profileImageView,fullNameEditText,userPhoneEditText,addressEditText);


        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checker.equals("clicked")){

                    userInfoSaved();

                }else {

                    updateOnlyUserInfo();
                }
            }
        });

        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "clicked";
                // start cropping activity for pre-acquired image saved on the device
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingActivity.this);
            }
        });

    }

    private void updateOnlyUserInfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String,Object> userMap = new HashMap<>();

        userMap.put("Name",fullNameEditText.getText().toString());
        userMap.put("Address",addressEditText.getText().toString());
        userMap.put("PhoneOrder",userPhoneEditText.getText().toString());
        ref.child(Prevalent.currentOnlineUsers.getPhone()).updateChildren(userMap);


        startActivity(new Intent(SettingActivity.this,HomeActivity.class));
        Toast.makeText(SettingActivity.this, "Profile info update successfully", Toast.LENGTH_SHORT).show();
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profileImageView.setImageURI(imageUri);
        }else
        {
            Toast.makeText(SettingActivity.this, "Error,Try again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingActivity.this,SettingActivity.class));
        }
    }

    private void userInfoSaved() {

        if(TextUtils.isEmpty(fullNameEditText.getText().toString())){
            Toast.makeText(this, "Name is mandatory....", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(this, "Address is mandatory....", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(userPhoneEditText.getText().toString())){
            Toast.makeText(this, "Phone_number is mandatory....", Toast.LENGTH_SHORT).show();
        }else if (checker.equals("clicked")){
            uploadImage();
        }


        
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setTitle("Plz wait we are updating your profile information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null) {
            final StorageReference fileRef = storageProfilePictureRef
                    .child(Prevalent.currentOnlineUsers.getPhone() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);// upload image in storage

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!uploadTask.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if (task.isSuccessful())
                    {
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                        HashMap<String,Object> userMap = new HashMap<>();

                        userMap.put("Name",fullNameEditText.getText().toString());
                        userMap.put("Address",addressEditText.getText().toString());
                        userMap.put("PhoneOrder",userPhoneEditText.getText().toString());
                        userMap.put("ImageUrl", myUrl);
                        ref.child(Prevalent.currentOnlineUsers.getPhone()).updateChildren(userMap);

                        progressDialog.dismiss();

                        startActivity(new Intent(SettingActivity.this,HomeActivity.class));
                        Toast.makeText(SettingActivity.this, "Profile info update successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(SettingActivity.this, "Error...", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else
            {
                Toast.makeText(this, "Image is not selected", Toast.LENGTH_SHORT).show();
            }


    }

    private void userinfodisply(CircleImageView profileImageView, EditText fullNameEditText, EditText userPhoneEditText, EditText addressEditText)
    {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUsers.getPhone());
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.child("ImageUrl").exists()){
                        String image = snapshot.child("ImageUrl").getValue().toString();
                        String name = snapshot.child("Name").getValue().toString();
                        String phone = snapshot.child("Phone").getValue().toString();
                        String address = snapshot.child("Address").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                        fullNameEditText.setText(name);
                        userPhoneEditText.setText(phone);
                        addressEditText.setText(address);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}