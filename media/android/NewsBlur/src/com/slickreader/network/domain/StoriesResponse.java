package com.slickreader.network.domain;

import com.google.gson.annotations.SerializedName;
import com.slickreader.domain.Classifier;
import com.slickreader.domain.Story;
import com.slickreader.domain.UserProfile;

public class StoriesResponse {
	
	@SerializedName("stories")
	public Story[] stories;
	
	@SerializedName("user_profiles")
	public UserProfile[] users;
	
	public Classifier classifiers;
	
	public boolean authenticated;

}
