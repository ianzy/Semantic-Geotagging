package geotagging.provider;

import geotagging.DES.Comment;
import geotagging.DES.Entity;
import geotagging.DES.States;
import geotagging.provider.CacheBase.CommentCategories;
import geotagging.provider.CacheBase.Comments;
import geotagging.provider.CacheBase.Entities;
import geotagging.provider.CacheBase.ResponseCategories;
import geotagging.provider.CacheBase.Responses;
import geotagging.provider.GeotaggingDatabaseHelper.Tables;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class DatabaseAdapter {

	private GeotaggingDatabaseHelper mDatabaseHelper;
	private SQLiteDatabase db;
	
	public DatabaseAdapter(Context context) {
		mDatabaseHelper = new GeotaggingDatabaseHelper(context);
	}
	
	public void open() {
		db = mDatabaseHelper.getWritableDatabase();
	}
	
	public void close() {
		mDatabaseHelper.close();
	}

	public Cursor GetAllEntities() {
		return db.query(Tables.ENTITIES, EntityQuery.PROJECTION, null, null, null, null, Entities.ENTITY_ID + " DESC");
	}
	
	public Cursor GetComments(int entity_id, int category_id) {
		return db.query(Tables.COMMENTS, CommentQuery.PROJECTION,
				Comments.COMMENT_ENTITYID + "=" + String.valueOf(entity_id) + " AND " + Comments.COMMENT_CATEGORYID+"="+String.valueOf(category_id),
				null, null, null, Comments.COMMENT_ID + " DESC");
	}
	
	public Cursor GetResponses(int comment_id) {
		return db.query(Tables.RESPONSES, ResponseQuery.PROJECTION, 
				Responses.RESPONSE_COMMENTID+"="+String.valueOf(comment_id), null, null, null, Responses._ID + " DESC");
	}
	
	public Cursor GetCommentCategories() {
		return db.query(Tables.COMMENTCATEGORIES, CommentCategoryQuery.PROJECTION, null, null, null, null, null);
	}
	
	public Cursor GetResponseCategories() {
		return db.query(Tables.RESPONSECATEGORIES, ResponseCategoryQuery.PROJECTION, null, null, null, null, null);
	}
	
	public Cursor GetStates(String username) {
		Cursor mCursor =
        db.query(true, Tables.STATES, StatesQuery.PROJECTION,
        		geotagging.provider.CacheBase.States.STATE_USERNAME + "=" + username, 
        		null,
        		null, 
        		null, 
        		null, 
        		null);
	    if (mCursor != null) {
	        mCursor.moveToFirst();
	    }
	    return mCursor;
	}
	
//	//handles user insert
//	public long createEntity(Entity e) {
//		return 0;
//	}
//	
//	public long createComment(Comment c) {
//		return 0;
//	}
//	
//	public long createResponse(Response r) {
//		return 0;
//	}
	
	public long createStates(String username, String password) {
		ContentValues initialValues = new ContentValues();
        initialValues.put(geotagging.provider.CacheBase.States.STATE_USERNAME, username);
        initialValues.put(geotagging.provider.CacheBase.States.STATE_PASSWORD, password);
        return db.insert(Tables.STATES, null, initialValues);
	}
	
	public boolean updateStates(String username, States s) {
		ContentValues args = new ContentValues();
        args.put(geotagging.provider.CacheBase.States.STATE_CACHEDCOMMENTID, s.getLatest_commentid());
        args.put(geotagging.provider.CacheBase.States.STATE_CACHEDENTITYID, s.getCached_entityid());
        args.put(geotagging.provider.CacheBase.States.STATE_CACHEDRESPONSEID, s.getCached_responseid());
        args.put(geotagging.provider.CacheBase.States.STATE_LATESTCOMMENTID, s.getLatest_commentid());
        args.put(geotagging.provider.CacheBase.States.STATE_LATESTENTITYID, s.getLatest_entityid());
        args.put(geotagging.provider.CacheBase.States.STATE_LATESTRESPONSEID, s.getLatest_responseid());
        args.put(geotagging.provider.CacheBase.States.STATE_PASSWORD, s.getPassword());
        args.put(geotagging.provider.CacheBase.States.STATE_USERNAME, s.getUsername());
        return db.update(Tables.STATES, args, 
        		geotagging.provider.CacheBase.States.STATE_USERNAME + "=" + username, null) > 0;
	}
	
	public int createEntities(List<Entity> le) {
		ContentValues args;
		for(int i=0; i<le.size(); i++) {
			Entity e = le.get(i);
			args = new ContentValues();
			args.put(Entities.ENTITY_DESCRIPTION, e.getDescription());
			args.put(Entities.ENTITY_ICONURL, e.getIconURI());
			args.put(Entities.ENTITY_ID, e.getId());
			args.put(Entities.ENTITY_LAT, e.getLat());
			args.put(Entities.ENTITY_LNG, e.getLng());
			args.put(Entities.ENTITY_LOCATION, e.getLocation());
			args.put(Entities.ENTITY_TITLE, e.getTitle());
			args.put(Entities.ENTITY_UPDATEDAT, e.getUpdatedAt());
			db.insert(Tables.ENTITIES, null, args);
		}
		return 0;
	}
	
	public int createComments(List<Comment> lc) {
		ContentValues args;
		for(int i=0; i<lc.size(); i++) {
			Comment c = lc.get(i);
			args = new ContentValues();
			args.put(Comments.COMMENT_CATEGORYID, c.getCategory_id());
			args.put(Comments.COMMENT_DESCRIPTION, c.getDescription());
			args.put(Comments.COMMENT_ENTITYID, c.getEntity_id());
			args.put(Comments.COMMENT_ID, c.getCommentId());
			args.put(Comments.COMMENT_IMG, c.getCommentImg());
			args.put(Comments.COMMENT_TIME, c.getTime());
			args.put(Comments.COMMENT_USERIMG, c.getUserImg());
			args.put(Comments.COMMENT_USERNAME, c.getUserName());
			db.insert(Tables.ENTITIES, null, args);
		}
		return 0;
	}
	
	public int createResponses(List<Comment> lr) {
		ContentValues args;
		for(int i=0; i<lr.size(); i++) {
			Comment c = lr.get(i);
			args = new ContentValues();
			args.put(Responses.RESPONSE_CATEGORYID, c.getCategory_id());
			args.put(Responses.RESPONSE_COMMENTID, c.getCommentId());
			args.put(Responses.RESPONSE_DESCRIPTION, c.getDescription());
			args.put(Responses.RESPONSE_TIME, c.getTime());
			args.put(Responses.RESPONSE_USERNAME, c.getUserName());
			db.insert(Tables.ENTITIES, null, args);
		}
		return 0;
	}
	
	private interface EntityQuery {
		String[] PROJECTION = {
			Entities.ENTITY_DESCRIPTION,
			Entities.ENTITY_ICONURL,
			Entities.ENTITY_ID,
			Entities.ENTITY_LAT,
			Entities.ENTITY_LNG,
			Entities.ENTITY_LOCATION,
			Entities.ENTITY_TITLE,
			Entities.ENTITY_UPDATEDAT
		};
	}
	
	private interface CommentQuery {
		String[] PROJECTION = {
			Comments.COMMENT_CATEGORYID,
			Comments.COMMENT_DESCRIPTION,
			Comments.COMMENT_ENTITYID,
			Comments.COMMENT_ID,
			Comments.COMMENT_IMG,
			Comments.COMMENT_TIME,
			Comments.COMMENT_USERIMG,
			Comments.COMMENT_USERNAME
		};
	}

	private interface ResponseQuery {
		String[] PROJECTION = {
			Responses.RESPONSE_CATEGORYID,
			Responses.RESPONSE_COMMENTID,
			Responses.RESPONSE_DESCRIPTION,
			Responses.RESPONSE_TIME,
			Responses.RESPONSE_USERNAME
		};
	}
	
	private interface CommentCategoryQuery {
		String[] PROJECTION = {
			CommentCategories.CATEGORY_CREATEDAT,
			CommentCategories.CATEGORY_ID,
			CommentCategories.CATEGORY_NAME
		};
	}
	
	private interface ResponseCategoryQuery {
		String[] PROJECTION = {
			ResponseCategories.CATEGORY_CREATEDAT,
			ResponseCategories.CATEGORY_ID,
			ResponseCategories.CATEGORY_NAME
		};
	}
	
	private interface StatesQuery {
		String[] PROJECTION = {
			geotagging.provider.CacheBase.States.STATE_CACHEDCOMMENTID,
			geotagging.provider.CacheBase.States.STATE_CACHEDENTITYID,
			geotagging.provider.CacheBase.States.STATE_CACHEDRESPONSEID,
			geotagging.provider.CacheBase.States.STATE_LATESTCOMMENTID,
			geotagging.provider.CacheBase.States.STATE_LATESTENTITYID,
			geotagging.provider.CacheBase.States.STATE_LATESTRESPONSEID,
			geotagging.provider.CacheBase.States.STATE_PASSWORD,
			geotagging.provider.CacheBase.States.STATE_USERNAME
		};
	}
}
