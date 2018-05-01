package com.example.pawel.udemy_instagramclone.baseFunctions;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Toast;

import com.example.pawel.udemy_instagramclone.R;
import com.example.pawel.udemy_instagramclone.UserOnlineStatusManager;
import com.example.pawel.udemy_instagramclone.baseFunctions.mainGallery.Gallery;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class Contacts extends android.support.v4.app.Fragment {
    private static final Contacts ourInstance = new Contacts();
    public static Contacts getInstance() {
        return ourInstance;
    }

    View v;

    ListView listView;
    android.support.v7.widget.Toolbar toolbar;

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
                FragmentManager fm =  getActivity().getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
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

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        SharePhoto fragment = new SharePhoto();

        Bundle bundle = new Bundle();
        bundle.putInt("type", 2);
        fragment.setArguments(bundle);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.active_fragment, fragment);
        fragmentTransaction.commit();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.activity_contacts, container, false);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainMethod(view);
    }

    private void mainMethod(View view) {

        initialize(view);

        setAdapter();

        createUsersList();

        openGalleryWhenUsernameClicked();
    }

    private void initialize(View view) {

        listView = view.findViewById(R.id.contactList);

        configureToolbar();

        users = new ArrayList<>();
    }

    private void configureToolbar() {
/*
        toolbar.setTitle(ParseUser.getCurrentUser().getUsername());

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);*/
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar!=null){
            actionBar.show();
            actionBar.setTitle(ParseUser.getCurrentUser().getUsername());
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setHasOptionsMenu(true);
    }

    public void refresh() {

        users.clear();

        createUsersList();
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

                    for (ParseUser user : objects) {

                        users.add(user.getUsername());
                    }

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

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                Gallery fragment = new Gallery();

                Bundle bundle = new Bundle();
                bundle.putString("username", listView.getItemAtPosition(position).toString());
                fragment.setArguments(bundle);

                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.active_fragment, fragment);
                fragmentTransaction.commit();

            }
        });
    }

    void closeFragment() {

        //getFragmentManager().beginTransaction().remove(this).commit();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        Gallery fragment = new Gallery();
        fragmentTransaction.replace(R.id.active_fragment, fragment);


        getActivity().findViewById(R.id.listOfAllPhotos).setAlpha((float) 1.0);
        getActivity().findViewById(R.id.addNewPhoto).setAlpha((float) 0.3);
        getActivity().findViewById(R.id.myProfile).setAlpha((float) 0.3);


        fragmentTransaction.commit();
    }


}
