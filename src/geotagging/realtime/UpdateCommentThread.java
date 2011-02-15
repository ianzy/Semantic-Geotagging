package geotagging.realtime;

import geotagging.DAL.GeoCommentDAL;
import geotagging.DES.Comment;
import geotagging.app.GeotaggingCommentsList;
import geotagging.provider.CacheBase;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

public class UpdateCommentThread extends Thread {

	private GeotaggingCommentsList commentList;
	private int entity_id;
	private int category_id;
	
	public UpdateCommentThread(GeotaggingCommentsList commentList, int entity_id, int category_id) {
		this.commentList = commentList;
		this.entity_id = entity_id;
		this.category_id = category_id;
	}
	
	@Override
	public void run() {
		this.prepareCachedCommentsList();
		this.prepareRemoteCommentsList();
	}
	
	private void prepareCachedCommentsList() {
		GeoCommentDAL commentsDAL = new GeoCommentDAL(commentList);
		List<Comment> commentsList = commentsDAL.getCachedCommentsByEntityIDAndCategoryID(entity_id, category_id);
        
		if(commentsList == null || commentsList.size() == 0 )
			return;
		//notify the observer to update the UI
		commentList.updateAdapter(commentsList);
	}
	
	private void prepareRemoteCommentsList() {
		
		SharedPreferences states = commentList.getSharedPreferences(CacheBase.PREFERENCE_FILENAME, Context.MODE_PRIVATE);
		int comment_id = states.getInt("latest_commentid", -1);
		int remote_count = states.getInt("remote_count", -1);
		if(-1 == comment_id) {
			
			return;
		}
		
		GeoCommentDAL commentsDAL = new GeoCommentDAL(commentList);
		List<Comment> commentsList = commentsDAL.getRemoteCommentsByEntityIDAndCategoryID(comment_id, entity_id, category_id, remote_count);
        
		if(commentsList == null || commentsList.size() == 0 )
			return;
		
		//notify the observer to update the UI
		commentList.updateAdapter(commentsList);
	}
}
