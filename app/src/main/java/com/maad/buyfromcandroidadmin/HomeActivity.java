package com.maad.buyfromcandroidadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private HomeActivityViewModel viewModel;
    //private ArrayList<ProductModel> productModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        viewModel = new ViewModelProvider(this).get(HomeActivityViewModel.class);
        viewModel.getProductsLiveData().observe(this, new Observer<List<DocumentChange>>() {
            @Override
            public void onChanged(List<DocumentChange> documentChanges) {
                Log.d("trace", "Observing");
                //viewModel.getProductModels().clear();

                if (viewModel.isNewDataArrived()){
                    for (int i = 0; i < documentChanges.size(); ++i) {
                        //Log.d("trace", "Products: " + documentChanges.get(i).getDocument());
                        QueryDocumentSnapshot snapshot = documentChanges.get(i).getDocument();
                        ProductModel productModel = snapshot.toObject(ProductModel.class);
                        //productModels.add(productModel);
                        viewModel.addProduct(productModel);
                    }
                    viewModel.setNewDataArrived(false);
                }
                ProductAdapter adapter = new ProductAdapter(HomeActivity.this, viewModel.getProductModels());
                RecyclerView recyclerView = findViewById(R.id.rv);
                recyclerView.setAdapter(adapter);


            }
        });

    }

    public void openAddProductActivity(View view) {
        Intent i = new Intent(this, AddProductActivity.class);
        startActivity(i);
    }

}