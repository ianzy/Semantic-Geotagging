package geotagging.DAL;

import geotagging.DES.Entity;
import geotagging.IDAL.GeoEntityIDAL;
import geotagging.app.R;
import geotagging.provider.CacheBase;
import geotagging.provider.DatabaseAdapter;
import geotagging.provider.CacheBase.Entities;
import geotagging.utils.BackendHelperSingleton;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.google.android.maps.GeoPoint;

public class GeoEntityDAL implements GeoEntityIDAL {

	private Context cx;
	private BackendHelperSingleton helper;
	private DatabaseAdapter da;
	
	public GeoEntityDAL(Context cx) {
		this.cx = cx;
		this.helper = BackendHelperSingleton.getInstance();
		this.da = new DatabaseAdapter(cx);
	}
	
	public List<Entity> getRemoteEntities(int entity_id, int count) {
		String jsonText = helper.getJsonText(cx.getResources().getString(R.string.GeotaggingAPIGetEntities)+"?since_id="+String.valueOf(entity_id)+"&count="+String.valueOf(count));
		return getEntitiesFromJsonText(jsonText);
	}
	
	public List<Entity> getCachedEntities() {
		da.open();
		Cursor cursor = da.getAllEntities();
		List<Entity> entitiesList = new ArrayList<Entity>();
		Entity entity;
		if(cursor.moveToFirst()) {
			do {
				entity = new Entity();
                
				entity.setLat(String.valueOf(cursor.getFloat(cursor.getColumnIndex(Entities.ENTITY_LAT))));
				entity.setLng(String.valueOf(cursor.getFloat(cursor.getColumnIndex(Entities.ENTITY_LNG))));
				entity.setDescription(cursor.getString(cursor.getColumnIndex(Entities.ENTITY_DESCRIPTION)));
				entity.setLocation(cursor.getString(cursor.getColumnIndex(Entities.ENTITY_LOCATION)));
				entity.setTitle(cursor.getString(cursor.getColumnIndex(Entities.ENTITY_TITLE)));
				entity.setId(cursor.getInt(cursor.getColumnIndex(Entities.ENTITY_ID)));
				entity.setIconURI(cursor.getString(cursor.getColumnIndex(Entities.ENTITY_ICONURL)));
                entity.setUpdatedAt(cursor.getString(cursor.getColumnIndex(Entities.ENTITY_UPDATEDAT)));

                entitiesList.add(entity);
			} while(cursor.moveToNext());
			cursor.close();
			da.close();
			return entitiesList;
		} else {
			cursor.close();
			da.close();
			return null;
		}
	}

	public List<Entity> getAllLatestEntities(int entity_id) {
//		String jsonText = helper.getJsonText(cx.getResources().getString(R.string.GeotaggingAPIUri));
//		return getEntitiesFromJsonText(jsonText);
		return null;
	}

	public List<Entity> getEntitiesByArea(GeoPoint p) {
//		String jsonText = helper.getJsonText(cx.getResources().getString(R.string.GeotaggingAPIUri));
//		return getEntitiesFromJsonText(jsonText);
		return null;
	}

	public List<Entity> getLatestEntitiesByArea(int entity_id, GeoPoint p) {
//		String jsonText = helper.getJsonText(cx.getResources().getString(R.string.GeotaggingAPIUri));
//		return getEntitiesFromJsonText(jsonText);
		return null;
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
        int latestEntityId = -1;
//        String updateAt;
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
                ne.setIconURI(entity.getJSONObject("entity").getString("icon_uri"));
                
                ne.setUpdatedAt(entity.getJSONObject("entity").getString("updated_at"));
//                updateAt = entity.getJSONObject("entity").getString("updated_at").replace("T", " ").replace("Z", "").replace("-", "/");
//                Log.i("updatedAT-----------------", updateAt);
//                ne.setUpdatedAt(Date.parse(updateAt));
//                Log.i("updatedAT-----------------", String.valueOf(Date.parse(updateAt)));
                entitiesList.add(ne);
                if(ne.getId() > latestEntityId)
                	latestEntityId = ne.getId();
            }
			
			if(entitiesList.size() == 0) {
				return null;
			} else {
				da.open();
				da.createEntities(entitiesList);
				da.close();
				
				SharedPreferences states = cx.getSharedPreferences(CacheBase.PREFERENCE_FILENAME, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = states.edit();
				editor.putInt("latest_entityid", latestEntityId);
				editor.commit();
				
				return entitiesList;
			}
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

}
