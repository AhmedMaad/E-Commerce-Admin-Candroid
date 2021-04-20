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

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Calendar;


public class AddProductActivity extends AppCompatActivity {

    private Uri imageUri;
    private ProgressBar progressBar;
    private Button addBtn;
    private ImageButton productIB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        progressBar = findViewById(R.id.progress_bar);
        addBtn = findViewById(R.id.btn_upload);
        productIB = findViewById(R.id.ib_product);
        addBtn.setText(R.string.add_product);

        if (savedInstanceState != null) {
            progressBar.setVisibility(savedInstanceState.getInt("pbVisibility"));
            addBtn.setVisibility(savedInstanceState.getInt("btnVisibility"));
            imageUri = savedInstanceState.getParcelable("image");
            productIB.setImageURI(imageUri);
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
        addBtn.setVisibility(View.INVISIBLE);
        uploadImage();
    }

    private void uploadImage() {
        //Accessing Cloud Storage bucket by creating an instance of FirebaseStorage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        //Create a reference to upload, download, or delete a file

        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);
        int millis = now.get(Calendar.MILLISECOND);
        String imageName = "image: " + day + '-' + month + '-' + year + ' ' + hour + ':' + minute
                + ':' + second + '.' + millis;

        StorageReference storageRef = storage.getReference().child(imageName);
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
        TextInputEditText titleET = findViewById(R.id.et_title);
        TextInputEditText descET = findViewById(R.id.et_description);
        TextInputEditText priceET = findViewById(R.id.et_price);
        TextInputEditText quantityET = findViewById(R.id.et_quantity);

        String title = titleET.getText().toString();
        String desc = descET.getText().toString();
        String price = priceET.getText().toString();
        String quantity = quantityET.getText().toString();

        RadioGroup radioGroup = findViewById(R.id.rg);
        RadioButton radioButton = findViewById(radioGroup.getCheckedRadioButtonId());

        ProductModel productModel
                = new ProductModel(title, desc, Double.parseDouble(price)
                , Integer.parseInt(quantity), radioButton.getText().toString(), imageUri.toString());

        //https://firebase.google.com/docs/firestore/manage-data/add-data?authuser=0#custom_objects
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db
                .collection("products")
                .add(productModel)
                .addOnSuccessListener(documentReference -> {
                    //Updating id to make delete and update functionality later
                    documentReference.update("id", documentReference.getId());
                    Toast.makeText(AddProductActivity.this, R.string.product_added, Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("pbVisibility", progressBar.getVisibility());
        outState.putInt("btnVisibility", addBtn.getVisibility());
        outState.putParcelable("image", imageUri);
    }

}