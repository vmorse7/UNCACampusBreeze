package com.unca.android.uncacampusbreeze;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostLab {
    private static PostLab postLab;

    private List<Post> posts;

    public static PostLab get(Context context){
        if(postLab == null){
            postLab = new PostLab(context);
        }
        return postLab;
    }

    public void addPost(Post p){
        posts.add(p);
    }

    private PostLab(Context context){
        posts = new ArrayList<>();

    }

    public List<Post> getPosts(){
        return posts;
    }

    public Post getPost(UUID id){
        for(Post post : posts){
            if(post.getId().equals(id)){
                return post;
            }
        }
        return null;
    }
}
