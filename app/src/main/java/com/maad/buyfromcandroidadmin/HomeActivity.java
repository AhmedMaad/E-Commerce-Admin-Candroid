package com.maad.buyfromcandroidadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.TargetOrBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private HomeActivityViewModel viewModel;
    private ProductAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        recyclerView = findViewById(R.id.rv);

        viewModel = new ViewModelProvider(this).get(HomeActivityViewModel.class);
        /*viewModel.getProductsLiveData().observe(this, new Observer<List<DocumentChange>>() {
            @Override
            public void onChanged(List<DocumentChange> documentChanges) {
                //Log.d("trace", "Observing document changes...");
                retrieveProducts(documentChanges);
            }
        });*/

        viewModel.getProductsLiveData().observe(this, new Observer<List<DocumentSnapshot>>() {
            @Override
            public void onChanged(List<DocumentSnapshot> documentSnapshots) {
                if (viewModel.isNewDataArrived()) {
                    viewModel.getProductModels().clear();
                    for (int i = 0; i < documentSnapshots.size(); ++i) {
                        ProductModel productModel = documentSnapshots.get(i).toObject(ProductModel.class);
                        viewModel.addProduct(productModel);
                    }
                    viewModel.setNewDataArrived(false);
                }
                showProducts();
            }
        });

    }


//    private void retrieveProducts(List<DocumentChange> documentChanges) {
//        if (viewModel.isNewDataArrived()) {
//            for (int i = 0; i < documentChanges.size(); ++i) {
//                if (!documentChanges.get(i).getType().equals(DocumentChange.Type.REMOVED)) {
//                    if (!documentChanges.get(i).getType().equals(DocumentChange.Type.MODIFIED)) {
//                        Log.d("trace", "Observing retrieving new/existed document");
//                        QueryDocumentSnapshot snapshot = documentChanges.get(i).getDocument();
//                        ProductModel productModel = snapshot.toObject(ProductModel.class);
//                        viewModel.addProduct(productModel);
//                    }
//                }
//                if (documentChanges.get(i).getType().equals(DocumentChange.Type.MODIFIED)){
//                    Log.d("trace", "Observing a modified document");
//                }
//                if (documentChanges.get(i).getType().equals(DocumentChange.Type.REMOVED)){
//                    Log.d("trace", "Observing removing a document");
//                    viewModel.getProductModels().remove(i);
//                    adapter.notifyItemRemoved(i);
//                }
//            }
//            viewModel.setNewDataArrived(false);
//        }
//        showProducts();
//    }

    private void showProducts() {
        adapter = new ProductAdapter(HomeActivity.this, viewModel.getProductModels());
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //Navigate to edit activity
            }
        });

        adapter.setOnDeleteItemClickListener(new ProductAdapter.OnDeleteItemClickListener() {
            @Override
            public void onDeleteItemClick(int position) {
                String id = viewModel.getProductModels().get(position).getId();
                Log.d("trace", "Document ID to delete: " + id);
                viewModel.deleteProduct(id);
                Toast.makeText(HomeActivity.this, "Product Deleted", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void openAddProductActivity(View view) {
        Intent i = new Intent(this, AddProductActivity.class);
        startActivity(i);
    }
}