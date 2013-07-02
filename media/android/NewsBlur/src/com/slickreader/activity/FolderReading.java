package com.slickreader.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.slickreader.database.DatabaseConstants;
import com.slickreader.database.FeedProvider;
import com.slickreader.database.MixedFeedsReadingAdapter;
import com.slickreader.service.SyncService;
import com.slickreader.util.PrefsUtils;

public class FolderReading extends Reading {

	private String[] feedIds;
	private String folderName;
	private boolean stopLoading = false;
	private boolean requestedPage;
	private int currentPage;

	@Override
	protected void onCreate(Bundle savedInstanceBundle) {
		super.onCreate(savedInstanceBundle);

		setResult(RESULT_OK);

		feedIds = getIntent().getStringArrayExtra(Reading.EXTRA_FEED_IDS);
		folderName = getIntent().getStringExtra(Reading.EXTRA_FOLDERNAME);
		setTitle(folderName);		

		Uri storiesURI = FeedProvider.MULTIFEED_STORIES_URI;
		stories = contentResolver.query(storiesURI, null, DatabaseConstants.getStorySelectionFromState(currentState), feedIds, null);

		readingAdapter = new MixedFeedsReadingAdapter(getSupportFragmentManager(), getContentResolver(), stories);
		setupPager();

		addStoryToMarkAsRead(readingAdapter.getStory(passedPosition));
	}

	@Override
	public void onPageSelected(int position) {
		addStoryToMarkAsRead(readingAdapter.getStory(position));
		checkStoryCount(position);
		super.onPageSelected(position);
	}

	@Override
	public void triggerRefresh() {
		triggerRefresh(1);
	}

	@Override
	public void triggerRefresh(int page) {
		setSupportProgressBarIndeterminateVisibility(true);
		final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SyncService.class);
		intent.putExtra(SyncService.EXTRA_STATUS_RECEIVER, syncFragment.receiver);
		intent.putExtra(SyncService.SYNCSERVICE_TASK, SyncService.EXTRA_TASK_MULTIFEED_UPDATE);
		intent.putExtra(SyncService.EXTRA_TASK_MULTIFEED_IDS, feedIds);
		
		if (page > 1) {
			intent.putExtra(SyncService.EXTRA_TASK_PAGE_NUMBER, Integer.toString(page));
		}
        intent.putExtra(SyncService.EXTRA_TASK_ORDER, PrefsUtils.getStoryOrderForFolder(this, folderName));
        intent.putExtra(SyncService.EXTRA_TASK_READ_FILTER, PrefsUtils.getReadFilterForFolder(this, folderName));
        
		startService(intent);
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
