package com.unca.android.uncacampusbreeze;

import java.util.Date;
import java.util.UUID;

public class Post {

    private UUID postID;
    private String postHeading;
    private String postText;
    private Date mDate;

    public Post() {
        this(UUID.randomUUID());
    }

    public Post(UUID id) {
        postID = id;
        mDate = new Date();
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

    public UUID getId() {
        return postID;
    }

    public Date getDate() {
        return mDate;
    }
}
