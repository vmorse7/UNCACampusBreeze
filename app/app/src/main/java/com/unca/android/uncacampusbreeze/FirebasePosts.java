package com.unca.android.uncacampusbreeze;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebasePosts extends Activity {

    Button newPostButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        newPostButton = (Button) findViewById(R.id.new_post);

        displayFirestoreData();

    }

    public void sendToPostActivity(View view){
        Intent sendToPost = new Intent(FirebasePosts.this, PostActivity.class);
        startActivity(sendToPost);
    }

    public void displayFirestoreData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference collectionReference = db.collection("userMessages");


    }
}
