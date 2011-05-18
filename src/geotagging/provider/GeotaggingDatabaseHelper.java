package geotagging.provider;

import geotagging.provider.CacheBase.CommentCategories;
import geotagging.provider.CacheBase.CommentCounters;
import geotagging.provider.CacheBase.CommentDrafts;
import geotagging.provider.CacheBase.Comments;
import geotagging.provider.CacheBase.Entities;
import geotagging.provider.CacheBase.ResponseCategories;
import geotagging.provider.CacheBase.ResponseCounters;
import geotagging.provider.CacheBase.ResponseDrafts;
import geotagging.provider.CacheBase.Responses;
import geotagging.provider.CacheBase.States;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GeotaggingDatabaseHelper extends SQLiteOpenHelper {

	private static final String TAG = "CacheDataProvider";

    private static final String DATABASE_NAME = "geotagging_cache.db";
    private static final int DATABASE_VERSION = 1;
    
    interface Tables {
        String ENTITIES = "entities";
        String COMMENTS = "comments";
        String RESPONSES = "responses";
        String STATES = "states";
        String COMMENTCATEGORIES = "commentcategories";
        String RESPONSECATEGORIES = "responsecategories";
        String COMMENTCOUNTERS = "commentcounters";
        String RESPONSECOUNTERS = "responsecounters";
        String COMMENTDRAFTS = "commentdrafts";
        String RESPONSEDRAFTS = "responsedrafts";
    }

	
	public GeotaggingDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE " + Tables.ENTITIES + " ("
			      + Entities._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			      + Entities.ENTITY_DESCRIPTION + " TEXT,"
			      + Entities.ENTITY_ICONURL + " TEXT,"
			      + Entities.ENTITY_ID + " INTEGER,"
			      + Entities.ENTITY_LAT + " FLOAT,"
			      + Entities.ENTITY_LNG + " FLOAT,"
			      + Entities.ENTITY_LOCATION + " TEXT,"
			      + Entities.ENTITY_TITLE + " TEXT,"
			      + Entities.ENTITY_UPDATEDAT + " TEXT"
			      + ");");
		
		db.execSQL("CREATE TABLE " + Tables.COMMENTS + " ("
			      + Comments._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			      + Comments.COMMENT_CATEGORYID + " INTEGER,"
			      + Comments.COMMENT_DESCRIPTION + " TEXT,"
			      + Comments.COMMENT_ENTITYID + " INTEGER,"
			      + Comments.COMMENT_ID + " INTEGER,"
			      + Comments.COMMENT_IMG + " TEXT,"
			      + Comments.COMMENT_TIME + " TEXT,"
			      + Comments.COMMENT_USERIMG + " TEXT,"
			      + Comments.COMMENT_COUNTER + " INTEGER,"
			      + Comments.COMMENT_IMPORTANT_TAG + " BOOLEAN,"
			      + Comments.COMMENT_USERNAME + " TEXT"
			      + ");");
		
		db.execSQL("CREATE TABLE " + Tables.RESPONSES + " ("
			      + Responses._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			      + Responses.RESPONSE_CATEGORYID + " INTEGER,"
			      + Responses.RESPONSE_COMMENTID + " INTEGER,"
			      + Responses.RESPONSE_DESCRIPTION + " TEXT,"
			      + Responses.RESPONSE_TIME + " TEXT,"
			      + Responses.RESPONSE_USERIMG + " TEXT,"
			      + Responses.RESPONSE_USERNAME + " TEXT,"
			      + Responses.RESPONSE_COUNTER + " INTEGER,"
			      + Responses.RESPONSE_IMPORTANT_TAG + " BOOLEAN,"
			      + Responses.RESPONSE_ID + " INTEGER"
			      + ");");
		
		db.execSQL("CREATE TABLE " + Tables.STATES + " ("
			      + States._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			      + States.STATE_CACHEDCOMMENTID + " INTEGER,"
			      + States.STATE_CACHEDENTITYID + " INTEGER,"
			      + States.STATE_CACHEDRESPONSEID + " INTEGER,"
			      + States.STATE_LATESTCOMMENTID + " INTEGER,"
			      + States.STATE_LATESTENTITYID + " INTEGER,"
			      + States.STATE_LATESTRESPONSEID + " INTEGER,"
			      + States.STATE_PASSWORD + " TEXT,"
			      + States.STATE_USERNAME + " TEXT"
			      + ");");
		
		db.execSQL("CREATE TABLE " + Tables.COMMENTCATEGORIES + " ("
			      + CommentCategories._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			      + CommentCategories.CATEGORY_CREATEDAT + " TEXT,"
			      + CommentCategories.CATEGORY_ID + " INTEGER,"
			      + CommentCategories.CATEGORY_NAME + " TEXT"
			      + ");");
		
		db.execSQL("CREATE TABLE " + Tables.RESPONSECATEGORIES + " ("
			      + ResponseCategories._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			      + ResponseCategories.CATEGORY_CREATEDAT + " TEXT,"
			      + ResponseCategories.CATEGORY_ID + " INTEGER,"
			      + ResponseCategories.CATEGORY_NAME + " TEXT"
			      + ");");
		
		db.execSQL("CREATE TABLE " + Tables.COMMENTCOUNTERS + " ("
			      + CommentCounters._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			      + CommentCounters.COUNTER_CATEGORYID + " INTEGER,"
			      + CommentCounters.COUNTER_COUNTER + " INTEGER,"
			      + CommentCounters.COUNTER_ENTITYID + " INTEGER,"
			      + CommentCounters.CATEGORY_IMPORTANT_TAG + " BOOLEAN,"
			      + CommentCounters.COUNTER_CATEGORY_NAME + " TEXT"
			      + ");");
		
		db.execSQL("CREATE TABLE " + Tables.RESPONSECOUNTERS + " ("
			      + ResponseCounters._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			      + ResponseCounters.COUNTER_CATEGORYID + " INTEGER,"
			      + ResponseCounters.COUNTER_COUNTER + " INTEGER,"
			      + ResponseCounters.COUNTER_COMMENTID + " INTEGER,"
			      + ResponseCounters.CATEGORY_IMPORTANT_TAG + " BOOLEAN,"
			      + ResponseCounters.COUNTER_CATEGORY_NAME + " TEXT"
			      + ");");
		
		db.execSQL("CREATE TABLE " + Tables.RESPONSEDRAFTS + " ("
			      + ResponseDrafts._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			      + ResponseDrafts.DRAFT_CATEGORYID + " INTEGER,"
			      + ResponseDrafts.DRAFT_COMMENTID + " INTEGER,"
			      + ResponseDrafts.DRAFT_IMPORTANT + " BOOLEAN,"
			      + ResponseDrafts.DRAFT_CONTENT + " TEXT"
			      + ");");
		
		db.execSQL("CREATE TABLE " + Tables.COMMENTDRAFTS + " ("
			      + CommentDrafts._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			      + CommentDrafts.DRAFT_CATEGORYID + " INTEGER,"
			      + CommentDrafts.DRAFT_ENTITYID + " INTEGER,"
			      + CommentDrafts.DRAFT_IMPORTANT + " BOOLEAN,"
			      + CommentDrafts.DRAFT_CONTENT + " TEXT"
			      + ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);

        // NOTE: This switch statement is designed to handle cascading database
        // updates, starting at the current version and falling through to all
        // future upgrade cases. Only use "break;" when you want to drop and
        // recreate the entire database.
        
    
        Log.w(TAG, "Destroying old data during upgrade");

        db.execSQL("DROP TABLE IF EXISTS " + Tables.COMMENTCATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.COMMENTCOUNTERS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.COMMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.ENTITIES);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.RESPONSECATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.RESPONSECOUNTERS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.RESPONSES);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.STATES);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.COMMENTDRAFTS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.RESPONSEDRAFTS);

        onCreate(db);

	}

}
