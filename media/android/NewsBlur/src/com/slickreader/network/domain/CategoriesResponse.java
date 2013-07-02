package com.slickreader.network.domain;

import java.util.HashMap;

import com.google.gson.annotations.SerializedName;
import com.slickreader.domain.Category;
import com.slickreader.domain.Feed;

public class CategoriesResponse {
	
	@SerializedName("feeds")
	public HashMap<String, Feed> feeds;

	
	@SerializedName("categories")
	public Category[] categories;
	
}
