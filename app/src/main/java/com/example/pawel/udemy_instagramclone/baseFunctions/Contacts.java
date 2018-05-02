package com.example.pawel.udemy_instagramclone.baseFunctions;

import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.pawel.udemy_instagramclone.MyFragmentManager;
import com.example.pawel.udemy_instagramclone.R;
import com.example.pawel.udemy_instagramclone.UserOnlineStatusManager;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static com.example.pawel.udemy_instagramclone.MyFragmentManager.ADD_AVATAR;
import static com.example.pawel.udemy_instagramclone.MyFragmentManager.GALLERY_TAG;
import static com.example.pawel.udemy_instagramclone.MyFragmentManager.SHARE_PHOTO_TAG;

public class Contacts extends android.support.v4.app.Fragment {
    private static final Contacts ourInstance = new Contacts();

    public static Contacts getInstance() {
        return ourInstance;
    }

    ListView listView;

    ArrayList<String> users;
    ArrayAdapter<String> adapter;


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.share_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                MyFragmentManager myFragmentManager = MyFragmentManager.getInstance();
                myFragmentManager.popBackStack();
                break;
            case R.id.logoutMenu:
                logoutUser();
                break;
            case R.id.addAvatar:
                startSharePhotoFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {

        UserOnlineStatusManager userOnlineStatusManager = UserOnlineStatusManager.getInstance();
        userOnlineStatusManager.logOut();
    }

    private void startSharePhotoFragment() {

        MyFragmentManager myFragmentManager = MyFragmentManager.getInstance();
        myFragmentManager.startFragment(SHARE_PHOTO_TAG, ADD_AVATAR);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_contacts, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.contactList);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainMethod();
    }

    private void mainMethod() {

        initialize();

        setAdapter();

        createUsersList();

        openGalleryWhenUsernameClicked();
    }

    private void initialize() {

        configureActionBar();

        users = new ArrayList<>();
    }

    private void configureActionBar() {

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
            actionBar.setTitle(ParseUser.getCurrentUser().getUsername());
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setHasOptionsMenu(true);
    }

    private void setAdapter() {

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, users);

        listView.setAdapter(adapter);
    }

    private void createUsersList() {

        ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.addAscendingOrder("username");

        query.findInBackground(new FindCallback<ParseUser>() {

            @Override
            public void done(List<ParseUser> objects, ParseException e) {

                if (e == null && objects.size() > 0) {

                    for (ParseUser user : objects)
                        users.add(user.getUsername());
                } else {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void openGalleryWhenUsernameClicked() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MyFragmentManager myFragmentManager=MyFragmentManager.getInstance();
                myFragmentManager.startFragment(GALLERY_TAG, listView.getItemAtPosition(position).toString());
            }
        });

    }

    public void refresh() {

        users.clear();

        createUsersList();
    }
}
