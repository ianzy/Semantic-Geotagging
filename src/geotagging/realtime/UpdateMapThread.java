package geotagging.realtime;

import geotagging.DAL.GeoEntityDAL;
import geotagging.DES.Entity;
import geotagging.IDAL.GeoEntityIDAL;
import geotagging.app.GeotaggingMap;
import geotagging.provider.CacheBase;
import geotagging.utils.GeotaggingItemizedOverlay;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class UpdateMapThread extends BaseThread {
	
	private Drawable d;
	private GeotaggingMap cx;
	private List<Overlay> mapOverlays;
	private GeotaggingItemizedOverlay itemizedoverlay;
	private GeoEntityIDAL dao;
	private int mode;
	
	public UpdateMapThread (Drawable drawable, GeotaggingMap cx, List<Overlay> mapOverlays, GeotaggingItemizedOverlay itemizedoverlay, int mode) {
		this.d = drawable;
		this.cx = cx;
		this.mapOverlays = mapOverlays;
		this.itemizedoverlay = itemizedoverlay;
		this.dao = new GeoEntityDAL(cx);
		this.mode = mode;
	}
	
	@Override
	public void run() {
		switch(mode) {
		case INITIALIZE_MODE:
			this.prepareMapOverlaysFromCachedData(d, cx, mapOverlays);
			this.prepareMapOverlaysFromRemoteData(d, cx, mapOverlays);
			break;
		case SYNC_MODE:
			this.updateMapOverlaysFromRemoteData(d, cx, mapOverlays);
			cx.dismissProgressBar();
			break;
		}
		
	}
	
	private void prepareMapOverlaysFromCachedData(Drawable drawable, Context cx, List<Overlay> mapOverlays) {
		List<Entity> list = dao.getCachedEntities();
      
        if (list == null)
        {
        	return;
        }
        
        prepareMapOverlays(list);
	}
	
	private void prepareMapOverlaysFromRemoteData(Drawable drawable, Context cx, List<Overlay> mapOverlays) {
		SharedPreferences states = cx.getSharedPreferences(CacheBase.PREFERENCE_FILENAME, Context.MODE_PRIVATE);
		int entity_id = states.getInt("latest_entityid", -1);
		int remote_count = states.getInt("remote_count", -1);
		if(-1 == entity_id) {
			
			return;
		}
			
		List<Entity> list = dao.getRemoteEntities(entity_id, remote_count);
	      
        if (list == null)
        {
        	return;
        }
        
        prepareMapOverlays(list);
	}
	
	private void prepareMapOverlays(List<Entity> list) {
		for (int i=0; i<list.size(); i++)
        {
            Entity entity = list.get(i);
            String lat = entity.getLat();
            String lng = entity.getLng();
            GeoPoint point = new GeoPoint(
                (int) (Double.parseDouble(lat) * 1E6), 
                (int) (Double.parseDouble(lng) * 1E6));
            
            
            OverlayItem overlayitem = new OverlayItem(point, "Opening Entity Information", "Opening Entity Information");
            
            // specify each icon, using hard coded data, needs to be refactored
            for(int ii=0; ii<GeotaggingMap.ICONNAMES.length; ii++) {
            	if(GeotaggingMap.ICONNAMES[ii].equals(entity.getIconURI())) {
            		Drawable d = cx.getResources().getDrawable(GeotaggingMap.DRAWABLES[ii]);
                    d.setBounds(-10, -20, d.getIntrinsicWidth()-10, d.getIntrinsicHeight()-20);
                    overlayitem.setMarker(d);
            		break;
            	}
            }
        	
            
//            Drawable d = cx.getResources().getDrawable(R.drawable.androidmarker);
//            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
//            overlayitem.setMarker(d);
            itemizedoverlay.addOverlay(overlayitem);
            itemizedoverlay.addEntity(entity);
            
        }
        mapOverlays.add(itemizedoverlay);
	}
	
	private void updateMapOverlaysFromRemoteData(Drawable drawable, Context cx, List<Overlay> mapOverlays) {
		SharedPreferences states = cx.getSharedPreferences(CacheBase.PREFERENCE_FILENAME, Context.MODE_PRIVATE);
		int entity_id = states.getInt("latest_entityid", -1);
		int remote_count = states.getInt("remote_count", -1);
		if(-1 == entity_id) {
			
			return;
		}
			
		List<Entity> list = dao.getRemoteEntities(entity_id, remote_count);
	      
        if (list == null)
        {
        	return;
        }
        
        updateMapOverlays(list);
	}
	
	private void updateMapOverlays(List<Entity> list) {
		boolean existflag;
		for (int i=0; i<list.size(); i++)
        {
			existflag = false;
            Entity entity = list.get(i);
            String lat = entity.getLat();
            String lng = entity.getLng();
            GeoPoint point = new GeoPoint(
                (int) (Double.parseDouble(lat) * 1E6), 
                (int) (Double.parseDouble(lng) * 1E6));
            
            
            OverlayItem overlayitem = new OverlayItem(point, "Opening Entity Information", "Opening Entity Information");
            
            // specify each icon, using hard coded data, needs to be refactored
            for(int ii=0; ii<GeotaggingMap.ICONNAMES.length; ii++) {
            	if(GeotaggingMap.ICONNAMES[ii].equals(entity.getIconURI())) {
            		Drawable d = cx.getResources().getDrawable(GeotaggingMap.DRAWABLES[ii]);
                    d.setBounds(-10, -20, d.getIntrinsicWidth()-10, d.getIntrinsicHeight()-20);
                    overlayitem.setMarker(d);
            		break;
            	}
            }
        	
            List<Entity> existingEntites = itemizedoverlay.getEntities();
            for(int ii=0; ii<existingEntites.size(); ii++) {
            	if(entity.getId() == existingEntites.get(ii).getId()) {
            		existflag = true;
            		break;
            	}
            		
            }
            
            if(existflag) {
            	continue;
            }
            itemizedoverlay.addOverlay(overlayitem);
            itemizedoverlay.addEntity(entity);
            
        }
        mapOverlays.add(itemizedoverlay);
	}
}
