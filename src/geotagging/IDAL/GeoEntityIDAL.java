package geotagging.IDAL;

import geotagging.DES.Entity;

import java.util.List;

import com.google.android.maps.GeoPoint;

public interface GeoEntityIDAL {
	List<Entity> getEntitiesByArea(GeoPoint p);
	List<Entity> getRemoteEntities(int entity_id, int count);
	List<Entity> getCachedEntities();
	List<Entity> getLatestEntitiesByArea(int entity_id, GeoPoint p);
	List<Entity> getAllLatestEntities(int entity_id);
	void newEntity(Entity e);
}
