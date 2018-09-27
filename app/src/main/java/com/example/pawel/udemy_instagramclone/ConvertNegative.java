package com.example.pawel.udemy_instagramclone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.wasabeef.glide.transformations.gpu.InvertFilterTransformation;

public class ConvertNegative extends AppCompatActivity {
	
	ImageView positive;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initialization();
		
		getIntentAndStartAction();
	}
	
	
	private void initialization() {
		
		setContentView(R.layout.activity_convert_negative);
		positive = findViewById(R.id.positive);
	}
	
	private void getIntentAndStartAction() {
		
		Intent intent = getIntent();
		
		if (Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null) {
			if ("text/plain".equals(intent.getType())) {
				String address = intent.getStringExtra(Intent.EXTRA_TEXT);
				String finalAddress;
				if (address.contains("9gag")) {
					finalAddress = constructFinalURL(address);
				}
				else {
					finalAddress = address;
				}
				Glide.with(getApplicationContext()).load(finalAddress).apply(RequestOptions.bitmapTransform(new InvertFilterTransformation())).into(positive);
			}
			else if (intent.getType().startsWith("image/")) {
				Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
				
				Glide.with(getApplicationContext()).load(imageUri).apply(RequestOptions.bitmapTransform(new InvertFilterTransformation())).into(positive);
			}
		}
	}
	
	private String constructFinalURL(String address) {
		
		
		String baseImageURL = "https://images-cdn.9gag.com/photo/";
		String endBaseImageURL = "_700b.jpg";
		
		String importantPart = getImportantPart(address);
		
		String finalURLtoDownloadImage = baseImageURL + importantPart + endBaseImageURL;
		
		Log.i("Final URL", finalURLtoDownloadImage);
		
		return finalURLtoDownloadImage;
	}
	
	private String getImportantPart(String address) {
		
		
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

