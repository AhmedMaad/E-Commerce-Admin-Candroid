package com.maad.buyfromcandroidadmin;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<List<DocumentSnapshot>> productsLiveData;
    private final ArrayList<ProductModel> productModels;
    private final FirebaseFirestore db;

    public HomeViewModel() {
        db = FirebaseFirestore.getInstance();
        productModels = new ArrayList<>();
    }

    public ArrayList<ProductModel> getProductModels() {
        return productModels;
    }

    public void addProduct(ProductModel product) {
        productModels.add(product);
    }

    public LiveData<List<DocumentSnapshot>> getProductsLiveData() {
        if (productsLiveData == null) {
            Log.d("trace", "Making instance from mutable live data");
            productsLiveData = new MutableLiveData<>();
            loadProducts();
        }
        return productsLiveData;
    }

    private void loadProducts() {
        CollectionReference collection = db.collection("products");
        collection.addSnapshotListener((value, error) -> {
            if (value != null) {
                Log.d("trace", "Data arrived from firebase");
                List<DocumentSnapshot> documentSnapshots = value.getDocuments();
                productsLiveData.setValue(documentSnapshots);
            }
        });
    }

    public void deleteProduct(String id) {
        db
                .collection("products")
                .document(id)
                .delete();
    }

}
