package com.example.pawel.udemy_instagramclone.baseFunctions.mainGallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pawel.udemy_instagramclone.R;

import java.util.List;

/**
 * Created by Pawel on 25.02.2018.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {

    Context context;
    List<Comment> comments;

    OnCommentAuthorClickedListener mOnCommentAuthorClicked;


    public CommentAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.comment_element, null);

        return new CommentHolder(v);
    }


    @Override
    public void onBindViewHolder(CommentHolder holder, final int position) {

        createComment(holder, position);

        commentAuthorSetOnClickListener(holder, position);


    }

    private void createComment(CommentHolder holder, final int position){

        if (comments.get(position).isComment()) {
            holder.CommentAuthor.setText(comments.get(position).getUsername());
            holder.Comment.setText(comments.get(position).getComment());
        } else {

            holder.CommentAuthor.setText(R.string.addComment);
        }
    }

    private void commentAuthorSetOnClickListener(CommentHolder holder, final int position){

        holder.CommentAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (comments.get(position).isComment()) {

                    mOnCommentAuthorClicked.onCommentAuthorClicked(comments.get(position).getUsername());
                } else {

                    mOnCommentAuthorClicked.onAddCommentClicked();
                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView CommentAuthor;
        TextView Comment;

        CommentHolder(View v) {
            super(v);

            Comment = v.findViewById(R.id.comment);
            CommentAuthor = v.findViewById(R.id.commentAuthor);
        }

        @Override
        public void onClick(View v) {

        }
    }


    public void setOnCommentAuthorClicked(OnCommentAuthorClickedListener mOnCommentAuthorClicked) {
        this.mOnCommentAuthorClicked = mOnCommentAuthorClicked;
    }

    interface OnCommentAuthorClickedListener {

        void onCommentAuthorClicked(String username);
        void onAddCommentClicked();
    }
}
