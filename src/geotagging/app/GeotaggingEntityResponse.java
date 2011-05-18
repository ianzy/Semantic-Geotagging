package geotagging.app;

import geotagging.DAL.GeoDraftDAL;
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

public class GeotaggingEntityResponse extends Activity implements TextWatcher {
	
	private int category_id;
	
	private int comment_id;
	private boolean important;
	private GeoDraftDAL draftDAL;
	private String content;
	
	private String categoryName;
//	private List<ResponseCategory> categories;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.entity_response);
//        GeoCategoryDAL categoryDAL = new GeoCategoryDAL(this);
        Bundle b = this.getIntent().getExtras();
		comment_id = b.getInt("comment_id");
//        categories = categoryDAL.getResponseCategoriesByCommentId(comment_id);
        category_id = b.getInt("category_id");
        important = false;
        EditText edit_content = (EditText)findViewById(R.id.edit_response);
		edit_content.addTextChangedListener(this);
        
        draftDAL = new GeoDraftDAL(this);
        
        Comment draftResponse = draftDAL.getResponseDraft(comment_id, category_id);
        
        if(null != draftResponse) {
        	content = draftResponse.getDescription();
            important = draftResponse.isImportantTag();
            ((CheckBox)this.findViewById(R.id.important_checkbox)).setChecked(important);
        	edit_content.setText(content);
        }
	}
	
//	public void onRadioClick(View v) {
//		// Perform action on clicks
//		Button buttonProblemReport = (Button) findViewById(R.id.submit_response);
//    	buttonProblemReport.setEnabled(true);
//    	buttonProblemReport.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
//    	
////    	RadioButton rb = (RadioButton)v;
////    	categoryName = rb.getText().toString();
////    	ResponseCategory rc;
////    	for(int i=0; i<categories.size(); i++) {
////    		rc = categories.get(i);
////    		if(categoryName.equals(rc.getName())) {
////    			category_id = rc.getCategory_id();
////    		}
////    	}
//	}
	
	public void onSubmitClick(View v) {
		
		Button btnSubmit = (Button) findViewById(R.id.submit_response);
		btnSubmit.setEnabled(false);
		
		if(((EditText)this.findViewById(R.id.edit_response)).getText().toString().trim().equals("")) {
    		Toast.makeText(this, "Follow up response content should not be empty", Toast.LENGTH_LONG).show();
    		btnSubmit.setEnabled(true);
    		return;
    	}
		
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
            obj.put("important_tag", this.important);
     
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
            String createAt = dateFormat.format(date);
            intent.putExtra("created_at", createAt);
            intent.putExtra("category_id", resp.getCategory_id());
            intent.putExtra("id", resp.getEntity_id());
            intent.putExtra("important_tag", this.important);
            draftDAL.deleteResponseDraft(this.comment_id, this.category_id);
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
	
	public void onSaveClick(View v) {
		EditText e = (EditText)this.findViewById(R.id.edit_response);
		if(e.getText().toString().trim().equals("")) {
    		Toast.makeText(this, "Empty content", Toast.LENGTH_LONG).show();
    		return;
    	}
		String content = e.getText().toString();
		if(null == draftDAL.getResponseDraft(this.comment_id, this.category_id)) {
			draftDAL.createResponseDraft(this.comment_id, this.category_id, content, this.important);
		} else {
			draftDAL.updateResponseDraft(this.comment_id, this.category_id, content, this.important);
		}
	}
	
	public void onResetClick(View v) {
		((EditText)this.findViewById(R.id.edit_response)).setText("");
	}
	
	public void onCancelClick(View v) {
		draftDAL.deleteResponseDraft(this.comment_id, this.category_id);
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
    
    public void afterTextChanged(Editable s) {

    	Button btnSubmit = (Button) findViewById(R.id.submit_response);
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
