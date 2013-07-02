package com.slickreader.network.domain;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;
import com.slickreader.domain.Feed;
import com.slickreader.domain.Story;
import com.slickreader.domain.UserProfile;

public class SocialFeedResponse implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@SerializedName("stories")
	public Story[] stories;
	
	@SerializedName("feeds")
	public Feed[] feeds;
	
	@SerializedName("user_profiles")
	public UserProfile[] userProfiles;
	
	public boolean authenticated;

}