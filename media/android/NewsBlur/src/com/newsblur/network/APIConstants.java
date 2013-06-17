package com.newsblur.network;

public class APIConstants {

    // TODO: make use of trailing slashes on URLs consistent or document why
    // they are not.

	public static final String URL_LOGIN = "https://slickreader.com//api/login";
	public static final String URL_FEEDS = "https://slickreader.com//reader/feeds/";
	public static final String URL_USER_PROFILE = "https://slickreader.com//social/profile";
	public static final String URL_MY_PROFILE = "https://slickreader.com//social/load_user_profile";
	public static final String URL_FOLLOW = "https://slickreader.com//social/follow";
	public static final String URL_UNFOLLOW = "https://slickreader.com//social/unfollow";
	
	public static final String URL_USER_INTERACTIONS = "https://slickreader.com//social/interactions";
	public static final String URL_RIVER_STORIES = "https://slickreader.com//reader/river_stories";
	public static final String URL_SHARED_RIVER_STORIES = "https://slickreader.com//social/river_stories";
	
	public static final String URL_FEED_STORIES = "https://slickreader.com//reader/feed";
	public static final String URL_SOCIALFEED_STORIES = "https://slickreader.com//social/stories";
	public static final String URL_SIGNUP = "https://slickreader.com//api/signup";
	public static final String URL_FEED_COUNTS = "https://slickreader.com//reader/refresh_feeds/";
	public static final String URL_MARK_FEED_AS_READ = "https://slickreader.com//reader/mark_feed_as_read/";
	public static final String URL_MARK_ALL_AS_READ = "https://slickreader.com//reader/mark_all_as_read/";
	public static final String URL_MARK_STORY_AS_READ = "https://slickreader.com//reader/mark_story_as_read/";
	public static final String URL_MARK_FEED_STORIES_AS_READ = "https://slickreader.com//reader/mark_feed_stories_as_read/";
	public static final String URL_MARK_SOCIALSTORY_AS_READ = "https://slickreader.com//reader/mark_social_stories_as_read/";
	public static final String URL_SHARE_STORY = "https://slickreader.com//social/share_story";
    public static final String URL_MARK_STORY_AS_STARRED = "https://slickreader.com//reader/mark_story_as_starred/";
    public static final String URL_STARRED_STORIES = "https://slickreader.com//reader/starred_stories";
	
	public static final String URL_FEED_AUTOCOMPLETE = "https://slickreader.com//rss_feeds/feed_autocomplete";
	
	public static final String URL_LIKE_COMMENT = "https://slickreader.com//social/like_comment";
	public static final String URL_UNLIKE_COMMENT = "https://slickreader.com//social/remove_like_comment";
	public static final String URL_REPLY_TO = "https://slickreader.com//social/save_comment_reply";
	public static final String URL_ADD_FEED = "https://slickreader.com//reader/add_url";
	public static final String URL_DELETE_FEED = "https://slickreader.com//reader/delete_feed";
	
	public static final String URL_CLASSIFIER_SAVE = "https://slickreader.com//classifier/save";
	
	public static final String PARAMETER_FEEDS = "feeds";
	public static final String PARAMETER_PASSWORD = "password";
	public static final String PARAMETER_USER_ID = "user_id";
	public static final String PARAMETER_USERNAME = "username";
	public static final String PARAMETER_EMAIL = "email";
	public static final String PARAMETER_USERID = "user_id";
	public static final String PARAMETER_STORYID = "story_id";
	public static final String PARAMETER_FEEDS_STORIES = "feeds_stories";
	public static final String PARAMETER_FEED_SEARCH_TERM = "term";
	public static final String PARAMETER_FOLDER = "folder";
	public static final String PARAMETER_IN_FOLDER = "in_folder";
	public static final String PARAMETER_COMMENT_USERID = "comment_user_id";
	public static final String PARAMETER_FEEDID = "feed_id";
	public static final String PARAMETER_REPLY_TEXT = "reply_comments";
	public static final String PARAMETER_STORY_FEEDID = "story_feed_id";
	public static final String PARAMETER_SHARE_COMMENT = "comments";
	public static final String PARAMETER_SHARE_SOURCEID = "source_user_id";
	public static final String PARAMETER_MARKSOCIAL_JSON = "users_feeds_stories";
	public static final String PARAMETER_URL = "url";
	public static final String PARAMETER_DAYS = "days";
	public static final String PARAMETER_UPDATE_COUNTS = "update_counts";
	
	public static final String PARAMETER_PAGE_NUMBER = "page";
	public static final String PARAMETER_ORDER = "order";
	public static final String PARAMETER_READ_FILTER = "read_filter";
	
	public static final String NEWSBLUR_URL = "https://slickreader.com/";
	public static final String URL_CATEGORIES = "https://slickreader.com//categories/" ;
	public static final String PARAMETER_CATEGORY = "category";
	public static final String URL_ADD_CATEGORIES = "https://slickreader.com//categories/subscribe";
	public static final String URL_AUTOFOLLOW_PREF = "https://slickreader.com//profile/set_preference";
	
}
