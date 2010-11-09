package geotagging.IDAL;

import geotagging.DES.Response;

import java.util.List;

public interface GeoResponseIDAL {
	List<Response> getResponsesByEntityID(int entity_id);
	List<Response> getLatestTenResponses(int entity_id);
	List<Response> getLatestResponsesByTime(String time);
	void newResponse(int entity_id);
}
