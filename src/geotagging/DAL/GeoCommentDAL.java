package geotagging.DAL;

import geotagging.DES.Comment;
import geotagging.IDAL.GeoCommentIDAL;
import geotagging.app.R;
import geotagging.utils.BackendHelperSingleton;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

public class GeoCommentDAL implements GeoCommentIDAL {

	private Activity cx;
	private BackendHelperSingleton helper;
	
	public GeoCommentDAL(Activity cx) {
		this.cx = cx;
        helper = BackendHelperSingleton.getInstance();
	}
	
	public List<Comment> getCommentsByEntityID(String entityId) {
		String apiurl = cx.getResources().getString(R.string.get_comments_by_entity_and_category);
		String jsonText = helper.getJsonText(apiurl);
		Log.i("test", apiurl);
		Log.i("jsontext", jsonText);
		return getCommentsFromJsonText(jsonText);
	}
	
	public List<Comment> getCommentsByCommentId(int commentId) {
		String apiurl = cx.getResources().getString(R.string.get_comments_by_entity_and_category);
		String jsonText = helper.getJsonText(apiurl);
		Log.i("test", apiurl);
		Log.i("jsontext", jsonText);
		return getCommentsFromJsonText(jsonText);
	}
	
	//methods for getting responses need to be refactored
    private List<Comment> getCommentsFromJsonText(String jsonText) {
    	
        if (jsonText == null)
        {
        	return null;
        }
        
        List<Comment> commentsList = new ArrayList<Comment>();
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
                
                comment.setActualUserImg(helper.fetchImage(comment.getUserImg()));
                
                commentsList.add(comment);
            }
			return commentsList;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

}
