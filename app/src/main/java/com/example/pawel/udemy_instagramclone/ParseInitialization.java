package com.example.pawel.udemy_instagramclone;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;

public class ParseInitialization extends Application {
  
  @Override
	public void onCreate() {
		super.onCreate();
		parseInitialization();
	}

	private void parseInitialization() {

		Parse.enableLocalDatastore(getApplicationContext());

		// Add your initialization code here
		Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
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
}
