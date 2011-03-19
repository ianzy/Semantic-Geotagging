package geotagging.app;

import geotagging.provider.CacheBase;
import geotagging.realtime.UpdateCategoriesThread;
import geotagging.utils.BackendHelperSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class GeotaggingSetUpAccount extends Activity {	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_up_user_account);
        
        SharedPreferences settings = this.getSharedPreferences(CacheBase.PREFERENCE_FILENAME, MODE_PRIVATE);
        if(!settings.getString("username", "NSV").equals("NSV")) {
        	Intent intent = new Intent();
			intent.setClassName("geotagging.app", "geotagging.app.GeotaggingMap");
			this.startActivity(intent);
            finish();
        }

	}
	
	public void onLogin(View v) {
		((Button)v).setEnabled(false);
		ProgressDialog MyDialog = ProgressDialog.show( this, "Loging in. " , " Please wait ... ", true);
		String username = ((EditText)this.findViewById(R.id.account_user_name)).getText().toString();
		String password = ((EditText)this.findViewById(R.id.account_password)).getText().toString();
		String organization = ((EditText)this.findViewById(R.id.account_organization)).getText().toString();
		String role = ((EditText)this.findViewById(R.id.account_role)).getText().toString();
		
		JSONObject obj = new JSONObject();
        try {
			obj.put("username", username);
			obj.put("password", password);
            obj.put("organization", organization);
            obj.put("role", role);

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String contentForPost = obj.toString();
		String apiurl = this.getResources().getString(R.string.GeotaggingAPIVerifyUserAccount);//"http://10.0.2.2:3000/api/verify_user_information.json";
		String response = BackendHelperSingleton.getInstance().postContent(apiurl, contentForPost);
		
//		Log.i("_______________________________", response);
		if(response != null) {
			
			String responseUserName = null;
			int userId = -1;
			try {
				JSONObject c = new JSONObject(response);
				responseUserName = c.getJSONObject("user").getString("login");
				userId = c.getJSONObject("user").getInt("id");
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			
			this.deleteDatabase("geotagging_cache.db");
			SharedPreferences settings = this.getSharedPreferences(CacheBase.PREFERENCE_FILENAME, MODE_PRIVATE);
	        
        	SharedPreferences.Editor prefEditor = settings.edit();
        	prefEditor.putString("username", responseUserName);
        	prefEditor.putString("password", password);
        	prefEditor.putInt("user_id", userId);
        	
        	prefEditor.putInt("latest_entityid", 0);
        	prefEditor.putInt("latest_commentid", 0);
        	prefEditor.putInt("latest_responseid", 0);
        	prefEditor.putInt("cached_entityid", 0);
        	prefEditor.putInt("cached_commentid", 0);
        	prefEditor.putInt("cached_responseid", 0);
        	prefEditor.putInt("remote_count", 50);
        	prefEditor.commit(); 
        	
        	// initialize the categories base
        	UpdateCategoriesThread thread = new UpdateCategoriesThread(this);
        	thread.start();
	        
			Intent intent = new Intent();
			intent.setClassName("geotagging.app", "geotagging.app.GeotaggingMap");
			this.startActivity(intent);
            finish();
		} else {
			Toast.makeText(this, "Login failed", Toast.LENGTH_LONG).show();
			((Button)v).setEnabled(true);
			MyDialog.dismiss();
		}
	}
	
	public void onSignUp(View v) {
		
	}
}
