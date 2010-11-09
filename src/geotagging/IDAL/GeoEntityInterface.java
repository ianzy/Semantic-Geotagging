package geotagging.IDAL;

import geotagging.DES.Entity;

import java.sql.Date;
import java.util.List;

import com.google.android.maps.GeoPoint;

public interface GeoEntityInterface {
	List<Entity> getEntitiesByArea(GeoPoint p);
	List<Entity> getAllEntities();
	List<Entity> getLatestEntitiesByArea(Date date, GeoPoint p);
	List<Entity> getAllLatestEntities(Date date);
	void newEntity(Entity e);
}
