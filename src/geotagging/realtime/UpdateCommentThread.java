package geotagging.realtime;

import geotagging.DAL.GeoCommentDAL;
import geotagging.DES.Comment;
import geotagging.app.GeotaggingCommentsList;

import java.util.List;

public class UpdateCommentThread extends Thread {

	private GeotaggingCommentsList commentList;
	private String entity_id;
	
	public UpdateCommentThread(GeotaggingCommentsList commentList, String entity_id) {
		this.commentList = commentList;
		this.entity_id = entity_id;
	}
	
	@Override
	public void run() {
		this.prepareCommentsList();
	}
	
	private void prepareCommentsList() {
		GeoCommentDAL commentsDAL = new GeoCommentDAL(commentList);
		List<Comment> commentsList = commentsDAL.getCommentsByEntityID(entity_id);
        //notify the observer to update the UI
		commentList.updateAdapter(commentsList);
	}
}
