package geotagging.DAL;

import geotagging.DES.CommentCategory;
import geotagging.DES.ResponseCategory;
import geotagging.IDAL.GeoCategoryIDAL;
import geotagging.app.R;
import geotagging.provider.DatabaseAdapter;
import geotagging.provider.CacheBase.CommentCounters;
import geotagging.provider.CacheBase.ResponseCounters;
import geotagging.utils.BackendHelperSingleton;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;

public class GeoCategoryDAL implements GeoCategoryIDAL {

	private static Context cx;
	
	private BackendHelperSingleton helper;
	private DatabaseAdapter da;
	
	public static void setContext(Context cx) {
		GeoCategoryDAL.cx = cx;
	}
	
	/**
	* SingletonHolder is loaded on the first execution of Singleton.getInstance() 
	* or the first access to SingletonHolder.INSTANCE, not before.
	*/
   private static class SingletonHolder { 
     public static final GeoCategoryDAL INSTANCE = new GeoCategoryDAL(GeoCategoryDAL.cx);
   }
 
   public static GeoCategoryDAL getInstance() {
     return SingletonHolder.INSTANCE;
   }
	
	protected GeoCategoryDAL(Context cx) {
        this.helper = BackendHelperSingleton.getInstance();
        this.da = new DatabaseAdapter(cx);
	}
	
	public List<CommentCategory> getCommentCategoriesByEntityId(int entityId) {
		da.open();
		Cursor cursor = da.getCommentCounters(entityId);
		//need to be refactored, kind of a hack, create the entry if not exist
		if(!cursor.moveToFirst()) {
			cursor.close();
			da.createCommentCategoriesCounter(entityId);
			cursor = da.getCommentCounters(entityId);
		}
		
		List<CommentCategory> categories = new ArrayList<CommentCategory>();
		CommentCategory category;
		if(cursor.moveToFirst()) {
			do {
				category = new CommentCategory();
                
				category.setCategory_id(cursor.getInt(cursor.getColumnIndex(CommentCounters.COUNTER_CATEGORYID)));
				category.setCount(cursor.getInt(cursor.getColumnIndex(CommentCounters.COUNTER_COUNTER)));
				category.setName(cursor.getString(cursor.getColumnIndex(CommentCounters.COUNTER_CATEGORY_NAME)));
				category.setImportanTag(cursor.getInt(cursor.getColumnIndex(CommentCounters.CATEGORY_IMPORTANT_TAG)) > 0);
				categories.add(category);
			} while(cursor.moveToNext());
			cursor.close();
			da.close();
			return categories;
		} else {
			cursor.close();
			da.close();
			return null;
		}
	}

	public List<ResponseCategory> getResponseCategoriesByCommentId(int commentId) {
		da.open();
		Cursor cursor = da.getResponseCounters(commentId);
		//need to be refactored, kind of a hack, create the entry if not exist
		if(!cursor.moveToFirst()) {
			cursor.close();
			da.createResponseCategoriesCounter(commentId);
			cursor = da.getResponseCounters(commentId);
		}
		
		List<ResponseCategory> categories = new ArrayList<ResponseCategory>();
		ResponseCategory category;
		if(cursor.moveToFirst()) {
			do {
				category = new ResponseCategory();
                
				category.setCategory_id(cursor.getInt(cursor.getColumnIndex(ResponseCounters.COUNTER_CATEGORYID)));
				category.setCount(cursor.getInt(cursor.getColumnIndex(ResponseCounters.COUNTER_COUNTER)));
				category.setName(cursor.getString(cursor.getColumnIndex(ResponseCounters.COUNTER_CATEGORY_NAME)));
				category.setImportanTag(cursor.getInt(cursor.getColumnIndex(ResponseCounters.CATEGORY_IMPORTANT_TAG)) > 0);
				categories.add(category);
			} while(cursor.moveToNext());
			cursor.close();
			da.close();
			return categories;
		} else {
			cursor.close();
			da.close();
			return null;
		}
	}

	public int getRemoteCommentCategories() {
		String apiurl = cx.getResources().getString(R.string.GeotaggingAPIGetCommentCategories);
		String jsonText = helper.getJsonText(apiurl);
		return getCommentCategoriesFromJsonText(jsonText);
	}

	public int getRemoteResponseCategories() {
		String apiurl = cx.getResources().getString(R.string.GeotaggingAPIGetResponseCategories);
		String jsonText = helper.getJsonText(apiurl);
		return getResponseCategoriesFromJsonText(jsonText);
	}

	private int getCommentCategoriesFromJsonText(String jsonText) {
    	
        if (jsonText == null)
        {
        	return -1;
        }
        
        List<CommentCategory> categoriesList = new ArrayList<CommentCategory>();
        CommentCategory category = null;
        try {
			JSONArray categories = new JSONArray(jsonText);
			for (int i=0; i<categories.length() ;i++)
            {
				category = new CommentCategory();
                JSONObject c = categories.getJSONObject(i);
                category.setCategory_id(c.getJSONObject("comment_category").getInt("id"));
                category.setName(c.getJSONObject("comment_category").getString("name"));
                
                categoriesList.add(category);
            }
			
			da.open();
			da.createCommentCategories(categoriesList);
			da.close();
			
			return 0;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		
	}

	private int getResponseCategoriesFromJsonText(String jsonText) {
		
	    if (jsonText == null)
	    {
	    	return -1;
	    }
	    
	    List<ResponseCategory> categoriesList = new ArrayList<ResponseCategory>();
	    ResponseCategory category = null;
	    try {
			JSONArray categories = new JSONArray(jsonText);
			for (int i=0; i<categories.length() ;i++)
	        {
				category = new ResponseCategory();
	            JSONObject c = categories.getJSONObject(i);
	            category.setCategory_id(c.getJSONObject("response_category").getInt("id"));
	            category.setName(c.getJSONObject("response_category").getString("name"));
	            
	            categoriesList.add(category);
	        }
			
			da.open();
			da.createResponseCategories(categoriesList);
			da.close();
			
			return 0;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		
	}

	public int createCommentCounters(int entityId) {
		da.open();
		da.createCommentCategoriesCounter(entityId);
		da.close();
		return 0;
	}

	public int createResponseCounters(int commentId) {
		da.open();
		da.createResponseCategoriesCounter(commentId);
		da.close();
		return 0;
	}
	
	public List<CommentCategory> getRemoteCommentCountersByEntityId(int entityId) {
		String apiurl = cx.getResources().getString(R.string.GeotaggingAPIGetCommentCategoriesCounters)+"?entity_id="+String.valueOf(entityId);
		String jsonText = helper.getJsonText(apiurl);
		
		if (jsonText == null)
	    {
	    	return null;
	    }
	    
		List<CommentCategory> categoriesList = new ArrayList<CommentCategory>();
        CommentCategory category = null;
        try {
			JSONArray categories = new JSONArray(jsonText);
			for (int i=0; i<categories.length() ;i++)
            {
				category = new CommentCategory();
                JSONObject c = categories.getJSONObject(i);
                category.setCategory_id(c.getJSONObject("entity_category_counter").getInt("comment_category_id"));
                category.setCount(c.getJSONObject("entity_category_counter").getInt("counter"));
                category.setImportanTag(c.getJSONObject("entity_category_counter").getBoolean("important_tag"));
                
                categoriesList.add(category);
            }
			
			return categoriesList;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public List<ResponseCategory> getRemoteResponseCountersByCommentId(int commentId) {
		String apiurl = cx.getResources().getString(R.string.GeotaggingAPIGetResponseCategoriesCounters)+"?comment_id="+String.valueOf(commentId);
		String jsonText = helper.getJsonText(apiurl);
		
		if (jsonText == null)
	    {
	    	return null;
	    }
	    
	    List<ResponseCategory> categoriesList = new ArrayList<ResponseCategory>();
	    ResponseCategory category = null;
	    try {
			JSONArray categories = new JSONArray(jsonText);
			for (int i=0; i<categories.length() ;i++)
	        {
				category = new ResponseCategory();
	            JSONObject c = categories.getJSONObject(i);
	            category.setCategory_id(c.getJSONObject("comment_category_counter").getInt("response_category_id"));
                category.setCount(c.getJSONObject("comment_category_counter").getInt("counter"));
                category.setImportanTag(c.getJSONObject("comment_category_counter").getBoolean("important_tag"));
	            
	            categoriesList.add(category);
	        }
			
			return categoriesList;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
