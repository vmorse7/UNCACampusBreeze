package com.unca.android.uncacampusbreeze;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.UUID;

public class PostActivity extends SingleFragmentActivity {

    public static final String EXTRA_POST_ID =
            "com.unca.android.uncacampusbreeze.crime_id";

    public static Intent newIntent(Context packageContext, UUID postID){
        Intent intent = new Intent(packageContext, PostActivity.class);
        intent.putExtra(EXTRA_POST_ID, postID);
        return intent;
    }

    @Override
    protected Fragment createFragment(){
        UUID postId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_POST_ID);
        return PostFragment.newInstance(postId);
    }
}
