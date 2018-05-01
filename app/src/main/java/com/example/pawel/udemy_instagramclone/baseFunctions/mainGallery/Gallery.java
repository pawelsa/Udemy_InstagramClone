package com.example.pawel.udemy_instagramclone.baseFunctions.mainGallery;


import android.content.Context;
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


public class Gallery extends android.support.v4.app.Fragment implements DownloadPhotosAndComments.OnPhotoDownloadStatusListener{
    private static final Gallery ourInstance = new Gallery();

    public static Gallery getInstance() {
        return ourInstance;
    }

    private View v;


    private SwipeRefreshLayout refreshLayout;
    private CustomGalleryAdapter adapter;
    private RecyclerView layout;

    private RelativeLayout addCommentLayout;
    private EditText addCommentEditText;
    private ImageButton sendButton;

    private String username = null;

    private String ImageID;

    private ArrayList<PhotoInfoObject> photoObjects;

    private static startNewFragment startNewFragment;


    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);

        username = args.getString("username");
    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.activity_gallery, container, false);

        return v;
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

        layout = v.findViewById(R.id.contentListView);
        refreshLayout = v.findViewById(R.id.refreshLayout);

        addCommentLayout = v.findViewById(R.id.addCommentLayout);
        addCommentEditText = v.findViewById(R.id.enterCommentEditText);
        sendButton = v.findViewById(R.id.sendButton);

        layout.setHasFixedSize(true);
        layout.setLayoutManager(new LinearLayoutManager(getActivity()));
        layout.setItemAnimator(new DefaultItemAnimator());

        photoObjects = new ArrayList<>();
    }

    private void setAdapter() {

        adapter = new CustomGalleryAdapter(getActivity().getApplicationContext());

        adapter.setOnNameClickListener(new CustomGalleryAdapter.OnNameClickListener() {
            @Override
            public void onNameClicked(String username) {

                startNewFragment.openNewFragment(username);
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

        final DownloadPhotosAndComments.OnPhotoDownloadStatusListener onPhotoDownloadStatusListener=this;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DownloadPhotosAndComments downloadPhotosAndComments = DownloadPhotosAndComments.getInstance();
                downloadPhotosAndComments.transferQuery(username, adapter.getOnPhotoObjectListener(), onPhotoDownloadStatusListener);
            }
        });
    }

    private void sendButtonOnClickListener() {

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                    addComment.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            if (e == null) {

                                Log.i("Comment", "Added");
                            } else {

                                Log.i("Comment", "Error");
                            }
                        }
                    });

                    ImageID = "";
                }
            }
        });
    }


    private void setOnRefreshListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    public void refresh() {
        adapter.clearAdapter();
        transferQuery();
    }


    private void keyBoardEventListener() {

        KeyboardVisibilityEvent.setEventListener(getActivity(), new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {

                if (!isOpen) {

                    addCommentLayout.setVisibility(View.GONE);
                    ImageID = "";
                    addCommentEditText.setText("");
                    addCommentEditText.clearFocus();
                }
            }
        });
    }

    @Override
    public void onStatusChange(boolean loading) {
        refreshLayout.setRefreshing(loading);
    }

    public static void setStartNewFragment(startNewFragment mStartNewFragment) {

        startNewFragment = mStartNewFragment;
    }

    public interface startNewFragment {

        void openNewFragment(String username);
    }
}
