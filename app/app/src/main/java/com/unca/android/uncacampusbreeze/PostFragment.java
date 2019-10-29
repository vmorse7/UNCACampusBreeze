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
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.Date;

public class PostFragment extends Fragment {

    private RecyclerView postRecyclerView;
    private TextView date;
    private Post post;
    private EditText heading;
    private EditText text;
    private String currentDate = DateFormat.getDateTimeInstance().format(new Date());

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        post = new Post();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.post_view, container, false);

        date = view.findViewById(R.id.postDate);
        date.setText(currentDate);

        heading = (EditText) view.findViewById(R.id.postHeading);
        heading.setText(post.getPostText());
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

        text = (EditText) view.findViewById(R.id.postText);
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



        return view;
    }

}
