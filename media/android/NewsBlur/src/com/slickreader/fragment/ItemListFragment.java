package com.slickreader.fragment;

import com.slickreader.util.StoryOrder;

import android.support.v4.app.Fragment;

public abstract class ItemListFragment extends Fragment {
	
	protected int READING_RETURNED = 0x02;
	
	public abstract void hasUpdated();
	public abstract void changeState(final int state);
	public abstract void setStoryOrder(StoryOrder storyOrder);

}
