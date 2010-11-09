package geotagging.realtime;

import geotagging.DAL.JsonEntityDAO;
import geotagging.DES.Entity;
import geotagging.IDAL.GeoEntityInterface;
import geotagging.utils.GeotaggingItemizedOverlay;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class UpdateMapThread extends Thread {
	
	private Drawable d;
	private Context cx;
	private List<Overlay> mapOverlays;
	private GeotaggingItemizedOverlay itemizedoverlay;
	
	public UpdateMapThread (Drawable drawable, Context cx, List<Overlay> mapOverlays, GeotaggingItemizedOverlay itemizedoverlay) {
		this.d = drawable;
		this.cx = cx;
		this.mapOverlays = mapOverlays;
		this.itemizedoverlay = itemizedoverlay;
	}
	
	@Override
	public void run() {
		this.prepareMapOverlays(d, cx, mapOverlays);
	}
	
	private void prepareMapOverlays(Drawable drawable, Context cx, List<Overlay> mapOverlays) {
		GeoEntityInterface dao = new JsonEntityDAO(cx);
		List<Entity> list = dao.getAllEntities();
      
        if (list == null)
        {
        	return;
        }
        
        for (int i=0; i<list.size(); i++)
        {
            Entity entity = list.get(i);
            String lat = entity.getLat();
            String lng = entity.getLng();
            GeoPoint point = new GeoPoint(
                (int) (Double.parseDouble(lat) * 1E6), 
                (int) (Double.parseDouble(lng) * 1E6));
            
            
            OverlayItem overlayitem = new OverlayItem(point, "Opening Entity Information", "Opening Entity Information");
            itemizedoverlay.addOverlay(overlayitem);
            itemizedoverlay.addEntity(entity);
            
        }
        mapOverlays.add(itemizedoverlay);
	}
	
}
