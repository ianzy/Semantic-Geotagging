package geotagging.app;

import geotagging.DES.Comment;
import geotagging.provider.CacheBase;
import geotagging.utils.BackendHelperSingleton;
import geotagging.utils.DALUtils;
import geotagging.utils.UIUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class GeotaggingCommentComposing extends Activity implements TextWatcher {
	
	private boolean important;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.comment_composing);
		this.important = false;
		
		//tricky way for the editText to listen to the on text change event
        ((EditText)findViewById(R.id.comment_composing_content)).addTextChangedListener(this);
	}
	
	public void onSubmitClick(View v) {
		Button btnSubmit = (Button) findViewById(R.id.comment_composing_submit);
		btnSubmit.setEnabled(false);
		
		if(((EditText)this.findViewById(R.id.comment_composing_content)).getText().toString().trim().equals("")) {
    		Toast.makeText(this, "Comment content should not be empty", Toast.LENGTH_LONG).show();
    		btnSubmit.setEnabled(true);
    		return;
    	}
		
		Bundle b = this.getIntent().getExtras();
		int entity_id = b.getInt("entity_id");
		int category_id = b.getInt("category_id");
		EditText edit_content = (EditText)this.findViewById(R.id.comment_composing_content);
		
		SharedPreferences settings = this.getSharedPreferences(CacheBase.PREFERENCE_FILENAME, MODE_PRIVATE);
		int user_id = settings.getInt("user_id", -1);
		String user_name = settings.getString("username", "error");
		
		JSONObject obj = new JSONObject();
        JSONObject ent = new JSONObject();
        try {
			obj.put("entity_id", entity_id);
			obj.put("category_id", category_id);
            obj.put("description", edit_content.getText().toString());
            obj.put("comment_id", -1);
            obj.put("user_id", user_id);
            obj.put("type", "");
            obj.put("created_at","");
            obj.put("updated_at","");
            obj.put("important_tag", this.important);
     
            ent.put("comment", obj);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String contentForPost = ent.toString();
		Log.i(">>>>>>>>>>>>>>>>", contentForPost);
		
		String apiurl = this.getResources().getString(R.string.GeotaggingAPIPostNewComment);//"http://10.0.2.2:3000/api/comments.json"
		String response = BackendHelperSingleton.getInstance().postContent(apiurl, contentForPost);
		
		
		if(response != null) {
			Toast.makeText(this, "Submitting new comment", Toast.LENGTH_LONG).show();
			Comment comment = DALUtils.getCommentFromJsonText(response);
			//finish the activity
            Intent intent = new Intent();
            //need to be refactored
            intent.putExtra("description",edit_content.getText().toString());
            intent.putExtra("user_name", user_name);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String createAt = dateFormat.format(date)+ " UTC";
            intent.putExtra("created_at", createAt); 
            intent.putExtra("id", comment.getCommentId());
            intent.putExtra("important_tag", this.important);
            setResult(RESULT_OK, intent);
            finish();
		} else {
			Toast.makeText(this, "Submition failed", Toast.LENGTH_LONG).show();
			btnSubmit.setEnabled(true);
		}
		
	}
	
	public void onImportantClick(View v) {
		CheckBox checkBox = (CheckBox)v;
		if(checkBox.isChecked())
			this.important = true;
		else
			this.important = false;
	}
	
	public void onCancelClick(View v) {
		this.setResult(Activity.RESULT_CANCELED);
		this.finish();
	}
	
	public void onBackClick(View v) {
    	UIUtils.goBack(this);
    }
	
	/** Handle "home" title-bar action. */
    public void onHomeClick(View v) {
        UIUtils.goHome(this);
    }


    /** Handle "search" title-bar action. */
    public void onSearchClick(View v) {
        UIUtils.goSearch(this);
    }
    
   
	public void afterTextChanged(Editable s) {
    	Button btnSubmit = (Button) findViewById(R.id.comment_composing_submit);
    	if(s.toString().trim().equals("")) {
    		btnSubmit.setEnabled(false);
    	} else {
    		btnSubmit.setEnabled(true);
        	btnSubmit.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
    	}
    	
    }
 
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {
        // TODO Auto-generated method stub
 
    }
 
    public void onTextChanged(CharSequence s, int start, int before,
            int count) {
        // TODO Auto-generated method stub
 
    }
    	
}
