package com.example.pawel.udemy_instagramclone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertNegative extends AppCompatActivity {

    ImageView positive;


    public class DownloadTask extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... params) {

            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();

                return BitmapFactory.decodeStream(in);

            } catch (java.io.IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialization();

        beginConversion();
    }


    private void initialization(){

        setContentView(R.layout.activity_convert_negative);
        positive = findViewById(R.id.positive);
    }

    private void beginConversion(){

        String imageURL = getImageURL();

        if (imageURL != null) {

            DownloadTask downloadTask = new DownloadTask();

            try {

                Bitmap originalImage = downloadTask.execute(imageURL).get();

                if (originalImage != null) {

                    Bitmap originalImageFitScreen = fitToScreen(originalImage);

                    if (originalImageFitScreen != null) {

                        Bitmap convertedImage = convertImage(originalImageFitScreen);

                        setConvertedImage(convertedImage);
                    }


                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void setConvertedImage(Bitmap convertedImage){

        if (convertedImage != null) {

            positive.setImageBitmap(convertedImage);
        }
    }

    private Bitmap fitToScreen(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int newWidth = size.x;

        Matrix matrix = new Matrix();

        float scaleWidth = ((float) newWidth) / width;

        matrix.postScale(scaleWidth, scaleWidth);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);

        bitmap.recycle();

        return resizedBitmap;
    }

    public Bitmap convertImage(Bitmap original) {

        final int RGB_MASK = 0x00FFFFFF;

        // Create mutable Bitmap to invert, argument true makes it mutable
        Bitmap inversion = original.copy(Bitmap.Config.ARGB_8888, true);

        // Get info about Bitmap
        int width = inversion.getWidth();
        int height = inversion.getHeight();
        int pixels = width * height;

        // Get original pixels
        int[] pixel = new int[pixels];
        inversion.getPixels(pixel, 0, width, 0, 0, width, height);

        // Modify pixels
        for (int i = 0; i < pixels; i++)
            pixel[i] ^= RGB_MASK;
        inversion.setPixels(pixel, 0, width, 0, 0, width, height);

        // Return inverted Bitmap
        return inversion;
    }

    private String getImageURL() {


        Intent intent = getIntent();

        if (intent != null) {

            String address = intent.getStringExtra(Intent.EXTRA_TEXT);

            return constructFinalURL(address);
        }

        return null;
    }

    private String constructFinalURL(String address){


        String baseImageURL = "https://images-cdn.9gag.com/photo/";
        String endBaseImageURL = "_700b.jpg";

        String importantPart = getImportantPart(address);

        String finalURLtoDownloadImage = baseImageURL + importantPart + endBaseImageURL;

        Log.i("Final URL", finalURLtoDownloadImage);

        return finalURLtoDownloadImage;
    }

    private String getImportantPart(String address){


        Pattern p = Pattern.compile("https://9gag.com/gag/(.*?)ref=android");
        Matcher m = p.matcher(address);

        String importantPart = "";

        while (m.find()) {

            importantPart = m.group(1);
        }

        importantPart = importantPart.substring(0, Math.min(importantPart.length(), importantPart.length() - 1));

        return importantPart;
    }
}

