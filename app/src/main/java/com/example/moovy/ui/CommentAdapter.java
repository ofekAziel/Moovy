package com.example.moovy.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moovy.R;
import com.example.moovy.models.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{
    private static final String TAG = "CommentAdapter";

    private ArrayList<Comment> comments;
    private Context context;

    public CommentAdapter(Context context, ArrayList<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        //Glide.with(context) 14:35
        holder.userName.setText(comments.get(position).getUser().getFullName());
        holder.content.setText(comments.get(position).getContent());
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/YY HH:mm");
        holder.date.setText(format.format(comments.get(position).getDate()));

        // TODO: swipe to delete
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView content;
        TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            content = itemView.findViewById(R.id.content);
            date = itemView.findViewById(R.id.date);

        }
    }
}
