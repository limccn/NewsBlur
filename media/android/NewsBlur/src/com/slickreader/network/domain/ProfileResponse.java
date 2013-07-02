package com.slickreader.network.domain;

import com.google.gson.annotations.SerializedName;
import com.slickreader.domain.UserDetails;

public class ProfileResponse {
	
	@SerializedName("user_profile")
	public UserDetails user;
	
	@SerializedName("activities")
	public ActivitiesResponse[] activities;
	
}
