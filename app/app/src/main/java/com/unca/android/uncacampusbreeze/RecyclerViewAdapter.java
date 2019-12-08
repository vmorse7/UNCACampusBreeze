package com.unca.android.uncacampusbreeze;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private ArrayList<String> heading = new ArrayList<>();
    private ArrayList<String> text = new ArrayList<>();
    private ArrayList<String> date = new ArrayList<>();
    private Context context;

    RecyclerViewAdapter(ArrayList<String> h, ArrayList<String> t, ArrayList<String> d, Context c){
        heading = h;
        text = t;
        date = d;
        context = c;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.postHeading.setText(heading.get(position));
        holder.postText.setText(text.get(position));
        holder.postDate.setText(date.get(position));



    }

    @Override
    public int getItemCount() {
        return heading.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
    TextView postHeading;
    TextView postText;
    TextView postDate;
    RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postHeading = itemView.findViewById(R.id.postHeading);
            postText = itemView.findViewById(R.id.postText);
            postDate = itemView.findViewById(R.id.postDate);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }


}
