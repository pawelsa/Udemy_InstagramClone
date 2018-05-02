package com.example.pawel.udemy_instagramclone.baseFunctions;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.pawel.udemy_instagramclone.MyFragmentManager;
import com.example.pawel.udemy_instagramclone.R;
import com.example.pawel.udemy_instagramclone.UserOnlineStatusManager;

import static com.example.pawel.udemy_instagramclone.UserOnlineStatusManager.LOGIN_REQUEST;

public class MainActivity extends AppCompatActivity implements UserOnlineStatusManager.UserOnlineStatusListener {

    private UserOnlineStatusManager userOnlineStatusManager;

    MyFragmentManager myFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userOnlineStatusManager = UserOnlineStatusManager.getInstance();
        userOnlineStatusManager.setupLogin(this, this);
    }

    @Override
    public void onBackPressed() {
        myFragmentManager.popBackStack();
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

        setContentView(R.layout.activity_main);
        myFragmentManager = MyFragmentManager.getInstance();
        myFragmentManager.startMyFragmentManager(this);
    }
}
