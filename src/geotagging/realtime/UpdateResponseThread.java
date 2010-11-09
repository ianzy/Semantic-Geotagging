package geotagging.realtime;

import geotagging.DAL.GeoResponseDAL;
import geotagging.DES.Response;
import geotagging.app.GeotaggingResponseList;

import java.util.List;

public class UpdateResponseThread extends Thread {
	
	private GeotaggingResponseList listView;
	private String entity_id;
	
	public UpdateResponseThread(GeotaggingResponseList listView, String entity_id) {
		this.listView = listView;
		this.entity_id = entity_id;
	}
	
	@Override
	public void run() {
		this.prepareResponsesList();
	}
	
	private void prepareResponsesList() {
		GeoResponseDAL responseDAL = new GeoResponseDAL(listView, entity_id);
		List<Response> respsList = responseDAL.getLatestResponsesByTime("2010-09-10%08:00:00");
        
        listView.updateAdapter(respsList);
	}
}
