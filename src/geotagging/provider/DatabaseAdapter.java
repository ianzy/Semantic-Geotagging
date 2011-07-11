package geotagging.provider;

import geotagging.DES.Comment;
import geotagging.DES.CommentCategory;
import geotagging.DES.Entity;
import geotagging.DES.ResponseCategory;
import geotagging.DES.States;
import geotagging.provider.CacheBase.CommentCategories;
import geotagging.provider.CacheBase.CommentCounters;
import geotagging.provider.CacheBase.CommentDrafts;
import geotagging.provider.CacheBase.Comments;
import geotagging.provider.CacheBase.Entities;
import geotagging.provider.CacheBase.ResponseCategories;
import geotagging.provider.CacheBase.ResponseCounters;
import geotagging.provider.CacheBase.ResponseDrafts;
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
		db.close();
	}
	
	public void upgradeDatabase() {
		if(db.isOpen()) {
			mDatabaseHelper.onUpgrade(db, db.getVersion(), db.getVersion()+1);
		}
	}

	public Cursor getAllEntities() {
		return db.query(Tables.ENTITIES, EntityQuery.PROJECTION, null, null, null, null, Entities.ENTITY_ID + " DESC");
	}
	
	public Cursor getLatestComment(int entity_id) {
		return db.query(Tables.COMMENTS, CommentQuery.PROJECTION,
				Comments.COMMENT_ENTITYID + "=" + String.valueOf(entity_id),
				null, null, null, Comments.COMMENT_ID + " DESC", "1");
	}
	
	public Cursor getLatestResponse(int comment_id) {
		return db.query(Tables.RESPONSES, ResponseQuery.PROJECTION, 
				Responses.RESPONSE_COMMENTID+"="+String.valueOf(comment_id), null, null, null, Responses._ID + " DESC", "1");
	}
	
	public Cursor getComments(int entity_id, int category_id) {
		return db.query(Tables.COMMENTS, CommentQuery.PROJECTION,
				Comments.COMMENT_ENTITYID + "=" + String.valueOf(entity_id) + " AND " + Comments.COMMENT_CATEGORYID+"="+String.valueOf(category_id),
				null, null, null, Comments.COMMENT_ID + " ASC");
	}
	
	public Cursor getResponses(int comment_id) {
		return db.query(Tables.RESPONSES, ResponseQuery.PROJECTION, 
				Responses.RESPONSE_COMMENTID+"="+String.valueOf(comment_id), null, null, null, Responses._ID + " ASC");
	}
	
	public Cursor getCommentCategories() {
		return db.query(Tables.COMMENTCATEGORIES, CommentCategoryQuery.PROJECTION, null, null, null, null, null);
	}
	
	public Cursor getResponseCategories() {
		return db.query(Tables.RESPONSECATEGORIES, ResponseCategoryQuery.PROJECTION, null, null, null, null, null);
	}
	
	public Cursor getStates(String username) {
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
	
	public Cursor getCommentCounters(int entity_id) {
		return db.query(Tables.COMMENTCOUNTERS, 
				CommentCounterQuery.PROJECTION, CommentCounters.COUNTER_ENTITYID
				+"="
				+String.valueOf(entity_id), 
				null, null, null, CommentCounters.COUNTER_CATEGORYID + " ASC");
	}
	
	public Cursor getResponseCounters(int comment_id) {
		return db.query(Tables.RESPONSECOUNTERS, ResponseCounterQuery.PROJECTION,
				ResponseCounters.COUNTER_COMMENTID+"="+String.valueOf(comment_id),
				null, null, null, ResponseCounters.COUNTER_CATEGORY_NAME + " ASC");
	}
	
	public boolean updateCommentCounter(int entity_id, int category_id, int count) {
		Cursor c = db.query(Tables.COMMENTCOUNTERS, 
				CommentCounterQuery.PROJECTION, CommentCounters.COUNTER_ENTITYID
				+"="+String.valueOf(entity_id)
				+" AND "+CommentCounters.COUNTER_CATEGORYID+"="+String.valueOf(category_id), 
				null, null, null, null);
		if (c.moveToFirst()) {
			ContentValues args = new ContentValues();
	        args.put(CommentCounters.COUNTER_ENTITYID, entity_id);
	        args.put(CommentCounters.COUNTER_CATEGORYID, category_id);
	        args.put(CommentCounters.COUNTER_CATEGORY_NAME, c.getString(c.getColumnIndex(CommentCounters.COUNTER_CATEGORY_NAME)));
	        args.put(CommentCounters.COUNTER_COUNTER, count);
	        c.close();
	        return db.update(Tables.COMMENTCOUNTERS, args, 
	        		CommentCounters.COUNTER_CATEGORYID+"="+String.valueOf(category_id)+" AND "
	        		+CommentCounters.COUNTER_ENTITYID+"="+String.valueOf(entity_id), null) > 0;
		}
		c.close();
		return false;
		
	}
	
	public boolean updateResponseCounter(int comment_id, int category_id, int count) {
		Cursor c = db.query(Tables.RESPONSECOUNTERS, 
				CommentCounterQuery.PROJECTION, ResponseCounters.COUNTER_COMMENTID
				+"="+String.valueOf(comment_id)
				+" AND "+ResponseCounters.COUNTER_CATEGORYID+"="+String.valueOf(category_id), 
				null, null, null, null);
		if (c.moveToFirst()) {
			ContentValues args = new ContentValues();
	        args.put(ResponseCounters.COUNTER_COMMENTID, comment_id);
	        args.put(ResponseCounters.COUNTER_CATEGORYID, category_id);
	        args.put(ResponseCounters.COUNTER_CATEGORY_NAME, c.getString(c.getColumnIndex(ResponseCounters.COUNTER_CATEGORY_NAME)));
	        args.put(ResponseCounters.COUNTER_COUNTER, count);
	        c.close();
	        return db.update(Tables.RESPONSECOUNTERS, args, 
	        		ResponseCounters.COUNTER_CATEGORYID+"="+String.valueOf(category_id)+" AND "
	        		+ResponseCounters.COUNTER_COMMENTID+"="+String.valueOf(comment_id), null) > 0;
	    }
		
		c.close();
		return false;
	}
	
	public int createCommentCategories(List<CommentCategory> lcc) {
		ContentValues initialValues;
		CommentCategory cc;
		for(int i=0; i<lcc.size(); i++)	{
			cc = lcc.get(i);
			initialValues = new ContentValues();
	        initialValues.put(geotagging.provider.CacheBase.CommentCategories.CATEGORY_ID, cc.getCategory_id());
	        initialValues.put(geotagging.provider.CacheBase.CommentCategories.CATEGORY_NAME, cc.getName());
	        db.insert(Tables.COMMENTCATEGORIES, null, initialValues);
		}
		
		return 0;
	}
	
	public int createResponseCategories(List<ResponseCategory> lrc) {
		ContentValues initialValues;
		ResponseCategory rc;
		for(int i=0; i<lrc.size(); i++)	{
			rc = lrc.get(i);
			initialValues = new ContentValues();
	        initialValues.put(geotagging.provider.CacheBase.ResponseCategories.CATEGORY_ID, rc.getCategory_id());
	        initialValues.put(geotagging.provider.CacheBase.ResponseCategories.CATEGORY_NAME, rc.getName());
	        db.insert(Tables.RESPONSECATEGORIES, null, initialValues);
		}
		
		return 0;
	}
	
	public int createCommentCategoriesCounter(int entity_id) {
		ContentValues initialValues;
		Cursor c = this.getCommentCategories();
		if(c.moveToFirst()) {
			do {
				initialValues = new ContentValues();
		        initialValues.put(CommentCounters.COUNTER_CATEGORYID, c.getInt(c.getColumnIndex(CommentCategories.CATEGORY_ID)));
		        initialValues.put(CommentCounters.COUNTER_COUNTER, 0);
		        initialValues.put(CommentCounters.COUNTER_ENTITYID, entity_id);
		        initialValues.put(CommentCounters.COUNTER_CATEGORY_NAME, c.getString(c.getColumnIndex(CommentCategories.CATEGORY_NAME)));
		        initialValues.put(CommentCounters.CATEGORY_IMPORTANT_TAG, false);
		        db.insert(Tables.COMMENTCOUNTERS, null, initialValues);
				
			} while(c.moveToNext());
		}

		c.close();
		return 0;
	}
	
	public int createResponseCategoriesCounter(int comment_id) {
		ContentValues initialValues;
		Cursor c = this.getResponseCategories();
		if(c.moveToFirst()) {
			do {
				initialValues = new ContentValues();
		        initialValues.put(ResponseCounters.COUNTER_CATEGORYID, c.getInt(c.getColumnIndex(ResponseCategories.CATEGORY_ID)));
		        initialValues.put(ResponseCounters.COUNTER_COUNTER, 0);
		        initialValues.put(ResponseCounters.COUNTER_COMMENTID, comment_id);
		        initialValues.put(ResponseCounters.COUNTER_CATEGORY_NAME, c.getString(c.getColumnIndex(ResponseCategories.CATEGORY_NAME)));
		        initialValues.put(ResponseCounters.CATEGORY_IMPORTANT_TAG, false);
		        db.insert(Tables.RESPONSECOUNTERS, null, initialValues);
				
			} while(c.moveToNext());
		}

		c.close();
		return 0;
	}
	
	
	
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
		Entity e;
		for(int i=0; i<le.size(); i++) {
			e = le.get(i);
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
		Comment c;
		for(int i=0; i<lc.size(); i++) {
			c = lc.get(i);
			args = new ContentValues();
			args.put(Comments.COMMENT_CATEGORYID, c.getCategory_id());
			args.put(Comments.COMMENT_DESCRIPTION, c.getDescription());
			args.put(Comments.COMMENT_ENTITYID, c.getEntity_id());
			args.put(Comments.COMMENT_ID, c.getCommentId());
			args.put(Comments.COMMENT_IMG, c.getCommentImg());
			args.put(Comments.COMMENT_TIME, c.getTime());
			args.put(Comments.COMMENT_USERIMG, c.getUserImg());
			args.put(Comments.COMMENT_COUNTER, 0);
//			args.put(Comments.COMMENT_COUNTER, c.getCommentCounter());
			args.put(Comments.COMMENT_USERNAME, c.getUserName());
			args.put(Comments.COMMENT_IMPORTANT_TAG, c.isImportantTag());
			db.insert(Tables.COMMENTS, null, args);
		}
		return 0;
	}
	
	public int createResponses(List<Comment> lr) {
		ContentValues args;
		Comment c;
		for(int i=0; i<lr.size(); i++) {
			c = lr.get(i);
			args = new ContentValues();
			args.put(Responses.RESPONSE_CATEGORYID, c.getCategory_id());
			args.put(Responses.RESPONSE_COMMENTID, c.getCommentId());
			args.put(Responses.RESPONSE_DESCRIPTION, c.getDescription());
			args.put(Responses.RESPONSE_TIME, c.getTime());
			args.put(Responses.RESPONSE_USERNAME, c.getUserName());
			args.put(Responses.RESPONSE_USERIMG, c.getUserImg());
			args.put(Responses.RESPONSE_COUNTER, 0);
//			args.put(Responses.RESPONSE_COUNTER, c.getCommentCounter());
			args.put(Responses.RESPONSE_ID, c.getEntity_id());
			args.put(Responses.RESPONSE_IMPORTANT_TAG, c.isImportantTag());
			db.insert(Tables.RESPONSES, null, args);
			
			//update counter in both comment and response tables
			Cursor cursor = db.query(Tables.COMMENTS, 
					CommentQuery.PROJECTION, Comments.COMMENT_ID
					+"="+String.valueOf(c.getCommentId()), 
					null, null, null, null);
			if (cursor.moveToFirst()) {
				ContentValues comment = new ContentValues();
				comment.put(Comments.COMMENT_CATEGORYID, cursor.getInt(cursor.getColumnIndex(Comments.COMMENT_CATEGORYID)));
		        comment.put(Comments.COMMENT_COUNTER, cursor.getInt(cursor.getColumnIndex(Comments.COMMENT_COUNTER))+1);
		        comment.put(Comments.COMMENT_DESCRIPTION, cursor.getString(cursor.getColumnIndex(Comments.COMMENT_DESCRIPTION)));
		        comment.put(Comments.COMMENT_ENTITYID, cursor.getInt(cursor.getColumnIndex(Comments.COMMENT_ENTITYID)));
		        comment.put(Comments.COMMENT_ID, cursor.getInt(cursor.getColumnIndex(Comments.COMMENT_ID)));
		        comment.put(Comments.COMMENT_IMG, cursor.getString(cursor.getColumnIndex(Comments.COMMENT_IMG)));
		        comment.put(Comments.COMMENT_TIME, cursor.getString(cursor.getColumnIndex(Comments.COMMENT_TIME)));
		        comment.put(Comments.COMMENT_USERIMG, cursor.getString(cursor.getColumnIndex(Comments.COMMENT_USERIMG)));
		        comment.put(Comments.COMMENT_USERNAME, cursor.getString(cursor.getColumnIndex(Comments.COMMENT_USERNAME)));
		        comment.put(Comments.COMMENT_IMPORTANT_TAG, cursor.getInt(cursor.getColumnIndex(Comments.COMMENT_IMPORTANT_TAG)) > 0);
		        db.update(Tables.COMMENTS, comment, 
		        		Comments.COMMENT_ID+"="+String.valueOf(c.getCommentId()), null);
		    }
			cursor.close();
			
			cursor = db.query(Tables.RESPONSES, 
					ResponseQuery.PROJECTION, Responses.RESPONSE_ID
					+"="+String.valueOf(c.getCommentId()), 
					null, null, null, null);
			if (cursor.moveToFirst()) {
				ContentValues response = new ContentValues();
				response.put(Responses.RESPONSE_CATEGORYID, cursor.getInt(cursor.getColumnIndex(Responses.RESPONSE_CATEGORYID)));
				response.put(Responses.RESPONSE_COMMENTID, cursor.getInt(cursor.getColumnIndex(Responses.RESPONSE_COMMENTID)));
				response.put(Responses.RESPONSE_COUNTER, cursor.getInt(cursor.getColumnIndex(Responses.RESPONSE_COUNTER))+1);
				response.put(Responses.RESPONSE_ID, cursor.getInt(cursor.getColumnIndex(Responses.RESPONSE_ID)));
				response.put(Responses.RESPONSE_DESCRIPTION, cursor.getString(cursor.getColumnIndex(Responses.RESPONSE_DESCRIPTION)));
				response.put(Responses.RESPONSE_TIME, cursor.getString(cursor.getColumnIndex(Responses.RESPONSE_TIME)));
				response.put(Responses.RESPONSE_USERIMG, cursor.getString(cursor.getColumnIndex(Responses.RESPONSE_USERIMG)));
				response.put(Responses.RESPONSE_USERNAME, cursor.getString(cursor.getColumnIndex(Responses.RESPONSE_USERNAME)));
				response.put(Responses.RESPONSE_IMPORTANT_TAG, cursor.getInt(cursor.getColumnIndex(Responses.RESPONSE_IMPORTANT_TAG)) > 0);
				db.update(Tables.RESPONSES, response, 
						Responses.RESPONSE_ID+"="+String.valueOf(c.getCommentId()), null);
			}
			cursor.close();
			
		}
		return 0;
	}
	
	//comment drafts CRUD
	public long createCommentDraft(int entityId, int categoryId, String content, boolean important) {
		ContentValues initialValues = new ContentValues();
        initialValues.put(CommentDrafts.DRAFT_CATEGORYID, categoryId);
        initialValues.put(CommentDrafts.DRAFT_CONTENT, content);
        initialValues.put(CommentDrafts.DRAFT_ENTITYID, entityId);
        initialValues.put(CommentDrafts.DRAFT_IMPORTANT, important);
        return db.insert(Tables.COMMENTDRAFTS, null, initialValues);
	}
	
	public boolean updateCommentDraft(int entityId, int categoryId, String content, boolean important) {
		ContentValues args = new ContentValues();
        args.put(CommentDrafts.DRAFT_ENTITYID, entityId);
        args.put(CommentDrafts.DRAFT_CATEGORYID, categoryId);
        args.put(CommentDrafts.DRAFT_CONTENT, content);
        args.put(CommentDrafts.DRAFT_IMPORTANT, important);
        return db.update(Tables.COMMENTDRAFTS, args, 
        		CommentDrafts.DRAFT_CATEGORYID+"="+String.valueOf(categoryId)+" AND "
        		+CommentDrafts.DRAFT_ENTITYID+"="+String.valueOf(entityId), null) > 0;
	}
	
	public Cursor getCommentDraft(int entityId, int categoryId) {
		return db.query(Tables.COMMENTDRAFTS, 
				CommentDraftQuery.PROJECTION, CommentDrafts.DRAFT_CATEGORYID
				+"="+String.valueOf(categoryId)
				+" AND "+CommentDrafts.DRAFT_ENTITYID+"="+String.valueOf(entityId), 
				null, null, null, null);
	}
	
	public int deleteCommentDraft(int entityId, int categoryId) {
		return db.delete(Tables.COMMENTDRAFTS, CommentDrafts.DRAFT_CATEGORYID+"="+String.valueOf(categoryId)+" AND "
        		+CommentDrafts.DRAFT_ENTITYID+"="+String.valueOf(entityId), null);
	}
	
	// response drafts CRUD
	public long createResponseDraft(int commentId, int categoryId, String content, boolean important) {
		ContentValues initialValues = new ContentValues();
        initialValues.put(ResponseDrafts.DRAFT_CATEGORYID, categoryId);
        initialValues.put(ResponseDrafts.DRAFT_CONTENT, content);
        initialValues.put(ResponseDrafts.DRAFT_COMMENTID, commentId);
        initialValues.put(ResponseDrafts.DRAFT_IMPORTANT, important);
        return db.insert(Tables.RESPONSEDRAFTS, null, initialValues);
	}
	
	public boolean updateResponseDraft(int commentId, int categoryId, String content, boolean important) {
		ContentValues args = new ContentValues();
        args.put(ResponseDrafts.DRAFT_COMMENTID, commentId);
        args.put(ResponseDrafts.DRAFT_CATEGORYID, categoryId);
        args.put(ResponseDrafts.DRAFT_CONTENT, content);
        args.put(ResponseDrafts.DRAFT_IMPORTANT, important);
        return db.update(Tables.RESPONSEDRAFTS, args, 
        		ResponseDrafts.DRAFT_CATEGORYID+"="+String.valueOf(categoryId)+" AND "
        		+ResponseDrafts.DRAFT_COMMENTID+"="+String.valueOf(commentId), null) > 0;
	}
	
	public Cursor getResponseDraft(int commentId, int categoryId) {
		return db.query(Tables.RESPONSEDRAFTS, 
				ResponseDraftQuery.PROJECTION, ResponseDrafts.DRAFT_CATEGORYID
				+"="+String.valueOf(categoryId)
				+" AND "+ResponseDrafts.DRAFT_COMMENTID+"="+String.valueOf(commentId), 
				null, null, null, null);
	}
	
	public int deleteResponseDraft(int commentId, int categoryId) {
		return db.delete(Tables.RESPONSEDRAFTS, ResponseDrafts.DRAFT_CATEGORYID+"="+String.valueOf(categoryId)+" AND "
        		+ResponseDrafts.DRAFT_COMMENTID+"="+String.valueOf(commentId), null);
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
			Comments.COMMENT_COUNTER,
			Comments.COMMENT_USERNAME,
			Comments.COMMENT_IMPORTANT_TAG
		};
	}

	private interface ResponseQuery {
		String[] PROJECTION = {
			Responses.RESPONSE_CATEGORYID,
			Responses.RESPONSE_COMMENTID,
			Responses.RESPONSE_DESCRIPTION,
			Responses.RESPONSE_TIME,
			Responses.RESPONSE_USERNAME,
			Responses.RESPONSE_USERIMG,
			Responses.RESPONSE_COUNTER,
			Responses.RESPONSE_ID,
			Responses.RESPONSE_IMPORTANT_TAG
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
	
	private interface CommentCounterQuery {
		String[] PROJECTION = {
			CommentCounters.COUNTER_CATEGORY_NAME,
			CommentCounters.COUNTER_CATEGORYID,
			CommentCounters.COUNTER_COUNTER,
			CommentCounters.CATEGORY_IMPORTANT_TAG
		};
	}
	
	private interface ResponseCounterQuery {
		String[] PROJECTION = {
			ResponseCounters.COUNTER_CATEGORY_NAME,
			ResponseCounters.COUNTER_CATEGORYID,
			ResponseCounters.COUNTER_COUNTER,
			ResponseCounters.CATEGORY_IMPORTANT_TAG
		};
	}
	
	private interface CommentDraftQuery {
		String[] PROJECTION = {
			CommentDrafts.DRAFT_CATEGORYID,
			CommentDrafts.DRAFT_CONTENT,
			CommentDrafts.DRAFT_IMPORTANT,
			CommentDrafts.DRAFT_ENTITYID
		};
	}
	
	private interface ResponseDraftQuery {
		String[] PROJECTION = {
			ResponseDrafts.DRAFT_CATEGORYID,
			ResponseDrafts.DRAFT_CONTENT,
			ResponseDrafts.DRAFT_IMPORTANT,
			ResponseDrafts.DRAFT_COMMENTID
		};
	}
}
