package com.unca.android.uncacampusbreeze;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;
import java.util.UUID;

public class Post {

    private UUID postID;
    private String postHeading;
    private String postText;
    private Date date;
    private String user;


    public Post(String heading, String text, UUID id) {
        postHeading = heading;
        postText = text;
        postID = id;
        date = new Date();
    }

    public UUID getId() {
        return postID;
    }

    public void setUser(String user){
        this.user = user;
    }

    public String getUser(){
        return user;
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

    public Date getDate() {
        return date;
    }


}
