package geotagging.app;

import geotagging.utils.BackendHelperSingleton;
import geotagging.utils.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class GeotaggingCommentsType extends Activity {
	
	private EditText edit_comment;
    private EditText edit_title;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_type);
        edit_comment = (EditText) findViewById(R.id.edit_comment);
        edit_title = (EditText)this.findViewById(R.id.edit_title);
        
	}
	
	public void onSubmitClick(View v) {
        	
		//experimental code for submitting data to the back end    	
    	//getting data from intent extra
    	Bundle b = getIntent().getExtras();
        final String location = b.getString("location");
        final double lng = b.getDouble("lng");
        final double lat = b.getDouble("lat");
        //end of getting data
		
    	if(edit_comment.getText().toString().trim().equals("") ||
    			edit_title.getText().toString().trim().equals("")) {
    		Toast.makeText(GeotaggingCommentsType.this, "Title and description should not be empty", Toast.LENGTH_LONG).show();
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

            ent.put("entity", obj);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String contentForPost = ent.toString();
		Log.i(">>>>>>>>>>>>>>>>", contentForPost);
		
		String apiurl = "http://10.0.2.2:3000/api/entities.json";
		
		if(BackendHelperSingleton.getInstance().postContent(apiurl, contentForPost)) {
			Toast.makeText(GeotaggingCommentsType.this, "Submitting new entity", Toast.LENGTH_LONG).show();
			//finish the activity
            Intent intent = new Intent();
            //need to be refactored
            intent.putExtra("location",location);
            intent.putExtra("title", edit_title.getText().toString());
            intent.putExtra("lng", lng);
            intent.putExtra("description", edit_comment.getText().toString());
            intent.putExtra("lat", lat);
            setResult(RESULT_OK, intent);
            finish();
		} else {
			Toast.makeText(GeotaggingCommentsType.this, "Submit fail", Toast.LENGTH_LONG).show();
			
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

    /** Handle "search" title-bar action. */
    public void onSearchClick(View v) {
        UIUtils.goSearch(this);
    }
}
