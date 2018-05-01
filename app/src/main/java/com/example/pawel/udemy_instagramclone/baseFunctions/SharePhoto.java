package com.example.pawel.udemy_instagramclone.baseFunctions;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pawel.udemy_instagramclone.R;
import com.example.pawel.udemy_instagramclone.SquareCropView;
import com.example.pawel.udemy_instagramclone.baseFunctions.mainGallery.Gallery;
import com.example.pawel.udemy_instagramclone.myImageGallery.AlbumLoader;
import com.example.pawel.udemy_instagramclone.myImageGallery.GalleryAdapter;
import com.example.pawel.udemy_instagramclone.myImageGallery.LoadPhotosFromMemory;
import com.example.pawel.udemy_instagramclone.myImageGallery.PhotoLoader;
import com.example.pawel.udemy_instagramclone.myImageGallery.Photo;
import com.example.pawel.udemy_instagramclone.myImageGallery.UploadChoosenImage;
import com.fenchtose.nocropper.BitmapResult;
import com.fenchtose.nocropper.CropState;
import com.fenchtose.nocropper.CropperView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class SharePhoto extends Fragment implements UploadChoosenImage.OnImageSharedListener {
    private static final SharePhoto ourInstance = new SharePhoto();

    public static SharePhoto getInstance() {
        return ourInstance;
    }

    private View v;
    private static final int RC_PHOTO_PICKER = 2;

    private int actionType;
    private RecyclerView myGalleryView;
    private CropperView cropView;
    private EditText addDescription;
    private SquareCropView squareCropView;
    GalleryAdapter myGalleryAdapter;


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.crop_image_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accept_changes: {
                acceptCrop();
                break;
            }
            case R.id.decline_changes: {
                declineCrop();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.activity_share_photo, container, false);

        return v;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initialization();

        getAlbumsIfListIsEmpty();

        setupGalleryRecyclerView();

        setupGalleryAdapter();
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);

        if (args != null) {
            actionType = args.getInt("type");
        }
    }


    private void initialization() {

        findViews();

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setHasOptionsMenu(true);

        myGalleryAdapter = new GalleryAdapter(getContext());

        cropView.setGridCallback(new CropperView.GridCallback() {
            @Override
            public boolean onGestureStarted() {
                return true;
            }

            @Override
            public boolean onGestureCompleted() {
                return false;
            }
        });

        requestPermission();
    }

    private void findViews() {

        cropView = v.findViewById(R.id.cropView);
        addDescription = v.findViewById(R.id.addDescription);
        squareCropView = v.findViewById(R.id.squareCropViewRelativeLayout);
    }

    private void requestPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {

                getAlbums();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            getAlbums();
        }

    }

    private void getAlbums() {

        try {
            LoadPhotosFromMemory loadPhotosFromMemory = LoadPhotosFromMemory.getInstance();
            loadPhotosFromMemory.proceedToLoading(getActivity(), myGalleryAdapter.getOnPhotoLoadListener());
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }


    private void getAlbumsIfListIsEmpty() {

        if (myGalleryAdapter.isEmpty()) {
            getAlbums();
        }
    }

    private void setupGalleryRecyclerView() {

        myGalleryView = v.findViewById(R.id.myGalleryView);
        myGalleryView.setHasFixedSize(true);
        myGalleryView.setLayoutManager(new GridLayoutManager(getContext(), 3));
    }

    private void setupGalleryAdapter() {

        myGalleryView.setAdapter(myGalleryAdapter);

        myGalleryAdapter.setOnItemSelectedListener(new GalleryAdapter.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Photo mPhoto) {

                if (mPhoto.uri != null) {
                    Uri mUri = mPhoto.uri;
                    setupCropView(mUri);
                } else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            setupCropView(uri);
        }
    }

    private void setupCropView(Uri mUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mUri);
            cropView.setImageBitmap(bitmap);
            squareCropView.setVisibility(View.VISIBLE);

            if (actionType == 1) {
                addDescription.setVisibility(View.VISIBLE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acceptCrop() {

        BitmapResult result = cropView.getCroppedBitmap();

        if (result.getState() == CropState.FAILURE_GESTURE_IN_PROCESS) {
            Toast.makeText(getActivity(), "unable to crop. Gesture in progress", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bitmap = result.getBitmap();

        if (bitmap != null) {
            //Log.d("Cropper", "crop1 bitmap: " + bitmap.getWidth() + ", " + bitmap.getHeight());
            UploadChoosenImage uploadChoosenImage = UploadChoosenImage.getInstance();

            if (actionType == 1) {
                String description = addDescription.getText().toString();
                uploadChoosenImage.uploadImage(getActivity(), bitmap, description, this);
            } else {
                uploadChoosenImage.uploadImage(getActivity(), bitmap, this);
            }
        }
    }

    private void declineCrop() {
        closeFragment();
    }

    private void closeFragment() {
        //getFragmentManager().beginTransaction().remove(this).commit();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Gallery fragment = Gallery.getInstance();
        fragmentTransaction.replace(R.id.active_fragment, fragment);

        getActivity().findViewById(R.id.listOfAllPhotos).setAlpha((float) 1.0);
        getActivity().findViewById(R.id.addNewPhoto).setAlpha((float) 0.3);
        getActivity().findViewById(R.id.myProfile).setAlpha((float) 0.3);

        fragmentTransaction.commit();
    }

    @Override
    public void onSuccessful() {
        Toast.makeText(getActivity(), "Successfully uploaded image", Toast.LENGTH_LONG).show();
        closeFragment();
    }

    @Override
    public void onFailure() {
        Toast.makeText(getActivity(), "Unsuccessfully uploaded image", Toast.LENGTH_LONG).show();
    }
}
