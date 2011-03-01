package geotagging.DAL;

import geotagging.DES.Comment;
import geotagging.DES.CommentCategory;
import geotagging.DES.ResponseCategory;
import geotagging.IDAL.GeoCommentIDAL;
import geotagging.app.R;
import geotagging.provider.CacheBase;
import geotagging.provider.DatabaseAdapter;
import geotagging.provider.CacheBase.Comments;
import geotagging.provider.CacheBase.Responses;
import geotagging.utils.BackendHelperSingleton;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class GeoCommentDAL implements GeoCommentIDAL {

	private Context cx;
	private BackendHelperSingleton helper;
	private DatabaseAdapter da;
	
	public GeoCommentDAL(Context cx) {
		this.cx = cx;
        this.helper = BackendHelperSingleton.getInstance();
        this.da = new DatabaseAdapter(cx);
	}
	
	//set of methods to get comment around a entity thread
	public List<Comment> getCachedCommentsByEntityIDAndCategoryID(int entity_id, int category_id) {
		da.open();
		Cursor cursor = da.getComments(entity_id, category_id);
		List<Comment> commentsList = new ArrayList<Comment>();
		Comment comment;
		if(cursor.moveToFirst()) {
			do {
				comment = new Comment();
                
				comment.setCategory_id(cursor.getInt(cursor.getColumnIndex(Comments.COMMENT_CATEGORYID)));
				comment.setCommentId(cursor.getInt(cursor.getColumnIndex(Comments.COMMENT_ID)));
				comment.setCommentImg(cursor.getString(cursor.getColumnIndex(Comments.COMMENT_IMG)));
				comment.setDescription(cursor.getString(cursor.getColumnIndex(Comments.COMMENT_DESCRIPTION)));
				comment.setEntity_id(cursor.getInt(cursor.getColumnIndex(Comments.COMMENT_ENTITYID)));
				comment.setTime(cursor.getString(cursor.getColumnIndex(Comments.COMMENT_TIME)));
				comment.setUserImg(cursor.getString(cursor.getColumnIndex(Comments.COMMENT_USERIMG)));
				comment.setUserName(cursor.getString(cursor.getColumnIndex(Comments.COMMENT_USERNAME)));
				
				//**********************attention!*************************
				Drawable d = cx.getResources().getDrawable(R.drawable.default_user_icon);
				comment.setActualUserImg(((BitmapDrawable)d).getBitmap());
                commentsList.add(comment);
			} while(cursor.moveToNext());
			cursor.close();
			da.close();
			return commentsList;
		} else {
			cursor.close();
			da.close();
			return null;
		}
	}
	
	public List<Comment> getRemoteCommentsByEntityIDAndCategoryID(int since_id, int entity_id, int category_id, int count) {
		String apiurl = cx.getResources().getString(R.string.GeotaggingAPIGetComments)+
			"?since_id="+String.valueOf(since_id)+
			"&entity_id="+String.valueOf(entity_id)+
			"&count="+String.valueOf(count);
		String jsonText = helper.getJsonText(apiurl);
		return getCommentsFromJsonText(jsonText, entity_id, category_id);
	}
	
	//set of functions to get follow up responses of a particular comment
	//may be this should be seperated to a new class and interface
	public List<Comment> getCachedFollowUpCommentsByCommentId(int commentId) {
		da.open();
		Cursor cursor = da.getResponses(commentId);
		List<Comment> commentsList = new ArrayList<Comment>();
		Comment comment;
		if(cursor.moveToFirst()) {
			do {
				comment = new Comment();
                
				comment.setCategory_id(cursor.getInt(cursor.getColumnIndex(Responses.RESPONSE_CATEGORYID)));
				comment.setCommentId(cursor.getInt(cursor.getColumnIndex(Responses.RESPONSE_COMMENTID)));
				comment.setDescription(cursor.getString(cursor.getColumnIndex(Responses.RESPONSE_DESCRIPTION)));
				comment.setTime(cursor.getString(cursor.getColumnIndex(Responses.RESPONSE_TIME)));
				comment.setUserName(cursor.getString(cursor.getColumnIndex(Responses.RESPONSE_USERNAME)));
				comment.setUserImg(cursor.getString(cursor.getColumnIndex(Responses.RESPONSE_USERIMG)));
				comment.setEntity_id(cursor.getInt(cursor.getColumnIndex(Responses.RESPONSE_ID)));
				
				//**********************attention!*************************
				Drawable d = cx.getResources().getDrawable(R.drawable.default_user_icon);
				comment.setActualUserImg(((BitmapDrawable)d).getBitmap());
                commentsList.add(comment);
			} while(cursor.moveToNext());
			cursor.close();
			da.close();
			return commentsList;
		} else {
			cursor.close();
			da.close();
			return null;
		}
	}
	
	public List<Comment> getRemoteFollowUpCommentsByCommentId(int since_id, int commentId, int count) {
		String apiurl = cx.getResources().getString(R.string.GeotaggingAPIGetResponses)+
			"?since_id="+String.valueOf(since_id)+
			"&comment_id="+String.valueOf(commentId)+
			"&count="+String.valueOf(count);
		String jsonText = helper.getJsonText(apiurl);
		return getResponsesFromJsonText(jsonText, commentId);
	}
	
	//methods for getting comments need to be refactored
    private List<Comment> getCommentsFromJsonText(String jsonText, int entity_id, int category_id) {
    	
        if (jsonText == null)
        {
        	return null;
        }
        int latest_comment_id = 0;
        
        // Get all the comment counters for update purpose
        GeoCategoryDAL categoryDAL = new GeoCategoryDAL(this.cx);
        List<CommentCategory> categories = categoryDAL.getCommentCategoriesByEntityId(entity_id);
        CommentCategory category;
        
        List<Comment> commentsList = new ArrayList<Comment>();
        List<Comment> commentsListOfASpecificCategory = new ArrayList<Comment>();
        Comment comment = null;
        try {
			JSONArray comments = new JSONArray(jsonText);
			for (int i=0; i<comments.length() ;i++)
            {
				comment = new Comment();
                JSONObject c = comments.getJSONObject(i);
                comment.setCommentId(c.getJSONObject("comment").getInt("comment_id"));
                comment.setCommentImg(c.getJSONObject("comment").getString("image_url"));
                comment.setDescription(c.getJSONObject("comment").getString("description"));
                comment.setTime(c.getJSONObject("comment").getString("created_at"));
                comment.setUserImg(c.getJSONObject("comment").getString("user_image"));
                comment.setUserName(c.getJSONObject("comment").getString("username"));
                comment.setCategory_id(c.getJSONObject("comment").getInt("category_id"));
                comment.setEntity_id(c.getJSONObject("comment").getInt("entity_id"));
                
                comment.setActualUserImg(helper.fetchImage(comment.getUserImg()));
                
                // Updating counter
                for(int ii=0; ii<categories.size(); ii++) {
                	category = categories.get(ii);
                	if(category.getCategory_id() == comment.getCategory_id()) {
                		category.setCount(category.getCount()+1);
                	}                		
                }
                //end of update
                
                if(comment.getCategory_id() == category_id) {
                	commentsListOfASpecificCategory.add(comment);
                }
                
                commentsList.add(comment);
                latest_comment_id = comment.getCommentId();
            }
			
			if(commentsList.size() == 0) {
				return null;
			} else {
				da.open();
				da.createComments(commentsList);
				for(int ii=0; ii<categories.size(); ii++) {
	            	category = categories.get(ii);
	            	da.updateCommentCounter(entity_id, category.getCategory_id(), category.getCount());        		
	            }
				da.close();
				
				SharedPreferences states = cx.getSharedPreferences(CacheBase.PREFERENCE_FILENAME, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = states.edit();
				editor.putInt("latest_commentid", latest_comment_id);
				editor.commit();
				
				return commentsListOfASpecificCategory;
			}
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
    
  //methods for getting responses need to be refactored
    private List<Comment> getResponsesFromJsonText(String jsonText, int comment_id) {
    	
        if (jsonText == null)
        {
        	return null;
        }
        
        int latest_response_id = 0;
        
        // Get all the response counters for update purpose
        GeoCategoryDAL categoryDAL = new GeoCategoryDAL(this.cx);
        List<ResponseCategory> categories = categoryDAL.getResponseCategoriesByCommentId(comment_id);
        ResponseCategory category;
        
        List<Comment> commentsList = new ArrayList<Comment>();
        Comment comment = null;
        try {
			JSONArray comments = new JSONArray(jsonText);
			for (int i=0; i<comments.length() ;i++)
            {
				comment = new Comment();
                JSONObject c = comments.getJSONObject(i);
                //used to identify existing responses, this field stores the actual comment id
                comment.setEntity_id(c.getJSONObject("comment").getInt("id"));
                comment.setCommentId(c.getJSONObject("comment").getInt("comment_id"));
                comment.setCategory_id(c.getJSONObject("comment").getInt("category_id"));
                comment.setDescription(c.getJSONObject("comment").getString("description"));
                comment.setTime(c.getJSONObject("comment").getString("created_at"));
                comment.setUserName(c.getJSONObject("comment").getString("username"));
                comment.setUserImg(c.getJSONObject("comment").getString("user_image"));
                comment.setActualUserImg(helper.fetchImage(comment.getUserImg()));
                // Updating counter
                for(int ii=0; ii<categories.size(); ii++) {
                	category = categories.get(ii);
                	if(category.getCategory_id() == comment.getCategory_id()) {
                		category.setCount(category.getCount()+1);
                	}                		
                }
                //end of update
                
                commentsList.add(comment);
                latest_response_id = c.getJSONObject("comment").getInt("id");
            }
			if(commentsList.size() == 0) {
				return null;
			} else {
				da.open();
				da.createResponses(commentsList);
				for(int ii=0; ii<categories.size(); ii++) {
	            	category = categories.get(ii);
	            	da.updateResponseCounter(comment_id, category.getCategory_id(), category.getCount());           		
	            }
				da.close();
				
				SharedPreferences states = cx.getSharedPreferences(CacheBase.PREFERENCE_FILENAME, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = states.edit();
				editor.putInt("latest_responseid", latest_response_id);
				editor.commit();
				
				return commentsList;
			}
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

}
