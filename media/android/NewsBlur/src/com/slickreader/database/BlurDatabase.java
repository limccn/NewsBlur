package com.slickreader.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.util.Log;

public class BlurDatabase extends SQLiteOpenHelper {

	private final String TEXT = " text";
	private final String INTEGER = " integer";
	public final static String DB_NAME = "blur.db";
	private final static int VERSION = 1;

	public BlurDatabase(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	private final String FOLDER_SQL = "CREATE TABLE " + DatabaseConstants.FOLDER_TABLE + " (" +
		DatabaseConstants.FOLDER_ID + INTEGER + " PRIMARY KEY AUTOINCREMENT, " +
		DatabaseConstants.FOLDER_NAME + TEXT + " UNIQUE " +  
		")";

	private final String FEED_SQL = "CREATE TABLE " + DatabaseConstants.FEED_TABLE + " (" +
		DatabaseConstants.FEED_ID + INTEGER + " PRIMARY KEY, " +
		DatabaseConstants.FEED_ACTIVE + TEXT + ", " +
		DatabaseConstants.FEED_ADDRESS + TEXT + ", " + 
		DatabaseConstants.FEED_FAVICON_COLOUR + TEXT + ", " +
		DatabaseConstants.FEED_FAVICON_URL + TEXT + ", " +
		DatabaseConstants.FEED_POSITIVE_COUNT + INTEGER + ", " +
		DatabaseConstants.FEED_NEGATIVE_COUNT + INTEGER + ", " +
		DatabaseConstants.FEED_NEUTRAL_COUNT + INTEGER + ", " +
		DatabaseConstants.FEED_FAVICON + TEXT + ", " +
		DatabaseConstants.FEED_FAVICON_FADE + TEXT + ", " +
		DatabaseConstants.FEED_FAVICON_BORDER + TEXT + ", " +
		DatabaseConstants.FEED_LINK + TEXT + ", " + 
		DatabaseConstants.FEED_SUBSCRIBERS + TEXT + ", " +
		DatabaseConstants.FEED_TITLE + TEXT + ", " + 
		DatabaseConstants.FEED_UPDATED_SECONDS +
		")";
	
	private final String USER_SQL = "CREATE TABLE " + DatabaseConstants.USER_TABLE + " (" + 
		DatabaseConstants.USER_PHOTO_URL + TEXT + ", " + 
		DatabaseConstants.USER_USERID + INTEGER + " PRIMARY KEY, " +
		DatabaseConstants.USER_USERNAME + TEXT + ")";
	
	private final String SOCIAL_FEED_SQL = "CREATE TABLE " + DatabaseConstants.SOCIALFEED_TABLE + " (" +
		DatabaseConstants.SOCIAL_FEED_ID + INTEGER + " PRIMARY KEY, " +
		DatabaseConstants.SOCIAL_FEED_POSITIVE_COUNT + INTEGER + ", " +
		DatabaseConstants.SOCIAL_FEED_NEGATIVE_COUNT + INTEGER + ", " +
		DatabaseConstants.SOCIAL_FEED_NEUTRAL_COUNT + INTEGER + ", " +
		DatabaseConstants.SOCIAL_FEED_ICON + TEXT + ", " + 
		DatabaseConstants.SOCIAL_FEED_TITLE + TEXT + ", " + 
		DatabaseConstants.SOCIAL_FEED_USERNAME + TEXT +
		")";

	private final String COMMENT_SQL = "CREATE TABLE " + DatabaseConstants.COMMENT_TABLE + " (" +
		DatabaseConstants.COMMENT_DATE + TEXT + ", " +
		DatabaseConstants.COMMENT_SHAREDDATE + TEXT + ", " +
		DatabaseConstants.COMMENT_SOURCE_USERID + TEXT + ", " +
		DatabaseConstants.COMMENT_ID + TEXT + " PRIMARY KEY, " +
		DatabaseConstants.COMMENT_LIKING_USERS + TEXT + ", " +
		DatabaseConstants.COMMENT_BYFRIEND + TEXT + ", " +
		DatabaseConstants.COMMENT_STORYID + TEXT + ", " + 
		DatabaseConstants.COMMENT_TEXT + TEXT + ", " +
		DatabaseConstants.COMMENT_USERID + TEXT +
		")";
	
	private final String REPLY_SQL = "CREATE TABLE " + DatabaseConstants.REPLY_TABLE + " (" +
		DatabaseConstants.REPLY_DATE + TEXT + ", " +
		DatabaseConstants.REPLY_SHORTDATE + TEXT + ", " +
		DatabaseConstants.REPLY_ID + TEXT + " PRIMARY KEY, " +
		DatabaseConstants.REPLY_COMMENTID + TEXT + ", " + 
		DatabaseConstants.REPLY_TEXT + TEXT + ", " +
		DatabaseConstants.REPLY_USERID + TEXT +
		")";
	
	private final String OFFLINE_UPDATE_SQL = "CREATE TABLE " + DatabaseConstants.UPDATE_TABLE + " (" +
		DatabaseConstants.UPDATE_ID + INTEGER + " PRIMARY KEY, " + 
		DatabaseConstants.UPDATE_TYPE + INTEGER + ", " + 
		DatabaseConstants.UPDATE_ARGUMENTS + TEXT +
		")";

    private final String STORY_TABLES_COLS = 
		DatabaseConstants.STORY_AUTHORS + TEXT + ", " +
		DatabaseConstants.STORY_CONTENT + TEXT + ", " +
		DatabaseConstants.STORY_DATE + TEXT + ", " +
		DatabaseConstants.STORY_SHARED_DATE + INTEGER + ", " +
		DatabaseConstants.STORY_SHORTDATE + TEXT + ", " +
		DatabaseConstants.STORY_LONGDATE + TEXT + ", " +
		DatabaseConstants.STORY_FEED_ID + INTEGER + ", " +
		DatabaseConstants.STORY_ID + TEXT + " PRIMARY KEY, " +
		DatabaseConstants.STORY_INTELLIGENCE_AUTHORS + INTEGER + ", " +
		DatabaseConstants.STORY_INTELLIGENCE_FEED + INTEGER + ", " +
		DatabaseConstants.STORY_INTELLIGENCE_TAGS + INTEGER + ", " +
		DatabaseConstants.STORY_INTELLIGENCE_TITLE + INTEGER + ", " +
		DatabaseConstants.STORY_COMMENT_COUNT + INTEGER + ", " +
		DatabaseConstants.STORY_SHARE_COUNT + INTEGER + ", " +
		DatabaseConstants.STORY_SOCIAL_USER_ID + TEXT + ", " +
		DatabaseConstants.STORY_SOURCE_USER_ID + TEXT + ", " +
		DatabaseConstants.STORY_SHARED_USER_IDS + TEXT + ", " +
		DatabaseConstants.STORY_PUBLIC_USER_IDS + TEXT + ", " +
		DatabaseConstants.STORY_FRIEND_USER_IDS + TEXT + ", " +
		DatabaseConstants.STORY_TAGS + TEXT + ", " +
		DatabaseConstants.STORY_PERMALINK + TEXT + ", " + 
		DatabaseConstants.STORY_READ + INTEGER + ", " +
		DatabaseConstants.STORY_TITLE + TEXT;

	private final String STORY_SQL = "CREATE TABLE " + DatabaseConstants.STORY_TABLE + " (" + STORY_TABLES_COLS + ")";

	private final String CLASSIFIER_SQL = "CREATE TABLE " + DatabaseConstants.CLASSIFIER_TABLE + " (" +
		DatabaseConstants.CLASSIFIER_ID + TEXT + ", " +
		DatabaseConstants.CLASSIFIER_KEY + TEXT + ", " + 
		DatabaseConstants.CLASSIFIER_TYPE + TEXT + ", " +
		DatabaseConstants.CLASSIFIER_VALUE + TEXT +
		")";

	private final String FEED_FOLDER_SQL = "CREATE TABLE " + DatabaseConstants.FEED_FOLDER_MAP_TABLE + " (" +
		DatabaseConstants.FEED_FOLDER_FOLDER_NAME + TEXT + " NOT NULL, " +
		DatabaseConstants.FEED_FOLDER_FEED_ID + INTEGER + " NOT NULL, " +
		"PRIMARY KEY (" + DatabaseConstants.FEED_FOLDER_FOLDER_NAME + ", " + DatabaseConstants.FEED_FOLDER_FEED_ID + ") " + 
		")";
	
	private final String SOCIALFEED_STORIES_SQL = "CREATE TABLE " + DatabaseConstants.SOCIALFEED_STORY_MAP_TABLE + " (" +
		DatabaseConstants.SOCIALFEED_STORY_STORYID  + TEXT + " NOT NULL, " +
		DatabaseConstants.SOCIALFEED_STORY_USER_ID  + INTEGER + " NOT NULL, " +
		"PRIMARY KEY (" + DatabaseConstants.SOCIALFEED_STORY_STORYID  + ", " + DatabaseConstants.SOCIALFEED_STORY_USER_ID + ") " + 
	    ")";

	private final String STARRED_STORIES_SQL = "CREATE TABLE " + DatabaseConstants.STARRED_STORIES_TABLE + " (" + STORY_TABLES_COLS + ")";

    private final String STARRED_STORIES_COUNT_SQL = "CREATE TABLE " + DatabaseConstants.STARRED_STORY_COUNT_TABLE + " (" +
        DatabaseConstants.STARRED_STORY_COUNT_COUNT + INTEGER + " NOT NULL" +
        ")";


	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(FEED_SQL);
		db.execSQL(SOCIAL_FEED_SQL);
		db.execSQL(FOLDER_SQL);
		db.execSQL(USER_SQL);
		db.execSQL(STORY_SQL);
		db.execSQL(COMMENT_SQL);
		db.execSQL(REPLY_SQL);
		db.execSQL(CLASSIFIER_SQL);
		db.execSQL(FEED_FOLDER_SQL);
		db.execSQL(SOCIALFEED_STORIES_SQL);
        db.execSQL(STARRED_STORIES_SQL);
        db.execSQL(STARRED_STORIES_COUNT_SQL);
		db.execSQL(OFFLINE_UPDATE_SQL);
	}
	
	public void dropAndRecreateTables() {
		SQLiteDatabase db = getWritableDatabase();
		String drop = "DROP TABLE IF EXISTS ";
		db.execSQL(drop + DatabaseConstants.FEED_TABLE);
		db.execSQL(drop + DatabaseConstants.SOCIALFEED_TABLE);
		db.execSQL(drop + DatabaseConstants.FOLDER_TABLE);
		db.execSQL(drop + DatabaseConstants.STORY_TABLE);
		db.execSQL(drop + DatabaseConstants.USER_TABLE);
		db.execSQL(drop + DatabaseConstants.COMMENT_TABLE);
		db.execSQL(drop + DatabaseConstants.REPLY_TABLE);
		db.execSQL(drop + DatabaseConstants.CLASSIFIER_TABLE);
		db.execSQL(drop + DatabaseConstants.FEED_FOLDER_MAP_TABLE);
		db.execSQL(drop + DatabaseConstants.SOCIALFEED_STORY_MAP_TABLE);
		db.execSQL(drop + DatabaseConstants.STARRED_STORIES_TABLE);
		db.execSQL(drop + DatabaseConstants.STARRED_STORY_COUNT_TABLE);
		db.execSQL(drop + DatabaseConstants.UPDATE_TABLE);
		
		onCreate(db);
		
		db.close();
	}

    @Override
    public void onUpgrade(SQLiteDatabase db, int previousVersion, int nextVersion) {
        // TODO: Handle DB version updates using switch
    }


}
