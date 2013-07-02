package com.slickreader.view;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.slickreader.R;
import com.slickreader.activity.FolderItemsList;
import com.slickreader.database.DatabaseConstants;
import com.slickreader.util.AppConstants;
import com.slickreader.util.ImageLoader;

public class FolderTreeViewBinder implements ViewBinder {

	private int currentState = AppConstants.STATE_SOME;
	private int READING_RETURNED = 0x02;
	private final ImageLoader imageLoader;
	
	public FolderTreeViewBinder(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}

	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		if (TextUtils.equals(cursor.getColumnName(columnIndex), DatabaseConstants.FEED_FAVICON_URL)) {
			if (cursor.getString(columnIndex) != null) {
				String imageUrl = cursor.getString(columnIndex);
				imageLoader.displayImage(imageUrl, (ImageView)view, false);
			} else {
				Bitmap bitmap = BitmapFactory.decodeResource(view.getContext().getResources(), R.drawable.world);
				((ImageView) view).setImageBitmap(bitmap);
			}
			return true;
		} else if (TextUtils.equals(cursor.getColumnName(columnIndex), DatabaseConstants.FEED_POSITIVE_COUNT) || TextUtils.equals(cursor.getColumnName(columnIndex), DatabaseConstants.SUM_POS)) {
			int feedPositive = cursor.getInt(columnIndex);
			if (feedPositive > 0) {
				view.setVisibility(View.VISIBLE);
				((TextView) view).setText("" + feedPositive);
			} else {
				view.setVisibility(View.GONE);
			}
			return true;
		} else if (TextUtils.equals(cursor.getColumnName(columnIndex), DatabaseConstants.FEED_NEUTRAL_COUNT) || TextUtils.equals(cursor.getColumnName(columnIndex), DatabaseConstants.SUM_NEUT)) {
			int feedNeutral = cursor.getInt(columnIndex);
			if (feedNeutral > 0 && currentState != AppConstants.STATE_BEST) {
				view.setVisibility(View.VISIBLE);
				((TextView) view).setText("" + feedNeutral);
			} else {
				view.setVisibility(View.GONE);
			}
			return true;
		} else if (TextUtils.equals(cursor.getColumnName(columnIndex), DatabaseConstants.FOLDER_NAME)) {
			final String folderName = cursor.getString(columnIndex);
			((TextView) view).setText("" + folderName.toUpperCase());
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(v.getContext(), FolderItemsList.class);
					i.putExtra(FolderItemsList.EXTRA_FOLDER_NAME, folderName);
					i.putExtra(FolderItemsList.EXTRA_STATE, currentState);
					((Activity) v.getContext()).startActivityForResult(i, READING_RETURNED);
				}
			});
			return true;
		}

		return false;
	}

	public void setState(int selection) {
		currentState = selection;
	}

}
