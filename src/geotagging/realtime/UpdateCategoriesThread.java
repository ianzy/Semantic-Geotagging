package geotagging.realtime;

import geotagging.DAL.GeoCategoryDAL;

public class UpdateCategoriesThread extends BaseThread {

	private GeoCategoryDAL categoryDAL;
	
	public UpdateCategoriesThread() {
		categoryDAL = GeoCategoryDAL.getInstance();
	}
	
	@Override
	public void run() {
		//these method should be run once in the initialization
		categoryDAL.getRemoteCommentCategories();
		categoryDAL.getRemoteResponseCategories();
	}
	
	
}
