package geotagging.realtime;

import geotagging.DAL.GeoEntityDAL;
import geotagging.DES.Entity;
import geotagging.IDAL.GeoEntityIDAL;
import geotagging.provider.CacheBase;
import geotagging.utils.GeotaggingItemizedOverlay;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class UpdateMapThread extends Thread {
	
	private Drawable d;
	private Context cx;
	private List<Overlay> mapOverlays;
	private GeotaggingItemizedOverlay itemizedoverlay;
	private GeoEntityIDAL dao;
	
	public UpdateMapThread (Drawable drawable, Context cx, List<Overlay> mapOverlays, GeotaggingItemizedOverlay itemizedoverlay) {
		this.d = drawable;
		this.cx = cx;
		this.mapOverlays = mapOverlays;
		this.itemizedoverlay = itemizedoverlay;
		this.dao = new GeoEntityDAL(cx);
	}
	
	@Override
	public void run() {
		this.prepareMapOverlaysFromCachedData(d, cx, mapOverlays);
		this.prepareMapOverlaysFromRemoteData(d, cx, mapOverlays);
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
            
           
//            	Drawable d = cx.getResources().getDrawable(R.drawable.amphitheater);
//	            d.setBounds(-10, -20, d.getIntrinsicWidth()-10, d.getIntrinsicHeight()-20);
//	            overlayitem.setMarker(d);
            
//            Drawable d = cx.getResources().getDrawable(R.drawable.androidmarker);
//            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
//            overlayitem.setMarker(d);
            itemizedoverlay.addOverlay(overlayitem);
            itemizedoverlay.addEntity(entity);
            
        }
        mapOverlays.add(itemizedoverlay);
	}
}
