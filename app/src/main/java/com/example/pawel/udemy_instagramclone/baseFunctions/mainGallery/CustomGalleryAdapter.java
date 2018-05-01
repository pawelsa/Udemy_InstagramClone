package com.example.pawel.udemy_instagramclone.baseFunctions.mainGallery;

import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.pawel.udemy_instagramclone.R;
import com.example.pawel.udemy_instagramclone.myImageGallery.GalleryAdapter;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Pawel on 03.02.2018.
 */

public class CustomGalleryAdapter extends RecyclerView.Adapter<CustomGalleryAdapter.ViewHolder> implements DownloadPhotosAndComments.OnPhotoObjectListener {


    private Context context;
    private ArrayList<PhotoInfoObject> photoInfoObjects;

    int activePosition;

    OnNameClickListener mOnNameClickListener;


    public CustomGalleryAdapter(Context context) {
        this.context = context;
        this.photoInfoObjects = new ArrayList<>();
    }

    public DownloadPhotosAndComments.OnPhotoObjectListener getOnPhotoObjectListener() {
        return this;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_list, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        activePosition = position;
        mainMethod(viewHolder, photoInfoObjects.get(position));
    }


    private void mainMethod(ViewHolder viewHolder, PhotoInfoObject item) {

        initialize(viewHolder, item);

        showCommentViewIfCommentExist(viewHolder, item);

        setCommentsAdapter(viewHolder, item);

        configureCommentView(viewHolder);

        setImages(viewHolder, item);

        setOnClickListeners(viewHolder, item);
    }


    private void initialize(ViewHolder viewHolder, PhotoInfoObject item) {
        viewHolder.UpAuthor.setText(item.getAuthorName());
        viewHolder.DownAuthor.setText(item.getAuthorName());
        viewHolder.Description.setText(item.getDescription());
    }

    private void showCommentViewIfCommentExist(ViewHolder viewHolder, PhotoInfoObject item) {

        if (item.getComments().size() > 0) {

            viewHolder.CommentView.setVisibility(View.VISIBLE);
            Log.i("Quantity of Comments", Integer.toString(item.getComments().size()));
        }
    }


    private void setCommentsAdapter(ViewHolder viewHolder, final PhotoInfoObject item) {

        viewHolder.commentAdapter = new CommentAdapter(context, item.getComments());

        viewHolder.commentAdapter.setOnCommentAuthorClicked(new CommentAdapter.OnCommentAuthorClickedListener() {
            @Override
            public void onCommentAuthorClicked(String username) {

                if (mOnNameClickListener != null)
                    mOnNameClickListener.onNameClicked(item.getAuthorName());
            }

            @Override
            public void onAddCommentClicked() {

                if (mOnNameClickListener != null)
                    mOnNameClickListener.onAddCommentClicked(item.getID());
            }
        });
    }


    private void configureCommentView(ViewHolder viewHolder) {

        viewHolder.CommentView.setHasFixedSize(true);
        viewHolder.CommentView.setLayoutManager(new LinearLayoutManager(context));
        viewHolder.CommentView.setItemAnimator(new DefaultItemAnimator());

        viewHolder.CommentView.setAdapter(viewHolder.commentAdapter);
    }


    private void setImages(ViewHolder viewHolder, PhotoInfoObject item) {

        Glide.with(context)
                .load(item.getImage())
                .into(viewHolder.Image);

        if (item.getAvatar() != null) {

            Glide.with(context)
                    .load(item.getAvatar())
                    .apply(new RequestOptions().centerInside())
                    .into(viewHolder.Avatar);
        }
    }


    private void setOnClickListeners(ViewHolder viewHolder, PhotoInfoObject item) {

        upAuthorSetOnClickListener(viewHolder, item);
        downAuthorSetOnClickListener(viewHolder, item);
        optionsSetOnClickListener(viewHolder, item);
        yesSetOnClickListener(viewHolder, item);
        noSetOnClickListener(viewHolder, item);
    }

    private void upAuthorSetOnClickListener(ViewHolder viewHolder, final PhotoInfoObject item) {

        viewHolder.UpAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mOnNameClickListener != null)
                    mOnNameClickListener.onNameClicked(item.getAuthorName());
            }
        });
    }

    private void downAuthorSetOnClickListener(ViewHolder viewHolder, final PhotoInfoObject item) {

        viewHolder.DownAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mOnNameClickListener != null)
                    mOnNameClickListener.onNameClicked(item.getAuthorName());
            }
        });
    }

    private void optionsSetOnClickListener(final ViewHolder viewHolder, final PhotoInfoObject item) {

        viewHolder.Options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Objects.equals(ParseUser.getCurrentUser().getUsername(), item.getAuthorName())) {
                    viewHolder.viewSwitcher.setInAnimation(context, R.anim.in_from_left);
                    viewHolder.viewSwitcher.setOutAnimation(context, R.anim.out_to_right);
                    viewHolder.viewSwitcher.showNext();
                }
            }
        });
    }

    private void yesSetOnClickListener(final ViewHolder viewHolder, final PhotoInfoObject item) {

        viewHolder.Yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                viewHolder.viewSwitcher.setInAnimation(context, R.anim.in_from_right);
                viewHolder.viewSwitcher.setOutAnimation(context, R.anim.out_to_left);

                final ParseQuery<ParseObject> object = ParseQuery.getQuery("Image");

                object.whereEqualTo("objectId", item.getID());

                object.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {

                        if (e == null) {
                            for (ParseObject obj : objects) {
                                obj.deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            photoInfoObjects.remove(activePosition);
                                            notifyDataSetChanged();
                                            notifyItemRemoved(activePosition);
                                            notifyItemRangeChanged(activePosition, photoInfoObjects.size());
                                            Toast.makeText(context, "Deleted", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }
                    }
                });

                viewHolder.viewSwitcher.showPrevious();
            }
        });
    }

    private void noSetOnClickListener(final ViewHolder viewHolder, PhotoInfoObject item) {

        viewHolder.No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                viewHolder.viewSwitcher.setInAnimation(context, R.anim.in_from_right);
                viewHolder.viewSwitcher.setOutAnimation(context, R.anim.out_to_left);
                Toast.makeText(context, "Wasn't deleted", Toast.LENGTH_LONG).show();
                viewHolder.viewSwitcher.showPrevious();
            }
        });
    }


    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return photoInfoObjects.size();
    }

    public void clearAdapter(){
        photoInfoObjects.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnFocusChangeListener {

        TextView UpAuthor;
        TextView Description;
        ImageView Image;
        TextView DownAuthor;
        ImageView Options;
        TextView Yes;
        TextView No;
        ViewSwitcher viewSwitcher;
        ImageView Avatar;
        RecyclerView CommentView;
        CommentAdapter commentAdapter;

        public ViewHolder(View view) {
            super(view);

            UpAuthor = view.findViewById(R.id.authorName);
            DownAuthor = view.findViewById(R.id.authorC);
            Description = view.findViewById(R.id.Comment);
            Image = view.findViewById(R.id.image);
            Options = view.findViewById(R.id.openOptions);
            Yes = view.findViewById(R.id.textYes);
            No = view.findViewById(R.id.textNo);
            viewSwitcher = view.findViewById(R.id.viewSwitcher);
            Avatar = view.findViewById(R.id.avatar);
            CommentView = view.findViewById(R.id.commentsList);
        }

        @Override
        public void onClick(View v) {

        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {

        }
    }

    @Override
    public void onCreated(PhotoInfoObject photoInfoObject) {
        photoInfoObjects.add(photoInfoObject);
        notifyDataSetChanged();
    }


    public void setOnNameClickListener(OnNameClickListener onNameClickListener) {
        mOnNameClickListener = onNameClickListener;
    }

    interface OnNameClickListener {
        void onNameClicked(String username);
        void onAddCommentClicked(String ImageID);
    }
}
