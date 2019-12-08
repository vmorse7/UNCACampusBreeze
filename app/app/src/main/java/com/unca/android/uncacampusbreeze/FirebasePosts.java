package com.unca.android.uncacampusbreeze;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FirebasePosts extends Activity {

    Button newPostButton;

    private ArrayList<String> mHeading = new ArrayList<>();
    private ArrayList<String> mText = new ArrayList<>();
    private ArrayList<String> mDate = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.list_view);
        setContentView(R.layout.post_list_view);

        newPostButton = (Button) findViewById(R.id.new_post);

       // displayFirestoreData();

        mHeading.add("Heading 1");
        mHeading.add("Heading 2");
        mHeading.add("Heading 3");
        mHeading.add("Heading 4");

        mText.add("Text 1");
        mText.add("Text 2");
        mText.add("Text 3");
        mText.add("Text 4");

        mDate.add("Date 1");
        mDate.add("Date 2");
        mDate.add("Date 3");
        mDate.add("Date 4");

        createRecyclerView();
    }

    private void createRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.post_recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(
                mHeading, mText, mDate, this
        );
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
