package com.example.e_comerceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String CategoryName,Description,Price,Pname,saveCurrentDate,saveCurrentTime;
    private ImageView input_product_image;
    private Button add_new_product;
    private EditText input_product_name, input_product_description, input_product_price;
    private TextView Name;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String productRandomKey,downloadImageUrl;
    private StorageReference ProductImageRef;
    private DatabaseReference ProductRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        input_product_image=findViewById(R.id.select_product_image);
        add_new_product=findViewById(R.id.add_new_product);
        input_product_name=findViewById(R.id.product_name);
        input_product_description=findViewById(R.id.product_description);
        input_product_price=findViewById(R.id.product_price);
        Name=findViewById(R.id.Name);
        loadingBar = new ProgressDialog(this);


        CategoryName = getIntent().getExtras().get("category").toString();
        Name.setText(CategoryName);
        ProductImageRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductRef = FirebaseDatabase.getInstance().getReference().child("Products");
//        Toast.makeText(AdminAddNewProductActivity.this, CategoryName, Toast.LENGTH_SHORT).show();


        //upload image point 1
        input_product_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });
        //upload image point 4
        //we have to save image in storage
        add_new_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getting input text from admin now store in string
                Pname=input_product_name.getText().toString();
                Description = input_product_description.getText().toString();
                Price = input_product_price.getText().toString();
                
                if (ImageUri==null){
                    Toast.makeText(AdminAddNewProductActivity.this, "Product image is Mandatory........", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(Pname)){
                    Toast.makeText(AdminAddNewProductActivity.this, "Please write product name........", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(Description)){
                    Toast.makeText(AdminAddNewProductActivity.this, "Please write product description........", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(Price)){
                    Toast.makeText(AdminAddNewProductActivity.this, "Please write product price........", Toast.LENGTH_SHORT).show();
                }else {
                    StoreProductInformation();
                }
            }
            //upload image point 5
            private void StoreProductInformation() {

                loadingBar.setTitle("Add new product");
                loadingBar.setMessage("Dear Admin, plz wait we are adding the new product.");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("MM dd,yyyy");
                saveCurrentDate = currentDate.format(calendar.getTime());

                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                saveCurrentTime = currentTime.format(calendar.getTime());

                //upload image point 6 storing data with date and time

                productRandomKey = saveCurrentDate + saveCurrentTime;

                StorageReference filePath = ProductImageRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");

                final UploadTask uploadTask = filePath.putFile(ImageUri);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        String message = e.toString();
                        Toast.makeText(AdminAddNewProductActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(AdminAddNewProductActivity.this, "Upload Image Successfully.....", Toast.LENGTH_SHORT).show();

                        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful())
                                {
                                    throw task.getException();
                                }
                                downloadImageUrl = filePath.getDownloadUrl().toString();
                                return filePath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                if (task.isSuccessful()){

                                    downloadImageUrl = task.getResult().toString();
                                    Toast.makeText(AdminAddNewProductActivity.this, "Got product image Url Successfully", Toast.LENGTH_SHORT).show();

                                    SaveProductInfoToDatabase();
                                }
                            }
                        });
                    }
                });

            }
        });
    }

    private void SaveProductInfoToDatabase() {

        HashMap<String,Object> prodctMap =new HashMap<>();
        prodctMap.put("pid",productRandomKey);
        prodctMap.put("date",saveCurrentDate);
        prodctMap.put("time",saveCurrentTime);
        prodctMap.put("description",Description);
        prodctMap.put("image",downloadImageUrl);
        prodctMap.put("category",CategoryName);
        prodctMap.put("price",Price);
        prodctMap.put("pname",Pname);

        ProductRef.child(productRandomKey).updateChildren(prodctMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            Intent intent=new Intent(AdminAddNewProductActivity.this,AdminCategoryActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            loadingBar.dismiss();
                            Toast.makeText(AdminAddNewProductActivity.this, "Product is added successfully....", Toast.LENGTH_SHORT).show();
                        }else {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Error" + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //upload image point 2
    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GalleryPick);
    }
    //upload image point 3
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick && resultCode==RESULT_OK && data!=null){
            ImageUri = data.getData();
            input_product_image.setImageURI(ImageUri);
        }
    }
}