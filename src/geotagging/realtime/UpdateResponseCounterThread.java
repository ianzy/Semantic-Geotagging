package geotagging.realtime;

import geotagging.DAL.GeoCategoryDAL;
import geotagging.DES.ResponseCategory;
import geotagging.app.GeotaggingFollowUpCategories;

import java.util.List;

public class UpdateResponseCounterThread extends BaseThread {

	private int commentId;
	private GeotaggingFollowUpCategories cx;
	private GeoCategoryDAL counterDAL;
	
	public UpdateResponseCounterThread(GeotaggingFollowUpCategories cx, int commentId) {
		this.commentId = commentId;
		this.cx = cx;
		this.counterDAL = new GeoCategoryDAL(cx);
	}
	@Override
	public void run() {
		List<ResponseCategory> counters = counterDAL.getRemoteResponseCountersByCommentId(commentId);
		if(counters == null || counters.size() == 0 )
			return;
		cx.updateCounter(counters);
	}
}
