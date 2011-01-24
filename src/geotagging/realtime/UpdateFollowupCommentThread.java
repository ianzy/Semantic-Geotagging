package geotagging.realtime;

import geotagging.DAL.GeoCommentDAL;
import geotagging.DES.Comment;
import geotagging.app.GeotaggingCommentDetails;

import java.util.List;

public class UpdateFollowupCommentThread extends Thread {

	private GeotaggingCommentDetails commentList;
	private int comment_id;
	
	public UpdateFollowupCommentThread(GeotaggingCommentDetails commentList, int comment_id) {
		this.commentList = commentList;
		this.comment_id = comment_id;
	}
	
	@Override
	public void run() {
		this.prepareCommentsList();
	}
	
	private void prepareCommentsList() {
		GeoCommentDAL commentsDAL = new GeoCommentDAL(commentList);
		List<Comment> commentsList = commentsDAL.getCommentsByCommentId(comment_id);
        //notify the observer to update the UI
		commentList.updateAdapter(commentsList);
	}
}
