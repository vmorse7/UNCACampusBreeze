package com.unca.android.uncacampusbreeze;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class PostListActivity extends SingleFragmentActivity{

    @Override
    protected Fragment createFragment(){
        return new PostListFragment();
    }
}
