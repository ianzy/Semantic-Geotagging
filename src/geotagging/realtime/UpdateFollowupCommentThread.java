package geotagging.realtime;

import geotagging.DAL.GeoCommentDAL;
import geotagging.DES.Comment;
import geotagging.app.GeotaggingCommentDetails;
import geotagging.provider.CacheBase;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

public class UpdateFollowupCommentThread extends Thread {

	private GeotaggingCommentDetails commentList;
	private int comment_id;
	
	public UpdateFollowupCommentThread(GeotaggingCommentDetails commentList, int comment_id) {
		this.commentList = commentList;
		this.comment_id = comment_id;
	}
	
	@Override
	public void run() {
		this.prepareCachedCommentsList();
		this.prepareRemoteCommentsList();
	}
	
	private void prepareCachedCommentsList() {
		GeoCommentDAL commentsDAL = new GeoCommentDAL(commentList);
		List<Comment> commentsList = commentsDAL.getCachedFollowUpCommentsByCommentId(comment_id);
        
		if(commentsList == null || commentsList.size() == 0 )
			return;
		//notify the observer to update the UI
		commentList.updateAdapter(commentsList);
	}
	
	private void prepareRemoteCommentsList() {
		SharedPreferences states = commentList.getSharedPreferences(CacheBase.PREFERENCE_FILENAME, Context.MODE_PRIVATE);
		int since_id = states.getInt("latest_responseid", -1);
		int remote_count = states.getInt("remote_count", -1);
		if(-1 == since_id) {
			
			return;
		}
		GeoCommentDAL commentsDAL = new GeoCommentDAL(commentList);
		List<Comment> commentsList = commentsDAL.getRemoteFollowUpCommentsByCommentId(since_id, comment_id, remote_count);
        
		if(commentsList == null || commentsList.size() == 0 )
			return;
		
		//notify the observer to update the UI
		commentList.updateAdapter(commentsList);
	}
}
