package geotagging.realtime;

import geotagging.DAL.GeoCommentDAL;
import geotagging.DES.Comment;
import geotagging.app.GeotaggingCommentDetails;
import geotagging.provider.CacheBase;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

public class UpdateFollowupCommentThread extends BaseThread {

	private GeotaggingCommentDetails commentList;
	private int comment_id;
	private int mode;
	
	public UpdateFollowupCommentThread(GeotaggingCommentDetails commentList, int comment_id, int mode) {
		this.commentList = commentList;
		this.comment_id = comment_id;
		this.mode = mode;
	}
	
	@Override
	public void run() {
		switch(this.mode) {
		case INITIALIZE_MODE:
			this.prepareCachedCommentsList();
			this.prepareRemoteCommentsList();
			break;
		case SYNC_MODE:
			this.prepareRemoteCommentsList();
			break;
		}
		
	}
	
	private void prepareCachedCommentsList() {
		GeoCommentDAL commentsDAL = new GeoCommentDAL(commentList);
		List<Comment> commentsList = commentsDAL.getCachedFollowUpCommentsByCommentId(comment_id);
        
		if(commentsList == null || commentsList.size() == 0 )
			return;
		//notify the observer to update the UI
		commentList.updateAdapter(commentsList, this.mode);
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
        
		if(commentsList == null || commentsList.size() == 0 ) {
			if(this.mode == SYNC_MODE)
				commentList.updateAdapter(commentsList, this.mode);
			return;
		}
		
		//notify the observer to update the UI
		commentList.updateAdapter(commentsList, this.mode);
	}
}
