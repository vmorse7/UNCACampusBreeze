package com.unca.android.uncacampusbreeze;

import java.util.Date;
import java.util.UUID;

public class Post {

    private UUID postID;
    private String postHeading;
    private String postText;
    private Date date;

    public Post(){
        this(UUID.randomUUID());
    }

    public Post(UUID id){
        postID = id;
        date = new Date();
    }

    public void setPostHeading(String postH){
        postH = postHeading;
    }

    public void setPostText(String postT){
        postT = postText;
    }

    public String getPostHeading(){
        return postHeading;
    }

    public String getPostText(){
        return postText;
    }


}
