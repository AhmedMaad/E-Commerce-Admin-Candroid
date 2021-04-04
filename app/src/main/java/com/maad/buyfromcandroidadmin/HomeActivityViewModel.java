package com.maad.buyfromcandroidadmin;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivityViewModel extends ViewModel {

    private MutableLiveData<List<DocumentChange>> productsLiveData;
    private ArrayList<ProductModel> productModels;
    private boolean isNewDataArrived;

    public HomeActivityViewModel() {
        if (productModels == null)
            productModels = new ArrayList<>();
    }

    public ArrayList<ProductModel> getProductModels() {
        return productModels;
    }


    public void addProduct(ProductModel product) {
        productModels.add(product);
    }

    public LiveData<List<DocumentChange>> getProductsLiveData() {
        if (productsLiveData == null) {
            Log.d("trace", "Making instance from mutable live data");
            productsLiveData = new MutableLiveData<>();
            loadProducts();
        }
        return productsLiveData;
    }


    private void loadProducts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collection = db.collection("products");
        collection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value
                    , @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    Log.d("trace", "Data arrived from firebase");
                    List<DocumentChange> documentChanges = value.getDocumentChanges();
                    isNewDataArrived = true;
                    productsLiveData.setValue(documentChanges);
                }
            }
        });
    }

    public boolean isNewDataArrived() {
        return isNewDataArrived;
    }

    public void setNewDataArrived(boolean newDataArrived) {
        isNewDataArrived = newDataArrived;
    }


}
