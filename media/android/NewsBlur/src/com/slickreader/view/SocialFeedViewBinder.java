package com.slickreader.view;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.slickreader.activity.NewsBlurApplication;
import com.slickreader.database.DatabaseConstants;
import com.slickreader.util.AppConstants;
import com.slickreader.util.ImageLoader;

public class SocialFeedViewBinder implements ViewBinder {

	private int currentState = AppConstants.STATE_SOME;
	private ImageLoader imageLoader;
	private Context context;
	
	public SocialFeedViewBinder(final Context context) {
		this.context = context;
		imageLoader = ((NewsBlurApplication) context.getApplicationContext()).getImageLoader();
	}
	
	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		if (TextUtils.equals(cursor.getColumnName(columnIndex), DatabaseConstants.SOCIAL_FEED_POSITIVE_COUNT)) {
			int feedPositive = cursor.getInt(columnIndex);
			if (feedPositive > 0) {
				view.setVisibility(View.VISIBLE);
				((TextView) view).setText("" + feedPositive);
			} else {
				view.setVisibility(View.GONE);
			}
			return true;
		} else if (TextUtils.equals(cursor.getColumnName(columnIndex), DatabaseConstants.SOCIAL_FEED_NEUTRAL_COUNT)) {
			int feedNeutral = cursor.getInt(columnIndex);
			if (feedNeutral > 0 && currentState != AppConstants.STATE_BEST) {
				view.setVisibility(View.VISIBLE);
				((TextView) view).setText("" + feedNeutral);
			} else {
				view.setVisibility(View.GONE);
			}
			return true;
		} else if (TextUtils.equals(cursor.getColumnName(columnIndex), DatabaseConstants.SOCIAL_FEED_ICON)) {
			String url = cursor.getString(columnIndex);
			imageLoader.displayImage(url, (ImageView) view, false);
			return true;
		} else if (TextUtils.equals(cursor.getColumnName(columnIndex), DatabaseConstants.SOCIAL_FEED_NEGATIVE_COUNT)) {
			int feedNegative = cursor.getInt(columnIndex);
			if (feedNegative > 0 && currentState == AppConstants.STATE_ALL) {
				view.setVisibility(View.VISIBLE);
				((TextView) view).setText("" + feedNegative);
			} else {
				view.setVisibility(View.GONE);
			}
			return true;
		} 

		return false;
	}

	public void setState(int selection) {
		currentState = selection;
	}

}
