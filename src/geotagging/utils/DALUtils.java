package geotagging.utils;

import geotagging.DES.Comment;
import geotagging.DES.Entity;

import org.json.JSONException;
import org.json.JSONObject;

public class DALUtils {
	
	public static Entity getEntityFromJsonText(String jsonText) {
		
        if (jsonText == null)
        {
        	return null;
        }
      
        Entity ne = null;
//        String updateAt;
        try {
        	JSONObject entity = new JSONObject(jsonText);
			ne = new Entity();
            ne.setLat(entity.getJSONObject("entity").getString("lat"));
            ne.setLng(entity.getJSONObject("entity").getString("lng"));
            ne.setDescription(entity.getJSONObject("entity").getString("description"));
            ne.setLocation(entity.getJSONObject("entity").getString("location"));
            ne.setTitle(entity.getJSONObject("entity").getString("title"));
            ne.setId(entity.getJSONObject("entity").getInt("id"));
            ne.setIconURI(entity.getJSONObject("entity").getString("icon_uri"));
            
            ne.setUpdatedAt(entity.getJSONObject("entity").getString("updated_at"));
			return ne;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static Comment getCommentFromJsonText(String jsonText) {
		if (jsonText == null)
        {
        	return null;
        }
		
        Comment comment = null;
        try {
			JSONObject c = new JSONObject(jsonText);
			
			comment = new Comment();
            comment.setCommentId(c.getJSONObject("comment").getInt("id"));
            
            return comment;
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static Comment getResponseFromJsonText(String jsonText) {
		if (jsonText == null)
        {
        	return null;
        }
		
        Comment comment = null;
        try {
			JSONObject c = new JSONObject(jsonText);
			
			comment = new Comment();
            comment.setEntity_id(c.getJSONObject("comment").getInt("id"));
            comment.setCategory_id(c.getJSONObject("comment").getInt("category_id"));
            comment.setTime(c.getJSONObject("comment").getString("created_at"));
            return comment;
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
}
