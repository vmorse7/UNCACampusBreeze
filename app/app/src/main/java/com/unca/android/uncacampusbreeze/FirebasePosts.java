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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FirebasePosts extends Activity {

    Button newPostButton;

    private static ArrayList<String> mHeading = new ArrayList<>();
    private static  ArrayList<String> mText = new ArrayList<>();
    private static ArrayList<String> mDate = new ArrayList<>();

    RecyclerView recyclerView;
    static RecyclerViewAdapter adapter;
    View parentLayout;


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

    public Context getActivity() {
        return FirebasePosts.this;
    }

    public static void displayFirestoreData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("userMessages");
//        Query postQuery = collectionReference.
//                whereEqualTo("User ID", FirebaseAuth.getInstance().getCurrentUser().getUid());
//        postQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()){
//                    for(QueryDocumentSnapshot document: task.getResult()){
//                        Post post = document.toObject(Post.class);
//                        mHeading.add(document.getData().toString());
//                        mText.add(document.getData().toString());
//                        mDate.add(document.getData().toString());
//                        Toast first = Toast.makeText(getActivity(), "Query returned: " + document.toString(), Toast.LENGTH_SHORT);
//                        first.show();
//                    }
//                    adapter.notifyDataSetChanged();
//                    Toast first = Toast.makeText(getActivity(), "Query Successful", Toast.LENGTH_SHORT);
//                    first.show();
//                }else{
//                    Toast first = Toast.makeText(getActivity(), "Query Failed", Toast.LENGTH_SHORT);
//                    first.show();
//                }
//            }
//        });


//        Toast first = Toast.makeText(getActivity(), "Query returned: ", Toast.LENGTH_SHORT);
//        first.show();


        DocumentReference docRef = db.collection("userMessages").document(PostActivity.getID());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        //Log.i("LOGGER","First "+document.getString("first"));
//                        Toast first = Toast.makeText(getActivity(), "Heading: " + document.getString("Heading"), Toast.LENGTH_SHORT);
//                        first.show();
                        Log.d("Toast", "Heading: " + document.getString("Heading"));
                        Log.d("Toast", "Text: " + document.getString("Text"));

                        mHeading.add(document.getString("Heading"));
                        //mHeading.notify();
                        mText.add(document.getString("Text"));
                       // mText.notify();

                        //mDate.add(document.getString("Date"));


                    }
                   // adapter.notifyDataSetChanged();

                }

            }
        });



        }


    }

