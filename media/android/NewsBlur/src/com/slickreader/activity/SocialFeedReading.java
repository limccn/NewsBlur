package com.slickreader.activity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.slickreader.database.DatabaseConstants;
import com.slickreader.database.FeedProvider;
import com.slickreader.database.MixedFeedsReadingAdapter;
import com.slickreader.domain.SocialFeed;
import com.slickreader.domain.Story;
import com.slickreader.service.SyncService;

public class SocialFeedReading extends Reading {

	private String userId;
	private String username;
	private SocialFeed socialFeed;
	private boolean requestedPage;
	private boolean stopLoading = false;
	private int currentPage;

	@Override
	protected void onCreate(Bundle savedInstanceBundle) {
		super.onCreate(savedInstanceBundle);

		setResult(RESULT_OK);

		userId = getIntent().getStringExtra(Reading.EXTRA_USERID);
		username = getIntent().getStringExtra(Reading.EXTRA_USERNAME);

		Uri socialFeedUri = FeedProvider.SOCIAL_FEEDS_URI.buildUpon().appendPath(userId).build();
		socialFeed = SocialFeed.fromCursor(contentResolver.query(socialFeedUri, null, null, null, null));

		Uri storiesURI = FeedProvider.SOCIALFEED_STORIES_URI.buildUpon().appendPath(userId).build();
		stories = contentResolver.query(storiesURI, null, DatabaseConstants.getStorySelectionFromState(currentState), null, null);
		setTitle(getIntent().getStringExtra(EXTRA_USERNAME));

		readingAdapter = new MixedFeedsReadingAdapter(getSupportFragmentManager(), getContentResolver(), stories);

		setupPager();

		addStoryToMarkAsRead(readingAdapter.getStory(passedPosition));
	}

	@Override
	public void onPageSelected(int position) {
		super.onPageSelected(position);
        addStoryToMarkAsRead(readingAdapter.getStory(position));
		checkStoryCount(position);
	}

	@Override
	public void triggerRefresh() {
		triggerRefresh(0);
	}

	@Override
	public void triggerRefresh(int page) {
		setSupportProgressBarIndeterminateVisibility(true);
		final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SyncService.class);
		intent.putExtra(SyncService.EXTRA_STATUS_RECEIVER, syncFragment.receiver);
		intent.putExtra(SyncService.SYNCSERVICE_TASK, SyncService.EXTRA_TASK_SOCIALFEED_UPDATE);
		intent.putExtra(SyncService.EXTRA_TASK_SOCIALFEED_ID, userId);
		if (page > 1) {
			intent.putExtra(SyncService.EXTRA_TASK_PAGE_NUMBER, Integer.toString(page));
		}
		intent.putExtra(SyncService.EXTRA_TASK_SOCIALFEED_USERNAME, username);
		startService(intent);
	}

	@Override
	public void checkStoryCount(int position) {
		if (position == stories.getCount() - 1 && !requestedPage && !stopLoading) {
			currentPage += 1;
			requestedPage = true;
			triggerRefresh(currentPage);
		}
	}

	@Override
	public void setNothingMoreToUpdate() {
		stopLoading = true;
	}

	@Override
	public void closeAfterUpdate() { }

}
