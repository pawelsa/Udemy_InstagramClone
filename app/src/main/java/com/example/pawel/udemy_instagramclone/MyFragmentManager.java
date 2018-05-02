package com.example.pawel.udemy_instagramclone;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.pawel.udemy_instagramclone.baseFunctions.Contacts;
import com.example.pawel.udemy_instagramclone.baseFunctions.SharePhoto;
import com.example.pawel.udemy_instagramclone.baseFunctions.mainGallery.Gallery;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Pawel on 02.05.2018.
 */

public class MyFragmentManager {
    private static final MyFragmentManager ourInstance = new MyFragmentManager();

    public static MyFragmentManager getInstance() {
        return ourInstance;
    }

    private Hashtable<Integer, Fragment> fragmentHashtable = new Hashtable<>();
    private List<String> TAGList = new ArrayList<>();


    public static final String GALLERY_TAG = "gallery_fragment";
    public static final String USER_GALLERY_TAG = "user_gallery_fragment";
    public static final String CONTACTS_TAG = "contacts_fragment";
    public static final String SHARE_PHOTO_TAG = "sharePhoto_fragment";
    public static final String ADD_AVATAR = "add_avatar";

    private Gallery gallery;
    private SharePhoto sharePhoto;
    private Contacts contacts;

    private ImageView allPhotos;
    private ImageView addPhoto;
    private ImageView myProfile;

    private AppCompatActivity activity;

    private FragmentManager fragmentManager;

    public void startMyFragmentManager(AppCompatActivity activity) {
        ourInstance.activity = activity;

        ourInstance.allPhotos = activity.findViewById(R.id.listOfAllPhotos);
        ourInstance.addPhoto = activity.findViewById(R.id.addNewPhoto);
        ourInstance.myProfile = activity.findViewById(R.id.myProfile);

        ourInstance.gallery = new Gallery();
        ourInstance.fragmentHashtable.put(0, ourInstance.gallery);
        ourInstance.TAGList.add(0, GALLERY_TAG);
        ourInstance.sharePhoto = SharePhoto.getInstance();
        ourInstance.contacts = Contacts.getInstance();

        ourInstance.fragmentManager = activity.getSupportFragmentManager();

        ourInstance.startFirstFragment();

        ourInstance.onButtonClick();
    }

    private void onButtonClick() {

        ourInstance.allPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(GALLERY_TAG);
            }
        });

        ourInstance.addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(SHARE_PHOTO_TAG);
            }
        });

        ourInstance.myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(CONTACTS_TAG);
            }
        });
    }

    private void startFirstFragment() {

        FragmentTransaction fragmentTransaction = ourInstance.fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.active_fragment, ourInstance.gallery);
        fragmentTransaction.commit();
    }

    public void startFragment(String TAG) {
        ourInstance.startFragment(TAG, "");
    }

    public void startFragment(String TAG, String username) {

        FragmentTransaction fragmentTransaction = ourInstance.fragmentManager.beginTransaction();

        setIconVisibility(TAG);

        switch (TAG) {

            default:
            case GALLERY_TAG:
                String mTAG = GALLERY_TAG;
                Gallery mGallery = new Gallery();
                if (!username.equals("")) {
                    Bundle args = new Bundle();
                    args.putString("username", username);
                    mGallery.setArguments(args);
                    mTAG = USER_GALLERY_TAG;
                }
                fragmentTransaction.replace(R.id.active_fragment, mGallery, mTAG);
                ourInstance.TAGList.add(0, mTAG);
                ourInstance.fragmentHashtable.put(ourInstance.fragmentHashtable.size(), mGallery);
                break;
            case SHARE_PHOTO_TAG:
                Bundle args = new Bundle();

                if (username.equals(ADD_AVATAR)) {
                    args.putInt("type", 2);
                } else
                    args.putInt("type", 1);
                ourInstance.sharePhoto.setArguments(args);

                fragmentTransaction.replace(R.id.active_fragment, ourInstance.sharePhoto, SHARE_PHOTO_TAG);
                ourInstance.TAGList.add(0, SHARE_PHOTO_TAG);
                ourInstance.fragmentHashtable.put(ourInstance.fragmentHashtable.size(), ourInstance.sharePhoto);
                break;
            case CONTACTS_TAG:

                fragmentTransaction.replace(R.id.active_fragment, ourInstance.contacts, CONTACTS_TAG);
                ourInstance.TAGList.add(0, CONTACTS_TAG);
                ourInstance.fragmentHashtable.put(ourInstance.fragmentHashtable.size(), ourInstance.contacts);
                break;
        }

        fragmentTransaction.commit();
    }

    private void setIconVisibility(String TAG) {

        ourInstance.allPhotos.setAlpha((float) 0.3);
        ourInstance.addPhoto.setAlpha((float) 0.3);
        ourInstance.myProfile.setAlpha((float) 0.3);

        switch (TAG) {
            case GALLERY_TAG: {
                ourInstance.allPhotos.setAlpha((float) 1.0);
                break;
            }
            case SHARE_PHOTO_TAG: {
                ourInstance.addPhoto.setAlpha((float) 1.0);
                break;
            }
            case CONTACTS_TAG: {
                ourInstance.myProfile.setAlpha((float) 1.0);
                break;
            }
        }
    }

    public void popBackStack() {
        int size = ourInstance.fragmentHashtable.size() - 1;

        if (size > 0) {

            ourInstance.fragmentHashtable.remove(size);
            ourInstance.TAGList.remove(0);
            size--;
            Fragment fragment = ourInstance.fragmentHashtable.get(size);
            String TAG = ourInstance.TAGList.get(0);
            ourInstance.setIconVisibility(TAG);

            ourInstance.fragmentManager.beginTransaction()
                    .replace(R.id.active_fragment, fragment).commit();
        } else
            activity.finish();
    }
}
