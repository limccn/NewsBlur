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
import com.slickreader.activity.FolderReading;
import com.slickreader.activity.ItemsList;
import com.slickreader.database.DatabaseConstants;
import com.slickreader.database.FeedProvider;
import com.slickreader.database.MultipleFeedItemsAdapter;
import com.slickreader.domain.Folder;
import com.slickreader.util.NetworkUtils;
import com.slickreader.util.StoryOrder;
import com.slickreader.view.FeedItemViewBinder;

public class FolderItemListFragment extends ItemListFragment implements LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener, OnScrollListener {

	private static final String TAG = "itemListFragment";
	public static final String FRAGMENT_TAG = "itemListFragment";
	private ContentResolver contentResolver;
	private String[] feedIds;
	private SimpleCursorAdapter adapter;
	private Uri storiesUri;
	private int currentState;
	private int currentPage = 1;
	private String folderName;
	private boolean requestedPage = false;
	private boolean doRequest = true;
	private Folder folder;
	
    private StoryOrder storyOrder;

	public static int ITEMLIST_LOADER = 0x01;


	public static FolderItemListFragment newInstance(ArrayList<String> feedIds, String folderName, int currentState, StoryOrder storyOrder) {
		FolderItemListFragment feedItemFragment = new FolderItemListFragment();

		Bundle args = new Bundle();
		args.putInt("currentState", currentState);
		args.putStringArrayList("feedIds", feedIds);
		args.putString("folderName", folderName);
		args.putSerializable("storyOrder", storyOrder);
		feedItemFragment.setArguments(args);

		return feedItemFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		currentState = getArguments().getInt("currentState");
		folderName = getArguments().getString("folderName");
		storyOrder = (StoryOrder)getArguments().getSerializable("storyOrder");
		ArrayList<String> feedIdArrayList = getArguments().getStringArrayList("feedIds");
		feedIds = new String[feedIdArrayList.size()];
		feedIdArrayList.toArray(feedIds);

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
		storiesUri = FeedProvider.MULTIFEED_STORIES_URI;

		Cursor cursor = contentResolver.query(storiesUri, null, DatabaseConstants.getStorySelectionFromState(currentState), feedIds, DatabaseConstants.getStorySortOrder(storyOrder));
		getActivity().startManagingCursor(cursor);

		String[] groupFrom = new String[] { DatabaseConstants.STORY_TITLE, DatabaseConstants.FEED_TITLE, DatabaseConstants.STORY_READ, DatabaseConstants.STORY_SHORTDATE, DatabaseConstants.STORY_INTELLIGENCE_AUTHORS, DatabaseConstants.STORY_AUTHORS };
		int[] groupTo = new int[] { R.id.row_item_title, R.id.row_item_feedtitle, R.id.row_item_title, R.id.row_item_date, R.id.row_item_sidebar, R.id.row_item_author };

		getLoaderManager().initLoader(ITEMLIST_LOADER , null, this);

		adapter = new MultipleFeedItemsAdapter(getActivity(), R.layout.row_folderitem, cursor, groupFrom, groupTo, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		itemList.setOnScrollListener(this);

		adapter.setViewBinder(new FeedItemViewBinder(getActivity()));
		itemList.setAdapter(adapter);
		itemList.setOnItemClickListener(this);

		return v;
	}


	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
		Uri uri = FeedProvider.MULTIFEED_STORIES_URI;
		CursorLoader cursorLoader = new CursorLoader(getActivity(), uri, null, DatabaseConstants.getStorySelectionFromState(currentState), feedIds, DatabaseConstants.getStorySortOrder(storyOrder));
		return cursorLoader;
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
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent i = new Intent(getActivity(), FolderReading.class);
		i.putExtra(FeedReading.EXTRA_FEED_IDS, feedIds);
		i.putExtra(FeedReading.EXTRA_POSITION, position);
		i.putExtra(FeedReading.EXTRA_FOLDERNAME, folderName);
		i.putExtra(ItemsList.EXTRA_STATE, currentState);
		startActivityForResult(i, READING_RETURNED );
	}

	public void changeState(int state) {
		currentState = state;
		final String selection = DatabaseConstants.getStorySelectionFromState(state);
		Cursor cursor = contentResolver.query(storiesUri, null, selection, feedIds, DatabaseConstants.getStorySortOrder(storyOrder));
		getActivity().startManagingCursor(cursor);
		adapter.swapCursor(cursor);
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
	public void onScrollStateChanged(AbsListView view, int scrollState) { }
	
	@Override
    public void setStoryOrder(StoryOrder storyOrder) {
        this.storyOrder = storyOrder;
    }
}
