package com.unca.android.uncacampusbreeze;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirebasePosts extends Activity {

    Button newPostButton;

    private static ArrayList<String> mHeading = new ArrayList<>();
    private static  ArrayList<String> mText = new ArrayList<>();
    private static ArrayList<String> mDate = new ArrayList<>();
    private static Context c;

    RecyclerView recyclerView;
    static RecyclerViewAdapter adapter = new RecyclerViewAdapter(mHeading,mText,mDate, c);
    View parentLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.list_view);
        setContentView(R.layout.post_list_view);
        refreshList();
        newPostButton = (Button) findViewById(R.id.new_post);
        adapter.notifyDataSetChanged();
        createRecyclerView();
        displayFirestoreData();






        //adapter.notifyDataSetChanged();
    }


    private void createRecyclerView(){
        recyclerView = findViewById(R.id.post_recycler_view);
        adapter = new RecyclerViewAdapter(
                mHeading, mText, mDate, this
        );
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void sendToPostActivity(View view){
        Intent sendToPost = new Intent(FirebasePosts.this, PostActivity.class);
        startActivity(sendToPost);
    }

    public void refreshList(){
        Button b = (Button) findViewById(R.id.refresh);
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d("post", "Refresh button is clicked");
                displayFirestoreData();
            }
        });
    }

    public Context getActivity() {
        return FirebasePosts.this;
    }




    public static void displayFirestoreData(){


        FirebaseFirestore.getInstance()
                .collection("userMessages")
                .orderBy("Date", Query.Direction.DESCENDING )
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("post", "ON COMPLETE CALLED");
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> myListOfDocuments = task.getResult().getDocuments();
                            mHeading.clear();
                            mText.clear();
                            mDate.clear();



                            for(int i = 0; i < myListOfDocuments.size();i++){

                                Log.d("post", "" +myListOfDocuments.get(i).get("Heading"));
                                mHeading.add(myListOfDocuments.get(i).get("Heading").toString());
                                mText.add(myListOfDocuments.get(i).get("Text").toString());
                                mDate.add(myListOfDocuments.get(i).get("Date").toString());
                                adapter.notifyDataSetChanged();

                            }

                        }
                    }
                });



        }


    }

