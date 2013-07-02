package com.slickreader.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.slickreader.database.DatabaseConstants;
import com.slickreader.database.FeedProvider;
import com.slickreader.database.FeedReadingAdapter;
import com.slickreader.domain.Classifier;
import com.slickreader.domain.Feed;
import com.slickreader.fragment.SyncUpdateFragment;
import com.slickreader.service.SyncService;
import com.slickreader.util.PrefsUtils;
import com.slickreader.util.StoryOrder;

public class FeedReading extends Reading {

	String feedId;
	private Feed feed;
	private int currentPage;
	private boolean stopLoading = false;
	private boolean requestedPage = false;

	@Override
	protected void onCreate(Bundle savedInstanceBundle) {
		super.onCreate(savedInstanceBundle);

		setResult(RESULT_OK);
		feedId = getIntent().getStringExtra(Reading.EXTRA_FEED);

		Uri classifierUri = FeedProvider.CLASSIFIER_URI.buildUpon().appendPath(feedId).build();
		Cursor feedClassifierCursor = contentResolver.query(classifierUri, null, null, null, null);
		Classifier classifier = Classifier.fromCursor(feedClassifierCursor);

		Uri storiesURI = FeedProvider.FEED_STORIES_URI.buildUpon().appendPath(feedId).build();
		StoryOrder storyOrder = PrefsUtils.getStoryOrderForFeed(this, feedId);
		stories = contentResolver.query(storiesURI, null, DatabaseConstants.getStorySelectionFromState(currentState), null, DatabaseConstants.getStorySortOrder(storyOrder));

		final Uri feedUri = FeedProvider.FEEDS_URI.buildUpon().appendPath(feedId).build();
		Cursor feedCursor = contentResolver.query(feedUri, null, null, null, null);

		feedCursor.moveToFirst();
		feed = Feed.fromCursor(feedCursor);
		setTitle(feed.title);

		readingAdapter = new FeedReadingAdapter(getSupportFragmentManager(), feed, stories, classifier);

		setupPager();

		syncFragment = (SyncUpdateFragment) fragmentManager.findFragmentByTag(SyncUpdateFragment.TAG);
		if (syncFragment == null) {
			syncFragment = new SyncUpdateFragment();
			fragmentManager.beginTransaction().add(syncFragment, SyncUpdateFragment.TAG).commit();
		}

		addStoryToMarkAsRead(readingAdapter.getStory(passedPosition));
	}

	@Override
	public void onPageSelected(int position) {
		super.onPageSelected(position);
		if (readingAdapter.getStory(position) != null) {
			addStoryToMarkAsRead(readingAdapter.getStory(position));
			checkStoryCount(position);
		}
	}
	
	@Override
	public void updateAfterSync() {
		setSupportProgressBarIndeterminateVisibility(false);
		stories.requery();
		requestedPage = false;
		readingAdapter.notifyDataSetChanged();
		checkStoryCount(pager.getCurrentItem());
	}

	@Override
	public void checkStoryCount(int position) {
		if (position == stories.getCount() - 1 && !stopLoading && !requestedPage) {
			requestedPage = true;
			currentPage += 1;
			triggerRefresh(currentPage);
		}
	}

	@Override
	public void triggerRefresh() {
		triggerRefresh(1);
	}

	@Override
	public void triggerRefresh(int page) {
		if (!stopLoading) {
			setSupportProgressBarIndeterminateVisibility(true);
			final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SyncService.class);
			intent.putExtra(SyncService.EXTRA_STATUS_RECEIVER, syncFragment.receiver);
			intent.putExtra(SyncService.SYNCSERVICE_TASK, SyncService.EXTRA_TASK_FEED_UPDATE);
			intent.putExtra(SyncService.EXTRA_TASK_FEED_ID, feedId);
			if (page > 1) {
				intent.putExtra(SyncService.EXTRA_TASK_PAGE_NUMBER, Integer.toString(page));
			}
            intent.putExtra(SyncService.EXTRA_TASK_ORDER, PrefsUtils.getStoryOrderForFeed(this, feedId));
            intent.putExtra(SyncService.EXTRA_TASK_READ_FILTER, PrefsUtils.getReadFilterForFeed(this, feedId));
			startService(intent);
		}
	}

	@Override
	public void setNothingMoreToUpdate() {
		stopLoading = true;
	}

	@Override
	public void closeAfterUpdate() { }


}
