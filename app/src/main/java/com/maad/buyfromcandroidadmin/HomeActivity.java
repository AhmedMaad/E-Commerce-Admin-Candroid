package com.maad.buyfromcandroidadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    private HomeViewModel viewModel;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        recyclerView = findViewById(R.id.rv);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        viewModel.getProductsLiveData().observe(this, documentSnapshots -> {
                viewModel.getProductModels().clear();
                for (int i = 0; i < documentSnapshots.size(); ++i) {
                    ProductModel productModel = documentSnapshots.get(i).toObject(ProductModel.class);
                    viewModel.addProduct(productModel);
                }
            showProducts();
        });

    }

    private void showProducts() {
        ProductAdapter adapter = new ProductAdapter(HomeActivity.this, viewModel.getProductModels());
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(position -> {
            //Navigate to edit activity
        });

        adapter.setOnDeleteItemClickListener(position -> {
            String id = viewModel.getProductModels().get(position).getId();
            Log.d("trace", "Document ID to delete: " + id);
            viewModel.deleteProduct(id);
            Toast.makeText(HomeActivity.this, "Product Deleted", Toast.LENGTH_SHORT).show();
        });

    }

    public void openAddProductActivity(View view) {
        Intent i = new Intent(this, AddProductActivity.class);
        startActivity(i);
    }
}