package geotagging.app;

import geotagging.DES.Entity;
import geotagging.utils.BackendHelperSingleton;
import geotagging.utils.DALUtils;
import geotagging.utils.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class GeotaggingCommentsType extends Activity implements TextWatcher {
	
	private EditText edit_comment;
    private EditText edit_title;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_type);
        edit_comment = (EditText)this.findViewById(R.id.edit_comment);
        edit_title = (EditText)this.findViewById(R.id.edit_title);
    
        Bundle b = getIntent().getExtras();
        String editLocation = b.getString("location");
        Log.i(">>>>>>>>>>>>>>>>>>>>>>>>>!!!!!!!!!!", editLocation);
        edit_comment.setText(editLocation);
        
        //tricky way for the editText to listen to the on text change event
        ((EditText)findViewById(R.id.edit_comment)).addTextChangedListener(this);
        ((EditText)findViewById(R.id.edit_title)).addTextChangedListener(this);
        
	}
	
	public void onSubmitClick(View v) {
        
		Button btnSubmit = (Button) findViewById(R.id.submit_comment);
		btnSubmit.setEnabled(false);
		
		//experimental code for submitting data to the back end    	
    	//getting data from intent extra
    	Bundle b = getIntent().getExtras();
        String location = b.getString("location");
        double lng = b.getDouble("lng");
        double lat = b.getDouble("lat");
        String iconName = b.getString("iconName");
        int drawableId = b.getInt("drawableId");
        //end of getting data
		
    	if(edit_comment.getText().toString().trim().equals("") ||
    			edit_title.getText().toString().trim().equals("")) {
    		Toast.makeText(GeotaggingCommentsType.this, "Title and description should not be empty", Toast.LENGTH_LONG).show();
    		btnSubmit.setEnabled(true);
    		return;
    	}
         	
    	
        JSONObject obj = new JSONObject();
        JSONObject ent = new JSONObject();
        try {
        	obj.put("location",location);
			obj.put("created_at","");
            obj.put("title", edit_title.getText().toString());
            obj.put("updated_at","");
            obj.put("lng", lng);
            obj.put("description", edit_comment.getText().toString());
            obj.put("lat", lat);
            obj.put("icon_uri", iconName);

            ent.put("entity", obj);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String contentForPost = ent.toString();
		Log.i(">>>>>>>>>>>>>>>>", contentForPost);
		
		String apiurl = this.getResources().getString(R.string.GeotaggingAPIPostNewEntity);
		String response = BackendHelperSingleton.getInstance().postContent(apiurl, contentForPost);
		
		
		if(response != null) {
			Entity entity = DALUtils.getEntityFromJsonText(response);
			Toast.makeText(GeotaggingCommentsType.this, "Submitting new entity", Toast.LENGTH_LONG).show();
			//finish the activity
            Intent intent = new Intent();
            //need to be refactored
            intent.putExtra("location",location);
            intent.putExtra("title", edit_title.getText().toString());
            intent.putExtra("lng", lng);
            intent.putExtra("description", edit_comment.getText().toString());
            intent.putExtra("lat", lat);
            intent.putExtra("drawableId", drawableId);
            intent.putExtra("entity_id", entity.getId());
            intent.putExtra("iconName", iconName);
            setResult(RESULT_OK, intent);
            finish();
		} else {
			Toast.makeText(GeotaggingCommentsType.this, "Submit fail", Toast.LENGTH_LONG).show();
			btnSubmit.setEnabled(true);
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
    
	public void afterTextChanged(Editable s) {
    	Button btnSubmit = (Button) findViewById(R.id.submit_comment);
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
