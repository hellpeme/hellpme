package com.example.vincius.myapplication.Notifications;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseIdService extends FirebaseInstanceIdService {


    private final String TAG = "MyFirebaseIDService";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        if(firebaseUser != null){
            updateToken(refreshToken);
        }

    }

    private void updateToken(final String refreshToken) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        CollectionReference reference = FirebaseFirestore.getInstance().collection("Tokens");
        Token token = new Token(refreshToken);
        reference.document(firebaseUser.getUid())
                .set(token).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, refreshToken);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("tokenwhy", "tokenNotUpdated");
            }
        });

    }
}
