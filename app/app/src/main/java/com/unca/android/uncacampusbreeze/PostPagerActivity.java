package com.unca.android.uncacampusbreeze;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;
import java.util.UUID;

public class PostPagerActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private List<Post> posts;
    private static final String EXTRA_POST_ID =
            "com.unca.android.uncacampusbreeze.post_id";

    public static Intent newIntent(Context packageContext, UUID postId){
        Intent intent = new Intent(packageContext, PostPagerActivity.class);
        intent.putExtra(EXTRA_POST_ID, postId);
        return intent;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_pager);

        UUID postId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_POST_ID);

        viewPager = (ViewPager) findViewById(R.id.post_view_pager);

        posts = PostLab.get(this).getPosts();
        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentPagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Post post = posts.get(position);
                return PostFragment.newInstance(post.getId());
            }

            @Override
            public int getCount() {
                return posts.size();
            }
        });

        for(int i = 0; i < posts.size(); i++){
            if(posts.get(i).getId().equals(postId)){
                viewPager.setCurrentItem(i);
                break;
            }
        }

    }
}
