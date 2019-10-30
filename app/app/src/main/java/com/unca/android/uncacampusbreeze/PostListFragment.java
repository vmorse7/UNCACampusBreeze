package com.unca.android.uncacampusbreeze;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;



public class PostListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

//        Toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.new_post:
                Post post = new Post();
                PostLab.get(getActivity()).addPost(post);
                Intent intent = PostPagerActivity
                        .newIntent(getActivity(), post.getId());
                startActivity(intent);
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);

        recyclerView = (RecyclerView) view
                .findViewById(R.id.post_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }

    private void updateUI(){
        PostLab postLab = PostLab.get(getActivity());
        List<Post> posts = postLab.getPosts();

        if(adapter == null){
            adapter = new ViewAdapter(posts);
            recyclerView.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
        }
    }

    private class ViewAdapter extends RecyclerView.Adapter<ViewHolder>{

        private List<Post> posts;

        public ViewAdapter(List<Post> Posts){
            posts = Posts;
        }


        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ViewHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Post post = posts.get(position);
            holder.bind(post);
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView heading;
        private TextView text;
        private TextView date;

        private Post Post;

        @Override
        public void onClick(View view){
            Intent intent = PostPagerActivity.newIntent(getActivity(), Post.getId());
            startActivity(intent);
        }

        public void bind(Post post){
            Post = post;
            heading.setText(post.getPostHeading());
            text.setText(post.getPostText());
            date.setText(post.getDate().toString());
        }

        public ViewHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.post, parent, false));
            itemView.setOnClickListener(this);
            heading = (TextView) itemView.findViewById(R.id.postHeading);
            text = (TextView) itemView.findViewById(R.id.postText);
            date = (TextView) itemView.findViewById(R.id.postDate);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }


}
