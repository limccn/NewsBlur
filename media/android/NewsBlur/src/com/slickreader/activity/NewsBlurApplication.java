package com.slickreader.activity;

import android.app.Application;

import com.slickreader.util.ImageLoader;

public class NewsBlurApplication extends Application {

	ImageLoader imageLoader;
	
	@Override
	public void onCreate() {
		super.onCreate();
		imageLoader = new ImageLoader(this);
	}

	public ImageLoader getImageLoader() {
		return imageLoader;
	}
	
}
