package com.example.pawel.udemy_instagramclone.myImageGallery;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

/**
 * Created by Pawel on 01.05.2018.
 */

public class UploadChoosenImage {
    private static final UploadChoosenImage ourInstance = new UploadChoosenImage();

    public static UploadChoosenImage getInstance() {
        return ourInstance;
    }

    private UploadChoosenImage() {
    }

    private int actionType;
    private String description;
    private Bitmap bitmapToUpload;
    private Activity activity;

    OnImageSharedListener onImageSharedListener;

    public void uploadImage(Activity activity, Bitmap bitmapToUpload, String description, OnImageSharedListener onImageSharedListener) {
        this.actionType = 1;
        this.description = description;
        uploadImage(activity, bitmapToUpload, onImageSharedListener);
    }

    public void uploadImage(Activity activity, Bitmap bitmapToUpload, OnImageSharedListener onImageSharedListener) {
        this.onImageSharedListener=onImageSharedListener;
        this.activity = activity;
        if (actionType != 1)
            this.actionType = 2;
        this.bitmapToUpload = bitmapToUpload;

        compressionAndUploadToServer();

        //TODO: Completed uploading
    }

    private Bitmap getResizedBitmap() {

        int width = bitmapToUpload.getWidth();
        int height = bitmapToUpload.getHeight();

        Matrix matrix = new Matrix();

        if (actionType == 1 || actionType == 2) {
            int longer = width >= height ? width : height;
            float scaleWidth = ((float) (actionType == 1 ? 1024 : 100)) / longer;
            matrix.postScale(scaleWidth, scaleWidth);
        }
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmapToUpload, 0, 0, width, height, matrix, false);
        bitmapToUpload.recycle();
        return resizedBitmap;
    }

    private Bitmap getAvatarBitmap() {

        Bitmap bitmap = getResizedBitmap();

        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }
        float radius = 50;

        Bitmap canvasBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        Canvas canvas = new Canvas(canvasBitmap);

        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                radius, paint);

        return canvasBitmap;
    }

    private void compressionAndUploadToServer() {


        Bitmap toCompress = (actionType == 1 ? getResizedBitmap() : getAvatarBitmap());

        //     compression to object file

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        if (toCompress == null) {
            return;
        }
        toCompress.compress(Bitmap.CompressFormat.PNG, 100, stream);
        //  Creating object to sent to server

        byte[] byteArray = stream.toByteArray();

        final ParseFile file = new ParseFile("image.png", byteArray);

        if (actionType == 2) {

            ParseUser.getCurrentUser().put("avatar", file);
            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    if (e == null) {
                        onImageSharedListener.onSuccessful();
                    } else {
                        onImageSharedListener.onFailure();
                    }
                }
            });
        } else if (actionType == 1) {

            ParseObject object = new ParseObject("Image");

            object.put("description", description);
            object.put("image", file);

            object.put("username", ParseUser.getCurrentUser().getUsername());

            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        onImageSharedListener.onSuccessful();
                    } else {
                        onImageSharedListener.onFailure();
                    }
                }
            });
        }
    }

    public interface OnImageSharedListener{
        void onSuccessful();
        void onFailure();
    }
}
