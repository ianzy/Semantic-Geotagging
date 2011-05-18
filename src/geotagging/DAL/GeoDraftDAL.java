package geotagging.DAL;

import geotagging.DES.Comment;
import geotagging.provider.DatabaseAdapter;
import geotagging.provider.CacheBase.CommentDrafts;
import geotagging.provider.CacheBase.ResponseDrafts;
import android.content.Context;
import android.database.Cursor;

public class GeoDraftDAL {
	private DatabaseAdapter da;
	
	public GeoDraftDAL(Context cx) {
		this.da = new DatabaseAdapter(cx);
	}
	
	//comment drafts CRUD
	public long createCommentDraft(int entityId, int categoryId, String content, boolean important) {
		da.open();
		long res = da.createCommentDraft(entityId, categoryId, content, important);
		da.close();
		
		return res;
	}
	
	public boolean updateCommentDraft(int entityId, int categoryId, String content, boolean important) {
		da.open();
		boolean res = da.updateCommentDraft(entityId, categoryId, content, important);
		da.close();
		
		return res;
	}
	
	public Comment getCommentDraft(int entityId, int categoryId) {
		Comment res = null;
		da.open();
		Cursor c = da.getCommentDraft(entityId, categoryId);
		if (c.moveToFirst()) {
			res = new Comment();
			res.setDescription(c.getString(c.getColumnIndex(CommentDrafts.DRAFT_CONTENT))); 
			res.setImportantTag(c.getInt(c.getColumnIndex(CommentDrafts.DRAFT_IMPORTANT))>0);
		}
		c.close();
		da.close();
		
		return res;
	}
	
	public int deleteCommentDraft(int entityId, int categoryId) {
		da.open();
		int res = da.deleteCommentDraft(entityId, categoryId);
		da.close();
		return res;
	}
	
	// response drafts CRUD
	public long createResponseDraft(int commentId, int categoryId, String content, boolean important) {
		da.open();
		long res = da.createResponseDraft(commentId, categoryId, content, important);
		da.close();
		return res;
	}
	
	public boolean updateResponseDraft(int commentId, int categoryId, String content, boolean important) {
		da.open();
		boolean res = da.updateResponseDraft(commentId, categoryId, content, important);
		da.close();
		
		return res;
	}
	
	public Comment getResponseDraft(int commentId, int categoryId) {
		Comment res = null;
		da.open();
		Cursor c = da.getResponseDraft(commentId, categoryId);
		if (c.moveToFirst()) {
			res = new Comment();
			res.setDescription(c.getString(c.getColumnIndex(ResponseDrafts.DRAFT_CONTENT))); 
			res.setImportantTag(c.getInt(c.getColumnIndex(ResponseDrafts.DRAFT_IMPORTANT))>0);
		}
		c.close();
		da.close();
		
		return res;
	}
	
	public int deleteResponseDraft(int commentId, int categoryId) {
		da.open();
		int res = da.deleteResponseDraft(commentId, categoryId);
		da.close();
		return res;
	}
}
