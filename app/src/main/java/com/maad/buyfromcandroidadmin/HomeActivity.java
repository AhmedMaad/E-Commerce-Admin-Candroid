package com.maad.buyfromcandroidadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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
    private ArrayList<ProductModel> productModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        viewModel = new ViewModelProvider(this).get(HomeActivityViewModel.class);
        viewModel.getProducts().observe(this, new Observer<List<DocumentChange>>() {
            @Override
            public void onChanged(List<DocumentChange> documentChanges) {
                Log.d("trace", "Observing");

                for (int i = 0;i<documentChanges.size();++i)
                    Log.d("trace", "Products: " + documentChanges.get(i).getDocument());

                /*for (QueryDocumentSnapshot document : querySnapshotTask.getResult()) {
                    Log.d("trace", "Products: " + document.getData());
                    Map<String, Object> data = document.getData();
                    productModels.add(
                            new ProductModel(
                                    data.get("title").toString()
                                    , data.get("description").toString()
                                    , Double.valueOf(data.get("price").toString())
                                    , Integer.valueOf(data.get("quantity").toString())
                                    , data.get("category").toString()
                                    , data.get("image").toString()
                            )
                    );
                }*/
            }
        });

        for (int i = 0;i<productModels.size();++i)
            Log.d("trace", productModels.get(i).getTitle());

    }

    public void openAddProductActivity(View view) {
        Intent i = new Intent(this, AddProductActivity.class);
        startActivity(i);
    }

}