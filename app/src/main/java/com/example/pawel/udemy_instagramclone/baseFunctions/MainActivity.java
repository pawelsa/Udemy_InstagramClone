package com.example.pawel.udemy_instagramclone.baseFunctions;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.pawel.udemy_instagramclone.R;
import com.example.pawel.udemy_instagramclone.UserOnlineStatusManager;
import com.example.pawel.udemy_instagramclone.baseFunctions.mainGallery.Gallery;

import static com.example.pawel.udemy_instagramclone.UserOnlineStatusManager.LOGIN_REQUEST;

public class MainActivity extends AppCompatActivity implements UserOnlineStatusManager.UserOnlineStatusListener {

    public static final String GALLERY_TAG = "gallery_fragment";
    public static final String CONTACTS_TAG = "contacts_fragment";
    public static final String SHARE_PHOTO_TAG = "sharePhoto_fragment";

    private UserOnlineStatusManager userOnlineStatusManager;

    private ImageView allPhotos;
    private ImageView addPhoto;
    private ImageView myProfile;

    /**
     * 1   -   Gallery
     * 2   -   SharePhoto
     * 3   -   Contacts
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userOnlineStatusManager = UserOnlineStatusManager.getInstance();
        userOnlineStatusManager.setupLogin(this, this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    private void initialization() {
        setContentView(R.layout.activity_main);

        allPhotos = findViewById(R.id.listOfAllPhotos);
        addPhoto = findViewById(R.id.addNewPhoto);
        myProfile = findViewById(R.id.myProfile);


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Gallery fragment = new Gallery();
        fragmentTransaction.add(R.id.active_fragment, fragment);
        fragmentTransaction.commit();
    }

    public void startFragment(int whichOne, String username) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragment;
        String TAG;

        allPhotos.setAlpha((float) 0.3);
        addPhoto.setAlpha((float) 0.3);
        myProfile.setAlpha((float) 0.3);

        switch (whichOne) {

            default:
            case 1:
                fragment = new Gallery();
                if (!username.equals("")) {
                    Bundle args = new Bundle();
                    args.putString("username", username);
                    fragment.setArguments(args);
                }
                allPhotos.setAlpha((float) 1.0);
                TAG = GALLERY_TAG;
                break;
            case 2:
                fragment = new SharePhoto();
                Bundle args = new Bundle();
                args.putInt("type", 1);
                fragment.setArguments(args);
                addPhoto.setAlpha((float) 1.0);
                TAG = SHARE_PHOTO_TAG;
                break;
            case 3:
                fragment = new Contacts();
                myProfile.setAlpha((float) 1.0);
                TAG = CONTACTS_TAG;
                break;
        }
        fragmentTransaction.replace(R.id.active_fragment, fragment, TAG);
        fragmentTransaction.addToBackStack(null).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_REQUEST) {
            userOnlineStatusManager.onActivityResult(requestCode, resultCode);
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void userLoggedIn() {

        initialization();

        allPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (allPhotos.getAlpha() == 1) {
                    Gallery generalGallery = (Gallery)
                            getSupportFragmentManager().findFragmentById(R.id.active_fragment);
                    if (generalGallery != null) {
                        generalGallery.refresh();
                    }
                } else {
                    startFragment(1, "");
                }
            }
        });


        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(2, "");
            }
        });

        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (myProfile.getAlpha() == 1) {
                    Contacts contacts = (Contacts) getSupportFragmentManager().findFragmentById(R.id.active_fragment);
                    if (contacts != null) {
                        contacts.refresh();
                    }
                } else {
                    startFragment(3, "");
                }
            }
        });

        Gallery.setStartNewFragment(new Gallery.startNewFragment() {
            @Override
            public void openNewFragment(String username) {
                startFragment(1, username);
            }
        });
    }
}
