package com.example.pawel.udemy_instagramclone.baseFunctions.mainGallery;


import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.example.pawel.udemy_instagramclone.MyFragmentManager;
import com.example.pawel.udemy_instagramclone.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.example.pawel.udemy_instagramclone.MyFragmentManager.USER_GALLERY_TAG;


public class Gallery extends android.support.v4.app.Fragment implements DownloadPhotosAndComments.OnPhotoDownloadStatusListener {

    private SwipeRefreshLayout refreshLayout;
    private CustomGalleryAdapter adapter;
    private RecyclerView layout;

    private RelativeLayout addCommentLayout;
    private EditText addCommentEditText;
    private ImageButton sendButton;

    private String username = null;

    private String ImageID;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        username = args.getString("username");
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layout = view.findViewById(R.id.contentListView);
        refreshLayout = view.findViewById(R.id.refreshLayout);

        addCommentLayout = view.findViewById(R.id.addCommentLayout);
        addCommentEditText = view.findViewById(R.id.enterCommentEditText);
        sendButton = view.findViewById(R.id.sendButton);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mainMethod();
    }


    private void mainMethod() {

        initialize();

        setAdapter();

        transferQuery();

        setOnRefreshListener();

        sendButtonOnClickListener();

        keyBoardEventListener();
    }

    private void initialize() {

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
            actionBar.setDisplayShowHomeEnabled(false);
        }
        layout.setHasFixedSize(true);
        layout.setLayoutManager(new LinearLayoutManager(getActivity()));
        layout.setItemAnimator(new DefaultItemAnimator());
    }

    private void setAdapter() {

        adapter = new CustomGalleryAdapter(getActivity().getApplicationContext());

        adapter.setOnNameClickListener(new CustomGalleryAdapter.OnNameClickListener() {
            @Override
            public void onNameClicked(String username) {

                MyFragmentManager myFragmentManager=MyFragmentManager.getInstance();
                myFragmentManager.startFragment(USER_GALLERY_TAG, username);
            }

            @Override
            public void onAddCommentClicked(String mImageID) {
                activateAddCommentEditText(mImageID);
            }
        });
        layout.setAdapter(adapter);
    }

    private void activateAddCommentEditText(String mImageID) {

        ImageID = mImageID;
        addCommentLayout.setVisibility(View.VISIBLE);
        addCommentEditText.requestFocus();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        addCommentEditText.requestFocus();
    }


    private void transferQuery() {

        final DownloadPhotosAndComments.OnPhotoDownloadStatusListener onPhotoDownloadStatusListener = this;
        Runnable runnable = () -> {

            DownloadPhotosAndComments downloadPhotosAndComments = DownloadPhotosAndComments.getInstance();
            downloadPhotosAndComments.transferQuery(username, adapter.getOnPhotoObjectListener(), onPhotoDownloadStatusListener);
        };
        Handler handler = new Handler();
        handler.post(runnable);
    }

    private void sendButtonOnClickListener() {

        sendButton.setOnClickListener(v -> {

            if (!Objects.equals(ImageID, "")) {

                String comment = addCommentEditText.getText().toString();

                addCommentLayout.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(addCommentEditText.getWindowToken(), 0);
                addCommentEditText.setText("");

                ParseObject addComment = new ParseObject("Comment");

                addComment.put("photoId", ImageID);
                addComment.put("authorId", ParseUser.getCurrentUser().getUsername());
                addComment.put("comment", comment);

                addComment.saveInBackground(e -> {
                    if (e == null) {
                        Log.i("Comment", "Added");
                    } else {
                        Log.i("Comment", "Error");
                    }
                });
                ImageID = "";
            }
        });
    }

    private void setOnRefreshListener() {
        refreshLayout.setOnRefreshListener(this::refresh);
    }

    public void refresh() {
        adapter.clearAdapter();
        transferQuery();
    }

    private void keyBoardEventListener() {

        KeyboardVisibilityEvent.setEventListener(getActivity(), isOpen -> {
            if (!isOpen) {
                addCommentLayout.setVisibility(View.GONE);
                ImageID = "";
                addCommentEditText.setText("");
                addCommentEditText.clearFocus();
            }
        });
    }

    @Override
    public void onStatusChange(boolean loading) {
        refreshLayout.setRefreshing(loading);
    }
}
