package com.example.pawel.udemy_instagramclone.myImageGallery;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pawel on 01.05.2018.
 */

public class LoadPhotosFromMemory {
    private static final LoadPhotosFromMemory ourInstance = new LoadPhotosFromMemory();

    public static LoadPhotosFromMemory getInstance() {
        return ourInstance;
    }

    private LoadPhotosFromMemory() {
    }

    OnPhotoLoadedListener onPhotoLoadedListener;

    AlbumLoader albumLoader;
    Cursor albumCursor;
    Cursor photoCursor;
    private List<Photo> albums;
    Activity activity;


    public void proceedToLoading(Activity activity, OnPhotoLoadedListener onPhotoLoadedListener) {

        this.onPhotoLoadedListener = onPhotoLoadedListener;
        this.activity = activity;
        albums = new ArrayList<>();
        albumLoader = new AlbumLoader(this.activity);
        albumCursor = albumLoader.loadInBackground();

        startLoading();
    }

    private void startLoading() {

        if (albumCursor != null && albumCursor.moveToFirst()) {
            loadAlbums();
        }
        if (albumCursor != null) {
            albumCursor.close();
        }
        albums.add(new Photo());
        onPhotoLoadedListener.photoLoaded(new Photo());
    }

    private void loadAlbums() {

        do {
            loadPhotos();
        } while (areThereMoreAlbums());
    }

    private void loadPhotos() {

        PhotoLoader photoLoader = new PhotoLoader(albumLoader.getContext(), new String[]{albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))});
        photoCursor = photoLoader.loadInBackground();

        if (photoCursor != null && photoCursor.moveToFirst()) {
            addPhotos();
        }
        if (photoCursor != null)
            photoCursor.close();

    }

    private void addPhotos() {
        do {
            Photo photo = createPhotoObject();
            albums.add(photo);
            onPhotoLoadedListener.photoLoaded(photo);
        }
        while (areThereMorePhotos());
    }

    private Photo createPhotoObject() {

        Long id = photoCursor.getLong(photoCursor.getColumnIndex(MediaStore.Images.Media._ID));
        Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
        Photo photo = new Photo();
        photo.id = id;
        photo.uri = uri;
        photo.isSelected = false;

        return photo;
    }

    private boolean areThereMorePhotos() {
        return (photoCursor.moveToNext() && albums.size() < 40);
    }

    private boolean areThereMoreAlbums() {
        return (albumCursor.moveToNext() && albums.size() < 40);
    }

    public interface OnPhotoLoadedListener {
        void photoLoaded(Photo photo);
    }
}
