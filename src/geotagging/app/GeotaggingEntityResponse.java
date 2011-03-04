package geotagging.app;

import geotagging.DAL.GeoCategoryDAL;
import geotagging.DES.Comment;
import geotagging.DES.ResponseCategory;
import geotagging.provider.CacheBase;
import geotagging.utils.BackendHelperSingleton;
import geotagging.utils.DALUtils;
import geotagging.utils.UIUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class GeotaggingEntityResponse extends Activity {
	
	private int category_id;
	
	private int comment_id;
	
	private String categoryName;
	private List<ResponseCategory> categories;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.entity_response);
        GeoCategoryDAL categoryDAL = new GeoCategoryDAL(this);
        Bundle b = this.getIntent().getExtras();
		comment_id = b.getInt("comment_id");
        categories = categoryDAL.getResponseCategoriesByCommentId(comment_id);
        
	}
	
	public void onRadioClick(View v) {
		// Perform action on clicks
		Button buttonProblemReport = (Button) findViewById(R.id.submit_response);
    	buttonProblemReport.setEnabled(true);
    	buttonProblemReport.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
    	
    	RadioButton rb = (RadioButton)v;
    	categoryName = rb.getText().toString();
    	ResponseCategory rc;
    	for(int i=0; i<categories.size(); i++) {
    		rc = categories.get(i);
    		if(categoryName.equals(rc.getName())) {
    			category_id = rc.getCategory_id();
    		}
    	}
	}
	
	public void onSubmitClick(View v) {
		
		EditText edit_comment = (EditText) findViewById(R.id.edit_response);
    	SharedPreferences settings = this.getSharedPreferences(CacheBase.PREFERENCE_FILENAME, MODE_PRIVATE);
		int user_id = settings.getInt("user_id", -1);
		String userName = settings.getString("username", "error");
    	
    	JSONObject obj = new JSONObject();
        JSONObject ent = new JSONObject();
        try {

            obj.put("comment_id", comment_id);
            obj.put("entity_id", -1);
			obj.put("category_id", category_id);
            obj.put("description", edit_comment.getText().toString());
            obj.put("user_id", user_id);
            obj.put("type", "");
            obj.put("created_at","");
            obj.put("updated_at","");
     
            ent.put("comment", obj);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String contentForPost = ent.toString();
		Log.i(">>>>>>>>>>>>>>>>", contentForPost);
		
		String apiurl = this.getResources().getString(R.string.GeotaggingAPIPostNewResponse);//"http://10.0.2.2:3000/api/comments.json"
		String response = BackendHelperSingleton.getInstance().postContent(apiurl, contentForPost);
		
		if(response != null) {
			Toast.makeText(this, "Submitting new response", Toast.LENGTH_LONG).show();
			Comment resp = DALUtils.getResponseFromJsonText(response);
			//finish the activity
	        Intent intent = new Intent();
	        //need to be refactored
	        intent.putExtra("description", edit_comment.getText().toString());
	        intent.putExtra("comment_id", comment_id);
	        intent.putExtra("userName",userName);
	        intent.putExtra("category_name", this.categoryName);
	        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String createAt = dateFormat.format(date)+ " UTC";
            intent.putExtra("created_at", createAt); 
            intent.putExtra("id", resp.getEntity_id());
	        setResult(RESULT_OK, intent);
	        finish();
		} else {
			Toast.makeText(this, "Submition failed", Toast.LENGTH_LONG).show();
		}
		
		
		
	}
	
	public void onCancelClick(View v) {
		this.setResult(Activity.RESULT_CANCELED);
		this.finish();
	}
	
	/** Handle "home" title-bar action. */
    public void onHomeClick(View v) {
        UIUtils.goHome(this);
    }
    
    public void onBackClick(View v) {
    	UIUtils.goBack(this);
    }


    /** Handle "search" title-bar action. */
    public void onSearchClick(View v) {
        UIUtils.goSearch(this);
    }
	
}
