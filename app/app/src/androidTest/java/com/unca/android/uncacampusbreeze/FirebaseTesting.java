package com.unca.android.uncacampusbreeze;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.BeforeClass;
import org.junit.Test;


public class FirebaseTesting {

    static final private String DEBUG_TAG = "FirebaseTesting";

    static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @BeforeClass
    static public void initFirebaseTesting() {
        Log.d(DEBUG_TAG, "started initFirebaseTesting()");

        Log.d(DEBUG_TAG, "existing initFirebaseTesting()");
    }

    @Test
    public void retrieveSingleUserDoc() {
        Log.d(DEBUG_TAG, "started retrieveSingleUserDoc()");
        DocumentReference docRef = db.collection("users").document("Bulldog000000");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.d(DEBUG_TAG, "inside onComplete()");
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(DEBUG_TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(DEBUG_TAG, "No such document.");
                    }
                } else {
                    Log.d(DEBUG_TAG, "get failed with ", task.getException());
                }
            }
        });
        Log.d(DEBUG_TAG, "exiting retrieveSingleUserDoc()");
    }
}
