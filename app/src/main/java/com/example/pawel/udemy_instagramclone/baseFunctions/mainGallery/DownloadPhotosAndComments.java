package com.example.pawel.udemy_instagramclone.baseFunctions.mainGallery;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Pawel on 01.05.2018.
 */

public class DownloadPhotosAndComments {
    private static final DownloadPhotosAndComments ourInstance = new DownloadPhotosAndComments();

    public static DownloadPhotosAndComments getInstance() {
        return ourInstance;
    }

    private DownloadPhotosAndComments() {
    }


    private ParseQuery<ParseObject> query;
    private String username;
    private OnPhotoObjectListener onPhotoObjectListener;
    private OnPhotoDownloadStatusListener onPhotoDownloadStatusListener;

    public void transferQuery(String username, OnPhotoObjectListener onPhotoObjectListener, OnPhotoDownloadStatusListener onPhotoDownloadStatusListener) {
        this.username = username;
        this.onPhotoObjectListener = onPhotoObjectListener;
        this.onPhotoDownloadStatusListener = onPhotoDownloadStatusListener;

        setupImageQuery();

        readPhotoObjectsInBackground();
    }

    private void setupImageQuery() {

        query = ParseQuery.getQuery("Image");

        query.addDescendingOrder("createdAt");
        query.orderByDescending("createdAt");

        if (username != null) {
            query.whereEqualTo("username", username);
        }
        query.addDescendingOrder("createdAt");
        query.orderByDescending("createdAt");
    }

    private void readPhotoObjectsInBackground() {

        onPhotoDownloadStatusListener.onStatusChange(true);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null && objects != null && objects.size() > 0) {

                    createListOfPhotoObjects(objects);

                } else {
                    if (e != null)
                        Log.i("Error", e.getMessage().toString());
                }
                onPhotoDownloadStatusListener.onStatusChange(false);
            }
        });
    }

    private void createListOfPhotoObjects(List<ParseObject> objects) {

        for (final ParseObject obj : objects) {

            ParseFile file = (ParseFile) obj.get("image");
            PhotoInfoObject photoInfoObject = new PhotoInfoObject(obj.getString("username"), obj.getString("description"), file.getUrl(), getAvatarBitmap(obj.getString("username")), obj.getObjectId(), getPhotoComments(obj));
            onPhotoObjectListener.onCreated(photoInfoObject);
        }
    }

    private String getAvatarBitmap(String mUsername) {

        ParseQuery<ParseUser> user = ParseUser.getQuery();
        user.whereEqualTo("username", mUsername);

        try {
            List<ParseUser> users = new ArrayList<>(user.find());

            ParseUser chosenUser = users.get(0);

            ParseFile avatarFile = (ParseFile) chosenUser.get("avatar");
            return avatarFile.getUrl();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<Comment> getPhotoComments(ParseObject obj) {
        final List<Comment> objComments = createCommentList(obj.getObjectId());

        Collections.reverse(objComments);

        objComments.add(new Comment());
        return objComments;
    }

    private List<Comment> createCommentList(String photoID) {

        ParseQuery<ParseObject> getComments = ParseQuery.getQuery("Comment");
        getComments.whereEqualTo("photoId", photoID);
        getComments.addDescendingOrder("createdAt");

        List<Comment> objComments = new ArrayList<>();

        List<ParseObject> comObj;

        try {
            comObj = getComments.find();

            int limit = 0;
            for (ParseObject getOne : comObj) {

                objComments.add(new Comment(getOne.getString("comment"), getOne.getString("authorId")));

                if (limit == 2) {
                    break;
                }
                limit++;
            }
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return objComments;
    }

    public interface OnPhotoObjectListener {
        void onCreated(PhotoInfoObject photoInfoObject);
    }

    public interface OnPhotoDownloadStatusListener {
        void onStatusChange(boolean loading);
    }
}
