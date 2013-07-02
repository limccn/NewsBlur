package com.slickreader.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.slickreader.R;
import com.slickreader.activity.FeedReading;
import com.slickreader.activity.ItemsList;
import com.slickreader.activity.SavedStoriesReading;
import com.slickreader.database.DatabaseConstants;
import com.slickreader.database.FeedProvider;
import com.slickreader.database.MultipleFeedItemsAdapter;
import com.slickreader.util.NetworkUtils;
import com.slickreader.util.StoryOrder;
import com.slickreader.view.SocialItemViewBinder;

public class SavedStoriesItemListFragment extends ItemListFragment implements LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener, OnScrollListener {

	private boolean doRequest = true;
	private ContentResolver contentResolver;
	private SimpleCursorAdapter adapter;
	private boolean requestedPage;
	private int currentPage = 0;
	
	public static int ITEMLIST_LOADER = 0x01;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!NetworkUtils.isOnline(getActivity())) {
			doRequest  = false;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_itemlist, null);
		ListView itemList = (ListView) v.findViewById(R.id.itemlistfragment_list);

		itemList.setEmptyView(v.findViewById(R.id.empty_view));

		contentResolver = getActivity().getContentResolver();
		Cursor cursor = contentResolver.query(FeedProvider.STARRED_STORIES_URI, null, null, null, null);
		
		String[] groupFrom = new String[] { DatabaseConstants.STORY_TITLE, DatabaseConstants.STORY_AUTHORS, DatabaseConstants.STORY_TITLE, DatabaseConstants.STORY_SHORTDATE, DatabaseConstants.STORY_INTELLIGENCE_AUTHORS, DatabaseConstants.FEED_TITLE };
		int[] groupTo = new int[] { R.id.row_item_title, R.id.row_item_author, R.id.row_item_title, R.id.row_item_date, R.id.row_item_sidebar, R.id.row_item_feedtitle };

		getLoaderManager().initLoader(ITEMLIST_LOADER , null, this);

		adapter = new MultipleFeedItemsAdapter(getActivity(), R.layout.row_socialitem, cursor, groupFrom, groupTo, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, true);

		itemList.setOnScrollListener(this);

		adapter.setViewBinder(new SocialItemViewBinder(getActivity(), true));
		itemList.setAdapter(adapter);
		itemList.setOnItemClickListener(this);

		return v;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (cursor != null) {
			adapter.swapCursor(cursor);
		}
	}

	public void hasUpdated() {
		getLoaderManager().restartLoader(ITEMLIST_LOADER , null, this);
		requestedPage = false;
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.notifyDataSetInvalidated();
	}

	@Override
	public void changeState(int state) {
        ; // This fragment ignores state
	}

	public static ItemListFragment newInstance() {
		ItemListFragment fragment = new SavedStoriesItemListFragment();
		return fragment;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisible, int visibleCount, int totalCount) {
		if (firstVisible + visibleCount == totalCount && !requestedPage && doRequest) {
			currentPage += 1;
			requestedPage = true;
			((ItemsList) getActivity()).triggerRefresh(currentPage);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) { }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent i = new Intent(getActivity(), SavedStoriesReading.class);
		i.putExtra(FeedReading.EXTRA_POSITION, position);
		startActivityForResult(i, READING_RETURNED );
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		CursorLoader cursorLoader = new CursorLoader(getActivity(), FeedProvider.STARRED_STORIES_URI, null, null, null, null);
		return cursorLoader;
	}

	@Override
    public void setStoryOrder(StoryOrder storyOrder) {
        ; // This fragment ignores story order
    }
}
