package com.maad.buyfromcandroidadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class EditProductActivity extends AppCompatActivity {

    private ProductModel product;
    private Button editBtn;
    private TextInputEditText titleET;
    private TextInputEditText descET;
    private TextInputEditText priceET;
    private TextInputEditText quantityET;
    private RadioGroup radioGroup;
    private Uri imageUri; //by default null
    private ImageButton productIB;
    private ProgressBar progressBar;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        editBtn = findViewById(R.id.btn_upload);
        titleET = findViewById(R.id.et_title);
        descET = findViewById(R.id.et_description);
        priceET = findViewById(R.id.et_price);
        quantityET = findViewById(R.id.et_quantity);
        radioGroup = findViewById(R.id.rg);
        productIB = findViewById(R.id.ib_product);
        progressBar = findViewById(R.id.progress_bar);

        product = getIntent().getParcelableExtra("product");

        if (savedInstanceState != null) {
            progressBar.setVisibility(savedInstanceState.getInt("pbVisibility"));
            editBtn.setVisibility(savedInstanceState.getInt("btnVisibility"));
            imageUri = savedInstanceState.getParcelable("image");
            productIB.setImageURI(imageUri);
        } else
            Glide.with(this).load(product.getImage()).into(productIB);
        

        editBtn.setText(R.string.edit_product);
        id = product.getId();
        titleET.setText(product.getTitle());
        descET.setText(product.getDescription());
        priceET.setText(String.valueOf(product.getPrice()));
        quantityET.setText(String.valueOf(product.getQuantity()));

        switch (product.getCategory()) {
            case "Clothes":
                radioGroup.check(R.id.rb_clothes);
                break;

            case "Electronics":
                radioGroup.check(R.id.rb_electronics);
                break;

            case "Furniture":
                radioGroup.check(R.id.rb_furniture);
                break;
        }

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
            productIB.setImageURI(imageUri);
        }
    }

    public void manageProduct(View view) {
        progressBar.setVisibility(View.VISIBLE);
        editBtn.setVisibility(View.INVISIBLE);
        if (imageUri != null)
            uploadImage();
        else
            uploadProduct(imageUri); //Image Uri in this case is null
    }

    private void uploadImage() {
        //Accessing Cloud Storage bucket by creating an instance of FirebaseStorage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        //Create a reference to upload, download, or delete a file
        StorageReference storageRef = storage.getReference().child(imageUri.getLastPathSegment());
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d("trace", "Image uploaded");
                    getLinkForUploadedImage(storageRef.getDownloadUrl());
                });
    }

    //Getting a download link after uploading a picture
    private void getLinkForUploadedImage(Task<Uri> task) {
        Log.d("trace", "Getting image download link");
        task.addOnSuccessListener(uri -> {
            Log.d("trace", "Image Link: " + uri);
            uploadProduct(uri);
        });
    }

    private void uploadProduct(Uri imageUri) {
        Log.d("trace", "Uploading product...");

        String title = titleET.getText().toString();
        String desc = descET.getText().toString();
        String price = priceET.getText().toString();
        String quantity = quantityET.getText().toString();

        RadioButton radioButton = findViewById(radioGroup.getCheckedRadioButtonId());
        String imageLink;
        if (imageUri != null)
            imageLink = imageUri.toString();
        else
            imageLink = product.getImage();

        product = new ProductModel(title, desc, Double.parseDouble(price)
                , Integer.parseInt(quantity), radioButton.getText().toString(), imageLink);
        product.setId(id);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db
                .collection("products")
                .document(id)
                .set(product)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditProductActivity.this, R.string.product_updated
                            , Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("pbVisibility", progressBar.getVisibility());
        outState.putInt("btnVisibility", editBtn.getVisibility());
        outState.putParcelable("image", imageUri);
    }

}