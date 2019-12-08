package com.unca.android.uncacampusbreeze;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;
import java.util.UUID;

public class Post {

    String postHeading;
    String postText;
    String date;



    public Post(String heading, String text) {
        postHeading = heading;
        postText = text;
        date = new Date().toString();
    }

    public void setPostHeading(String postH){
        postHeading = postH;
    }

    public void setPostText(String postT){
        postText = postT;
    }

    public String getPostHeading(){
        return postHeading;
    }

    public String getPostText(){
        return postText;
    }

}
