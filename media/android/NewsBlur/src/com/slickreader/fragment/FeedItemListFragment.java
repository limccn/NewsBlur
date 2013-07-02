package com.slickreader.fragment;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.slickreader.R;
import com.slickreader.activity.FeedReading;
import com.slickreader.activity.ItemsList;
import com.slickreader.activity.Reading;
import com.slickreader.database.DatabaseConstants;
import com.slickreader.database.FeedItemsAdapter;
import com.slickreader.database.FeedProvider;
import com.slickreader.domain.Feed;
import com.slickreader.domain.Story;
import com.slickreader.util.FeedUtils;
import com.slickreader.util.NetworkUtils;
import com.slickreader.util.StoryOrder;
import com.slickreader.view.FeedItemViewBinder;

public class FeedItemListFragment extends ItemListFragment implements LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener, OnScrollListener, OnCreateContextMenuListener {

	private static final String TAG = "itemListFragment";
	public static final String FRAGMENT_TAG = "itemListFragment";
	private ContentResolver contentResolver;
	private String feedId;
	private FeedItemsAdapter adapter;
	private Uri storiesUri;
	private int currentState;
	private int currentPage = 1;
	private boolean requestedPage = false;
	private boolean doRequest = true;

	public static int ITEMLIST_LOADER = 0x01;
	private int READING_RETURNED = 0x02;
	private Feed feed;
	private Cursor feedCursor;
	
    private StoryOrder storyOrder;

	public static FeedItemListFragment newInstance(String feedId, int currentState, StoryOrder storyOrder) {
		FeedItemListFragment feedItemFragment = new FeedItemListFragment();

		Bundle args = new Bundle();
		args.putInt("currentState", currentState);
		args.putString("feedId", feedId);
		args.putSerializable("storyOrder", storyOrder);
		feedItemFragment.setArguments(args);

		return feedItemFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		currentState = getArguments().getInt("currentState");
		feedId = getArguments().getString("feedId");
		storyOrder = (StoryOrder)getArguments().getSerializable("storyOrder");

		if (!NetworkUtils.isOnline(getActivity())) {
			doRequest = false;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_itemlist, null);
		ListView itemList = (ListView) v.findViewById(R.id.itemlistfragment_list);

		itemList.setEmptyView(v.findViewById(R.id.empty_view));

		contentResolver = getActivity().getContentResolver();
		storiesUri = FeedProvider.FEED_STORIES_URI.buildUpon().appendPath(feedId).build();
		Cursor cursor = contentResolver.query(storiesUri, null, DatabaseConstants.getStorySelectionFromState(currentState), null, DatabaseConstants.getStorySortOrder(storyOrder));

		setupFeed();

		String[] groupFrom = new String[] { DatabaseConstants.STORY_TITLE, DatabaseConstants.STORY_AUTHORS, DatabaseConstants.STORY_READ, DatabaseConstants.STORY_SHORTDATE, DatabaseConstants.STORY_INTELLIGENCE_AUTHORS };
		int[] groupTo = new int[] { R.id.row_item_title, R.id.row_item_author, R.id.row_item_title, R.id.row_item_date, R.id.row_item_sidebar };

		getLoaderManager().initLoader(ITEMLIST_LOADER , null, this);

		adapter = new FeedItemsAdapter(getActivity(), feed, R.layout.row_item, cursor, groupFrom, groupTo, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		itemList.setOnScrollListener(this);

		adapter.setViewBinder(new FeedItemViewBinder(getActivity()));
		itemList.setAdapter(adapter);
		itemList.setOnItemClickListener(this);
		itemList.setOnCreateContextMenuListener(this);
		
		return v;
	}

	private void setupFeed() {
		Uri feedUri = FeedProvider.FEEDS_URI.buildUpon().appendPath(feedId).build();
		feedCursor = contentResolver.query(feedUri, null, null, null, null);
		feedCursor.moveToFirst();
		feed = Feed.fromCursor(feedCursor);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
		Uri uri = FeedProvider.FEED_STORIES_URI.buildUpon().appendPath(feedId).build();
		CursorLoader cursorLoader = new CursorLoader(getActivity(), uri, null, DatabaseConstants.getStorySelectionFromState(currentState), null, DatabaseConstants.getStorySortOrder(storyOrder));
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (cursor != null) {
			adapter.swapCursor(cursor);
		}
	}

	public void hasUpdated() {
		setupFeed();
		getLoaderManager().restartLoader(ITEMLIST_LOADER , null, this);
		requestedPage = false;
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.notifyDataSetInvalidated();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent i = new Intent(getActivity(), FeedReading.class);
		i.putExtra(Reading.EXTRA_FEED, feedId);
		i.putExtra(FeedReading.EXTRA_POSITION, position);
		i.putExtra(ItemsList.EXTRA_STATE, currentState);
		startActivityForResult(i, READING_RETURNED );
	}

	public void changeState(int state) {
		currentState = state;
		refreshStories();
	}

	private void refreshStories() {
		final String selection = DatabaseConstants.getStorySelectionFromState(currentState);
		Cursor cursor = contentResolver.query(storiesUri, null, selection, null, DatabaseConstants.getStorySortOrder(storyOrder));
		adapter.swapCursor(cursor);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisible, int visibleCount, int totalCount) {
		if (firstVisible + visibleCount == totalCount && !requestedPage) {
			currentPage += 1;
			requestedPage = true;
			((ItemsList) getActivity()).triggerRefresh(currentPage);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) { }


	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		if (item.getItemId() == R.id.menu_mark_story_as_read) {
			final Story story = adapter.getStory(menuInfo.position);
			if(! story.read) {
				ArrayList<Story> storiesToMarkAsRead = new ArrayList<Story>();
				storiesToMarkAsRead.add(story);
				FeedUtils.markStoriesAsRead(storiesToMarkAsRead, getActivity());
                refreshStories();
			}
		} else if (item.getItemId() == R.id.menu_mark_previous_stories_as_read) {
			final ArrayList<Story> previousStories = adapter.getPreviousStories(menuInfo.position);
			ArrayList<Story> storiesToMarkAsRead = new ArrayList<Story>();
			for(Story story: previousStories) {
				if(! story.read) {
					storiesToMarkAsRead.add(story);
				}
			}
			FeedUtils.markStoriesAsRead(storiesToMarkAsRead, getActivity());
            refreshStories();
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		MenuInflater inflater = getActivity().getMenuInflater();
		
		inflater.inflate(R.menu.context_story, menu);
	}

    @Override
    public void setStoryOrder(StoryOrder storyOrder) {
        this.storyOrder = storyOrder;
    }

}
