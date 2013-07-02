package com.slickreader.database;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.slickreader.activity.ReadingAdapter;
import com.slickreader.domain.Classifier;
import com.slickreader.domain.Feed;
import com.slickreader.domain.Story;
import com.slickreader.fragment.LoadingFragment;
import com.slickreader.fragment.ReadingItemFragment;

public class FeedReadingAdapter extends ReadingAdapter {

	private final Feed feed;
	private Classifier classifier;

	public FeedReadingAdapter(FragmentManager fm, Feed feed, Cursor stories, Classifier classifier) {
		super(fm, stories);
		this.feed = feed;
		this.classifier = classifier;
	}

	@Override
	public Fragment getItem(int position)  {
		if (stories == null || stories.getCount() == 0) {
			loadingFragment = new LoadingFragment();
			return loadingFragment;
		} else {
			stories.moveToPosition(position);
			return ReadingItemFragment.newInstance(Story.fromCursor(stories), feed.title, feed.faviconColour, feed.faviconFade, feed.faviconUrl, classifier, false);
		}
	}
	
	

}
