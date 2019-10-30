package com.unca.android.uncacampusbreeze;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class PostFragment extends Fragment {

    private static final String ARG_POST_ID = "post_id";

    private TextView date;
    private Post post;
    private TextView heading;
    private TextView text;
    private String currentDate = DateFormat.getDateTimeInstance().format(new Date());

    public static PostFragment newInstance(UUID postId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_POST_ID, postId);

        PostFragment fragment = new PostFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        UUID postId = (UUID) getArguments().getSerializable(ARG_POST_ID);
        post = PostLab.get(getActivity()).getPost(postId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.new_post, container, false);

        heading = (EditText) v.findViewById(R.id.insertHeading);
        heading.setText(post.getPostHeading());
        heading.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                post.setPostHeading(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        text = (EditText) v.findViewById(R.id.insertText);
        text.setText(post.getPostText());
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                post.setPostText(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return v;
    }

}