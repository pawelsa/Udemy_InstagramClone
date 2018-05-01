package com.example.pawel.udemy_instagramclone;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.parse.LogOutCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Pawel on 01.05.2018.
 */

public class UserOnlineStatusManager {
    private static final UserOnlineStatusManager ourInstance = new UserOnlineStatusManager();

    private Activity activity;
    private UserOnlineStatusListener userOnlineStatusListener;

    public static final int LOGIN_REQUEST = 1;

    public static UserOnlineStatusManager getInstance() {
        return ourInstance;
    }

    private UserOnlineStatusManager() {
    }

    public void setupLogin(Activity activity, UserOnlineStatusListener userOnlineStatusListener) {
        this.userOnlineStatusListener = userOnlineStatusListener;
        this.activity = activity;
        parseInitialization();
        checkIfUserIsLoggedIn();
    }

    private void parseInitialization() {

        Parse.enableLocalDatastore(activity);

        // Add your initialization code here
        Parse.initialize(new Parse.Configuration.Builder(activity)
                .applicationId("8c6f3c6c1bb88c1f3a6bc607368019f43db8ce1b")
                .clientKey("c8d8271fb51a5aaf7bfd38d6976666755b626665")
                .server("http://13.58.211.243:80/parse/")
                .build()
        );
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

    }

    private void checkIfUserIsLoggedIn() {

        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser != null) {
            userOnlineStatusListener.userLoggedIn();
        } else {
            Intent intent = new Intent(activity, Welcome_Login.class);
            activity.startActivityForResult(intent, LOGIN_REQUEST);
        }
    }

    public void onActivityResult(int requestCode, int resultCode) {

        Log.i("onActivityResult", "onActivityResult");
        if (requestCode == LOGIN_REQUEST && resultCode == RESULT_OK) {
            userOnlineStatusListener.userLoggedIn();
        } else {
            activity.finish();
        }
    }

    public void logOut() {
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(activity, "Logged Out", Toast.LENGTH_LONG).show();
                checkIfUserIsLoggedIn();
            }
        });
    }


    public interface UserOnlineStatusListener {
        void userLoggedIn();
    }

}
