package geotagging.DAL;

import geotagging.DES.Entity;
import geotagging.IDAL.GeoEntityInterface;
import geotagging.app.R;
import geotagging.utils.BackendHelperSingleton;

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
	private BackendHelperSingleton helper;
	
	public JsonEntityDAO(Context cx) {
		this.cx = cx;
		helper = BackendHelperSingleton.getInstance();
	}
	
	public List<Entity> getAllEntities() {
		String jsonText = helper.getJsonText(cx.getResources().getString(R.string.GeotaggingAPIUri));
		return getEntitiesFromJsonText(jsonText);
	}

	public List<Entity> getAllLatestEntities(Date date) {
		String jsonText = helper.getJsonText(cx.getResources().getString(R.string.GeotaggingAPIUri));
		return getEntitiesFromJsonText(jsonText);
	}

	public List<Entity> getEntitiesByArea(GeoPoint p) {
		String jsonText = helper.getJsonText(cx.getResources().getString(R.string.GeotaggingAPIUri));
		return getEntitiesFromJsonText(jsonText);
	}

	public List<Entity> getLatestEntitiesByArea(Date date, GeoPoint p) {
		String jsonText = helper.getJsonText(cx.getResources().getString(R.string.GeotaggingAPIUri));
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
        String updateAt;
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
                ne.setId(entity.getJSONObject("entity").getInt("id"));
                
                updateAt = entity.getJSONObject("entity").getString("updated_at").replace("T", " ").replace("Z", "").replace("-", "/");
                Log.i("updatedAT-----------------", updateAt);
                ne.setUpdatedAt(Date.parse(updateAt));
                Log.i("updatedAT-----------------", String.valueOf(Date.parse(updateAt)));
                entitiesList.add(ne);
            }
			
			return entitiesList;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

}
