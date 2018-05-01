package com.example.pawel.udemy_instagramclone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import static com.example.pawel.udemy_instagramclone.UserOnlineStatusManager.LOGIN_REQUEST;


public class Welcome_Login extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener {

    private EditText usernameET;
    private EditText passwordET;
    private TextView textView;
    private RelativeLayout relativeLayout;
    private ImageView imageView;
    private Button button;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (ParseUser.getCurrentUser() == null) {
            Intent intent=new Intent();
            intent.putExtra("STH","STH");
            setResult(RESULT_CANCELED, intent);
            finish();
            //Welcome_Login.super.onBackPressed();
        }
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            if (button.getText().equals(getString(R.string.login))) {
                logIn();
            } else {
                signUp();
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainMethod();
    }

    private void mainMethod() {

        initialize();

        setButtonOnClickListener();

        setTextViewOnClickListener();

        setListenersForElementsWhichCloseKeyboard();

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    private void initialize() {

        setContentView(R.layout.activity_fullscreen);

        setupActionbar();

        usernameET = findViewById(R.id.usernameET);
        passwordET = findViewById(R.id.passwordET);
        button = findViewById(R.id.logInButton);
        textView = findViewById(R.id.signInButton);
        relativeLayout = findViewById(R.id.background);
        imageView = findViewById(R.id.instagramLogo);
    }

    private void setupActionbar(){
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null)
            actionBar.setDisplayHomeAsUpEnabled(false);
    }

    private void setButtonOnClickListener() {

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseActionLoginOrSignup();
            }
        });
    }

    private void chooseActionLoginOrSignup() {

        if (button.getText().toString().equals(getString(R.string.login))) {
            logIn();
        } else {
            signUp();
        }
    }

    private void logIn() {

        ParseUser.logOut();

        String username = usernameET.getText().toString();

        while (username.endsWith(" ")) {
            username = username.trim();
        }

        ParseUser.logInInBackground(username, passwordET.getText().toString(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {

                if (e == null) {
                    Toast.makeText(getApplicationContext(), "Logged In succesfully", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent();
                    intent.putExtra("STH","STH");
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Couldn't log in : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void signUp() {

        ParseUser user = prepareUserToSignup();

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "Signed In succesfully !", Toast.LENGTH_LONG).show();
                    onActivityResult(LOGIN_REQUEST, RESULT_OK, null);
                } else {
                    Toast.makeText(getApplicationContext(), "Signed In unsuccesfully", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private ParseUser prepareUserToSignup() {

        ParseUser user = new ParseUser();

        String username = usernameET.getText().toString();
        while (username.endsWith(" ")) {
            username.substring(0, username.length() - 1);
        }
        user.setUsername(username);
        user.setPassword(passwordET.getText().toString());

        return user;
    }

    private void setTextViewOnClickListener() {

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (button.getText().toString().equals(getString(R.string.login))) {
                    button.setText(R.string.signin);
                    textView.setText(R.string.login);
                } else {
                    button.setText(R.string.login);
                    textView.setText(R.string.signin);
                }
            }
        });
    }

    private void setListenersForElementsWhichCloseKeyboard() {
        passwordET.setOnKeyListener(this);
        relativeLayout.setOnClickListener(this);
        imageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.instagramLogo || view.getId() == R.id.background) {

            InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


}
