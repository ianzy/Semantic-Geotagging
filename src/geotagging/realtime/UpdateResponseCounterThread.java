package geotagging.realtime;

import geotagging.DAL.GeoCategoryDAL;
import geotagging.DES.ResponseCategory;
import geotagging.app.IResponseCounter;

import java.util.List;

public class UpdateResponseCounterThread extends BaseThread {

	private int commentId;
	private IResponseCounter cx;
	private GeoCategoryDAL counterDAL;
	
	public UpdateResponseCounterThread(IResponseCounter cx, int commentId) {
		this.commentId = commentId;
		this.cx = cx;
		this.counterDAL = GeoCategoryDAL.getInstance();
	}
	@Override
	public void run() {
		List<ResponseCategory> counters = counterDAL.getRemoteResponseCountersByCommentId(commentId);
		if(counters == null || counters.size() == 0 )
			return;
		cx.updateCounter(counters);
	}
}
