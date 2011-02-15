package geotagging.realtime;

import geotagging.DAL.GeoCategoryDAL;
import android.content.Context;

public class UpdateCategoriesThread extends Thread {

	private GeoCategoryDAL categoryDAL;
	
	public UpdateCategoriesThread(Context cx) {
		categoryDAL = new GeoCategoryDAL(cx);
	}
	
	@Override
	public void run() {
		//these method should be run once in the initialization
		categoryDAL.getRemoteCommentCategories();
		categoryDAL.getRemoteResponseCategories();
	}
	
	
}
