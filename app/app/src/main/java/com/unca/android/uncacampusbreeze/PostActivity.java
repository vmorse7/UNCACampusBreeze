package com.unca.android.uncacampusbreeze;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.content.ContentValues.TAG;
import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class PostActivity extends Activity {

    EditText heading;
    EditText text;
    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_post);

        heading = findViewById(R.id.insertHeading);
        text = (EditText) findViewById(R.id.insertText);

    }

    public Context getActivity() {
        return PostActivity.this;
    }

    public void getUserTextInput(){
        UUID id = UUID.randomUUID();
        String h = heading.getEditableText().toString();
        String t = text.getEditableText().toString();
        post = new Post(h, t, id);
    }

    public void sendToFirebasePost(View view){

        getUserTextInput();

        createMessageData(post);

        Intent sendToPost = new Intent(this, FirebasePosts.class);
        startActivity(sendToPost);
    }

    public void createMessageData(Post post){
        Map<String, Object> messages = new HashMap<>();

        String heading = post.getPostHeading();
        String text = post.getPostText();
        Date date = new Date();

//        Toast first = Toast.makeText(getActivity(), "Heading: " + heading, Toast.LENGTH_SHORT);
//        first.show();
//        Toast second = Toast.makeText(getActivity(), "Text: " + text, Toast.LENGTH_SHORT);
//        second.show();


        messages.put("Heading", heading);
        messages.put("Text", text);
        messages.put("Date", date);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference documentReference = db.collection("userMessages").document();
                documentReference
                .set(messages)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }
}
