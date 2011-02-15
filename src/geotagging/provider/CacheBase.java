package geotagging.provider;

import android.provider.BaseColumns;


public final class CacheBase {
	
	// shared preference file name across the application
	public static final String PREFERENCE_FILENAME = "GeotaggingStates";
	
	// This class cannot be instantiated
	private CacheBase() {
		
	}
	
	interface EntitiesColumns {
		String ENTITY_ID = "entity_id";
		String ENTITY_TITLE = "title";
		String ENTITY_DESCRIPTION = "description";
		String ENTITY_ICONURL = "icon_url";
		String ENTITY_LOCATION = "location";
		String ENTITY_LAT = "lat";
		String ENTITY_LNG = "lng";
		String ENTITY_UPDATEDAT = "updated_at"; 
	}
	
	interface CommentsColumns {
		String COMMENT_USERIMG = "user_img";
		String COMMENT_USERNAME = "user_name";
		String COMMENT_DESCRIPTION = "description";
		String COMMENT_IMG = "comment_img";
		String COMMENT_ID = "comment_id";
		String COMMENT_TIME = "time";
		String COMMENT_CATEGORYID = "category_id";
		String COMMENT_ENTITYID = "entity_id";
	}
	
	interface ResponsesColumns {
		String RESPONSE_USERNAME = "user_name";
		String RESPONSE_DESCRIPTION = "description";
		String RESPONSE_COMMENTID = "comment_id";
		String RESPONSE_TIME = "time";
		String RESPONSE_CATEGORYID = "category_id";
		String RESPONSE_USERIMG = "user_img";
	}
	
	interface StatesColumns {
		String STATE_USERNAME = "user_name";
		String STATE_PASSWORD = "password";
		String STATE_LATESTENTITYID = "latest_entityid";
		String STATE_LATESTCOMMENTID = "latest_commentid";
		String STATE_LATESTRESPONSEID = "latest_responseid";
		String STATE_CACHEDENTITYID = "cached_entityid";
		String STATE_CACHEDCOMMENTID = "cached_commentid";
		String STATE_CACHEDRESPONSEID = "cached_responseid";
	}
	
	interface CommentCategoriesColumns {
		String CATEGORY_ID = "category_id";
		String CATEGORY_NAME = "name";
		String CATEGORY_CREATEDAT = "created_at";
	}
	
	interface ResponseCategoriesColumns {
		String CATEGORY_ID = "category_id";
		String CATEGORY_NAME = "name";
		String CATEGORY_CREATEDAT = "created_at";
	}
	
	interface CommentCounterColumns {
		String COUNTER_ENTITYID = "entity_id";
		String COUNTER_CATEGORYID = "category_id";
		String COUNTER_COUNTER = "counter";
		String COUNTER_CATEGORY_NAME = "category_name";
	}
	
	interface ResponseCounterColumns {
		String COUNTER_COMMENTID = "comment_id";
		String COUNTER_CATEGORYID = "category_id";
		String COUNTER_COUNTER = "counter";	
		String COUNTER_CATEGORY_NAME = "category_name";
	}
	
	public static class Entities implements EntitiesColumns, BaseColumns {
		// This class can be extended to support content provider
	}
	
	public static class Comments implements CommentsColumns, BaseColumns {
		// This class can be extended to support content provider
	}
	
	public static class Responses implements ResponsesColumns, BaseColumns {
		// This class can be extended to support content provider
	}
	
	public static class States implements StatesColumns, BaseColumns {
		// This class can be extended to support content provider
	}
	
	public static class CommentCategories implements CommentCategoriesColumns, BaseColumns {
		// This class can be extended to support content provider
	}
	
	public static class ResponseCategories implements ResponseCategoriesColumns, BaseColumns {
		// This class can be extended to support content provider
	}
	
	public static class CommentCounters implements CommentCounterColumns, BaseColumns {
		// This class can be extended to support content provider
	}
	
	public static class ResponseCounters implements ResponseCounterColumns, BaseColumns {
		// This class can be extended to support content provider
	}
	
	
}
