package geotagging.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class GeotaggingMap extends MapActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geotagging_map);
        
        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        MapController mc = mapView.getController();
        String coordinates[] = {"37.409948", "-122.059822"};
        double lat = Double.parseDouble(coordinates[0]);
        double lng = Double.parseDouble(coordinates[1]);
 
        GeoPoint p = new GeoPoint(
            (int) (lat * 1E6), 
            (int) (lng * 1E6));
 
        mc.animateTo(p);
        mc.setZoom(17);
        
        List<Overlay> mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.amphitheater);
        
        this.prepareMapOverlays(drawable, this, mapOverlays);
        
        GeotaggingMapWithOverlay itemizedoverlay = new GeotaggingMapWithOverlay(drawable,this);
        GeoPoint point = new GeoPoint(19240000,-99120000);
        OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "I'm in Mexico City!");
        GeoPoint point2 = new GeoPoint(35410000, 139460000);
        OverlayItem overlayitem2 = new OverlayItem(point2, "", "");
        itemizedoverlay.addOverlay(overlayitem);
        itemizedoverlay.addOverlay(overlayitem2);
        mapOverlays.add(itemizedoverlay);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	private void prepareMapOverlays(Drawable drawable, Context cx, List<Overlay> mapOverlays){
		try
        {
            String jsontext = this.getJsonText();
            JSONArray entities = new JSONArray(jsontext);
            for (int i=0; i<entities.length() ;i++)
            {
                JSONObject entity = entities.getJSONObject(i);
                String lat = entity.getJSONObject("entity").getString("lat");
                String lng = entity.getJSONObject("entity").getString("lng");
                GeoPoint point = new GeoPoint(
                    (int) (Double.parseDouble(lat) * 1E6), 
                    (int) (Double.parseDouble(lng) * 1E6));
                
                GeotaggingMapWithOverlay itemizedoverlay = new GeotaggingMapWithOverlay(drawable,cx);
                OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "I'm in Mexico City!");
                itemizedoverlay.addOverlay(overlayitem);
                mapOverlays.add(itemizedoverlay);
            }
            
        }
        catch (Exception je)
        {
        	Log.i("Error ", je.getMessage());
        }
        
	}
	
	private String getJsonText(){
		URL url;
		StringBuilder sb = new StringBuilder("");
		try {
			url = new URL(this.getString(R.string.GeotaggingAPIUri));
			URLConnection conn = url.openConnection();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        // Get the response
			String line = "";
            while ((line = rd.readLine()) != null) {
            	sb.append(line);
            }
            return sb.toString();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
