package com.slickreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.slickreader.R;
import com.slickreader.fragment.FeedItemListFragment;
import com.slickreader.fragment.SocialFeedItemListFragment;
import com.slickreader.fragment.SyncUpdateFragment;
import com.slickreader.network.APIManager;
import com.slickreader.network.MarkSocialFeedAsReadTask;
import com.slickreader.service.SyncService;
import com.slickreader.util.PrefConstants;
import com.slickreader.util.PrefsUtils;
import com.slickreader.util.ReadFilter;
import com.slickreader.util.StoryOrder;

public class SocialFeedItemsList extends ItemsList {

	private String userIcon, userId, username, title;
	private APIManager apiManager;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		apiManager = new APIManager(this);
		
		username = getIntent().getStringExtra(EXTRA_BLURBLOG_USERNAME);
		userIcon = getIntent().getStringExtra(EXTRA_BLURBLOG_USER_ICON );
		userId = getIntent().getStringExtra(EXTRA_BLURBLOG_USERID);
		title = getIntent().getStringExtra(EXTRA_BLURBLOG_TITLE);
				
		setTitle(title);
		
		if (itemListFragment == null) {
			itemListFragment = SocialFeedItemListFragment.newInstance(userId, username, currentState, getStoryOrder());
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
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.itemslist, menu);
		return true;
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
			intent.putExtra(SyncService.SYNCSERVICE_TASK, SyncService.EXTRA_TASK_SOCIALFEED_UPDATE);
			intent.putExtra(SyncService.EXTRA_TASK_SOCIALFEED_ID, userId);
			intent.putExtra(SyncService.EXTRA_TASK_PAGE_NUMBER, Integer.toString(page));
			intent.putExtra(SyncService.EXTRA_TASK_SOCIALFEED_USERNAME, username);
			startService(intent);
		}
	}

	@Override
	public void markItemListAsRead() {
		new MarkSocialFeedAsReadTask(apiManager, getContentResolver()){
			@Override
			protected void onPostExecute(Boolean result) {
				if (result.booleanValue()) {
					setResult(RESULT_OK);
					Toast.makeText(SocialFeedItemsList.this, R.string.toast_marked_socialfeed_as_read, Toast.LENGTH_SHORT).show();
					finish();
				} else {
					Toast.makeText(SocialFeedItemsList.this, R.string.toast_error_marking_feed_as_read, Toast.LENGTH_LONG).show();
				}
			}
		}.execute(userId);
	}

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
