package com.maad.buyfromcandroidadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {

    private String category;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        RadioGroup radioGroup = findViewById(R.id.rg);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_clothes:
                        category = "Clothes";
                        break;

                    case R.id.rb_electronics:
                        category = "Electronics";
                        break;

                    case R.id.rb_furniture:
                        category = "Furniture";
                        break;
                }
            }
        });

    }

    public void choosePicture(View view) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            imageUri = data.getData();
            ImageButton productIB = findViewById(R.id.ib_product);
            productIB.setImageURI(imageUri);
        }
    }

    public void addProduct(View view) {
        uploadImage();
    }

    private void uploadImage() {
        //Accessing Cloud Storage bucket by creating an instance of FirebaseStorage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        //Create a reference to upload, download, or delete a file
        StorageReference storageRef = storage.getReference().child(imageUri.getLastPathSegment());
        storageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d("trace", "Image uploaded");
                        //we should get a download link now
                        getLinkForUploadedImage(storageRef.getDownloadUrl());
                    }
                });
    }

    private void getLinkForUploadedImage(Task<Uri> task) {
        Log.d("trace", "Getting image download link");
        task.addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("trace", "Image Link: " + uri);
                uploadProduct(uri);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("trace", "Failed to get download image: " + e.toString());
                    }
                });
    }

    private void uploadProduct(Uri imageUri) {
        Log.d("trace", "Uploading product...");
        EditText titleET = findViewById(R.id.et_title);
        EditText descET = findViewById(R.id.et_description);
        EditText priceET = findViewById(R.id.et_price);
        EditText quantityET = findViewById(R.id.et_quantity);

        String title = titleET.getText().toString();
        String desc = descET.getText().toString();
        String price = priceET.getText().toString();
        String quantity = quantityET.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> product = new HashMap<>();
        product.put("title", title);
        product.put("description", desc);
        product.put("price", Double.valueOf(price));
        product.put("quantity", Integer.valueOf(quantity));
        product.put("category", category);
        product.put("image", imageUri.toString());

        db
                .collection("products")
                .add(product)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AddProductActivity.this, "Product Added", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}