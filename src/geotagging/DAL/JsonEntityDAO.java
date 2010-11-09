package geotagging.DAL;

import geotagging.DES.Entity;
import geotagging.IDAL.GeoEntityInterface;
import geotagging.app.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public class JsonEntityDAO implements GeoEntityInterface {

	private Context cx;
	
	public JsonEntityDAO(Context cx) {
		this.cx = cx;
	}
	
	public List<Entity> getAllEntities() {
		String jsonText = this.getJsonText(cx.getResources().getString(R.string.GeotaggingAPIUri));
		return getEntitiesFromJsonText(jsonText);
	}

	public List<Entity> getAllLatestEntities(Date date) {
		String jsonText = this.getJsonText(cx.getResources().getString(R.string.GeotaggingAPIUri));
		return getEntitiesFromJsonText(jsonText);
	}

	public List<Entity> getEntitiesByArea(GeoPoint p) {
		String jsonText = this.getJsonText(cx.getResources().getString(R.string.GeotaggingAPIUri));
		return getEntitiesFromJsonText(jsonText);
	}

	public List<Entity> getLatestEntitiesByArea(Date date, GeoPoint p) {
		String jsonText = this.getJsonText(cx.getResources().getString(R.string.GeotaggingAPIUri));
		return getEntitiesFromJsonText(jsonText);
	}

	public void newEntity(Entity e) {
		// TODO Auto-generated method stub

	}
	
	private List<Entity> getEntitiesFromJsonText(String jsonText) {
	
        if (jsonText == null)
        {
        	return null;
        }
        
        List<Entity> entitiesList = new ArrayList<Entity>();
        Entity ne = null;
        try {
			JSONArray entities = new JSONArray(jsonText);
			for (int i=0; i<entities.length() ;i++)
            {
				ne = new Entity();
                JSONObject entity = entities.getJSONObject(i);
                ne.setLat(entity.getJSONObject("entity").getString("lat"));
                ne.setLng(entity.getJSONObject("entity").getString("lng"));
                ne.setDescription(entity.getJSONObject("entity").getString("description"));
                ne.setLocation(entity.getJSONObject("entity").getString("location"));
                ne.setTitle(entity.getJSONObject("entity").getString("title"));
                entitiesList.add(ne);
            }
			
			return entitiesList;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	private String getJsonText(String api){
		URL url;
		BufferedReader rd;
		StringBuilder sb = new StringBuilder("");
		try {
			url = new URL(api);
			URLConnection conn = url.openConnection();
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        // Get the response
			String line = "";
            while ((line = rd.readLine()) != null) {
            	sb.append(line);
            }
            
            rd.close();
            return sb.toString();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.v("title","message for catch");
			e.printStackTrace();
			return null;
		}
	}

}
