package com.slickreader.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.slickreader.R;
import com.slickreader.database.DatabaseConstants;
import com.slickreader.database.FeedProvider;
import com.slickreader.fragment.AllSharedStoriesItemListFragment;
import com.slickreader.fragment.FeedItemListFragment;
import com.slickreader.fragment.SyncUpdateFragment;
import com.slickreader.service.SyncService;
import com.slickreader.util.PrefConstants;
import com.slickreader.util.PrefsUtils;
import com.slickreader.util.ReadFilter;
import com.slickreader.util.StoryOrder;

public class AllSharedStoriesItemsList extends ItemsList {

	private ArrayList<String> feedIds;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setTitle(getResources().getString(R.string.all_shared_stories));

		feedIds = new ArrayList<String>();

		Cursor cursor = getContentResolver().query(FeedProvider.SOCIAL_FEEDS_URI, null, null, null, null);
		while (cursor.moveToNext()) {
			feedIds.add(cursor.getString(cursor.getColumnIndex(DatabaseConstants.SOCIAL_FEED_ID)));
		}

		itemListFragment = (AllSharedStoriesItemListFragment) fragmentManager.findFragmentByTag(FeedItemListFragment.FRAGMENT_TAG);
		if (itemListFragment == null) {
			itemListFragment = AllSharedStoriesItemListFragment.newInstance(currentState, getStoryOrder());
			itemListFragment.setRetainInstance(true);
			FragmentTransaction listTransaction = fragmentManager.beginTransaction();
			listTransaction.add(R.id.activity_itemlist_container, itemListFragment, FeedItemListFragment.FRAGMENT_TAG);
			listTransaction.commit();
		}

		syncFragment = (SyncUpdateFragment) fragmentManager.findFragmentByTag(SyncUpdateFragment.TAG);
		if (syncFragment == null) {
			syncFragment = new SyncUpdateFragment();
			fragmentManager.beginTransaction().add(syncFragment, SyncUpdateFragment.TAG).commit();
			triggerRefresh();
		}
		cursor.close();
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
			intent.putExtra(SyncService.SYNCSERVICE_TASK, SyncService.EXTRA_TASK_MULTISOCIALFEED_UPDATE);

			String[] feeds = new String[feedIds.size()];
			feedIds.toArray(feeds);
			intent.putExtra(SyncService.EXTRA_TASK_MULTIFEED_IDS, feeds);
			intent.putExtra(SyncService.EXTRA_TASK_PAGE_NUMBER, Integer.toString(page));

			startService(intent);
		}
	}

	// We don't allow All Shared Stories to be marked as read
	@Override
	public void markItemListAsRead() { }

	@Override
	public void closeAfterUpdate() { }


    @Override
    protected StoryOrder getStoryOrder() {
        return PrefsUtils.getStoryOrderForFolder(this, PrefConstants.ALL_SHARED_STORIES_FOLDER_NAME);
    }

    @Override
    public void updateStoryOrderPreference(StoryOrder newValue) {
        PrefsUtils.setStoryOrderForFolder(this, PrefConstants.ALL_SHARED_STORIES_FOLDER_NAME, newValue);
    }

    @Override
    protected void updateReadFilterPreference(ReadFilter newValue) {
        PrefsUtils.setReadFilterForFolder(this, PrefConstants.ALL_SHARED_STORIES_FOLDER_NAME, newValue);
    }
    
    @Override
    protected ReadFilter getReadFilter() {
        return PrefsUtils.getReadFilterForFolder(this, PrefConstants.ALL_SHARED_STORIES_FOLDER_NAME);
    }
}
