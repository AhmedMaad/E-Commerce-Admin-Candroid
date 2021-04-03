package com.maad.buyfromcandroidadmin;


import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class HomeActivityViewModel extends ViewModel {


    private MutableLiveData<List<DocumentChange>> products;

    public LiveData<List<DocumentChange>> getProducts() {
        if (products == null) {
            products = new MutableLiveData<>();
            loadProducts();
        }
        return products;
    }


    private void loadProducts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                            products.setValue(task.getResult().getDocumentChanges());

                    }
                });
    }

}
