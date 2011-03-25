package geotagging.realtime;

import geotagging.DAL.GeoCategoryDAL;
import geotagging.DES.CommentCategory;
import geotagging.app.GeotaggingEntityInformation;

import java.util.List;

public class UpdateCommentCounterThread extends BaseThread {
	private GeotaggingEntityInformation cx;
	private int entityId;
	private GeoCategoryDAL counterDAL;
	
	public UpdateCommentCounterThread(GeotaggingEntityInformation cx, int entityId) {
		this.cx = cx;
		this.counterDAL = GeoCategoryDAL.getInstance();
		this.entityId = entityId;
	}
	
	@Override
	public void run() {
		List<CommentCategory> counters = counterDAL.getRemoteCommentCountersByEntityId(entityId);
		if(counters == null || counters.size() == 0 )
			return;
		cx.updateCounter(counters);
	}
}
