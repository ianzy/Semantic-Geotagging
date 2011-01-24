package geotagging.app;

import geotagging.utils.BackendHelperSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class GeotaggingEntityResponse extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.entity_response);
        
        final EditText edit_response = (EditText) findViewById(R.id.edit_response);
        final RadioButton radio_big_picture = (RadioButton) findViewById(R.id.radio_big_picture);
        final RadioButton radio_details = (RadioButton) findViewById(R.id.radio_details);
        final RadioButton radio_explanation = (RadioButton) findViewById(R.id.radio_explanation);
        final RadioButton radio_advice = (RadioButton) findViewById(R.id.radio_advice);
        final RadioButton radio_alternative = (RadioButton) findViewById(R.id.radio_alternative);
        final RadioButton radio_example = (RadioButton) findViewById(R.id.radio_example);
                
        final Button buttonProblemReport = (Button) findViewById(R.id.submit_response);
        buttonProblemReport.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	StringBuffer message = new StringBuffer();
            	message.append("Submitting the response\n\"" + edit_response.getText() + "\"\nAs a\n");
            	if(radio_big_picture.isChecked()){
            		message.append(radio_big_picture.getText());
            	}
            	if(radio_details.isChecked()){
            		message.append(radio_details.getText());
            	}
            	if(radio_explanation.isChecked()){
            		message.append(radio_explanation.getText());
            	}
            	if(radio_advice.isChecked()){
            		message.append(radio_advice.getText());
            	}
            	if(radio_alternative.isChecked()){
            		message.append(radio_alternative.getText());
            	}
            	if(radio_example.isChecked()){
            		message.append(radio_example.getText());
            	}
            	Toast.makeText(GeotaggingEntityResponse.this, message, Toast.LENGTH_LONG).show();
            	
            	EditText edit_comment = (EditText) findViewById(R.id.edit_response);
            	
            	JSONObject obj = new JSONObject();
                JSONObject ent = new JSONObject();
                try {
					
	                obj.put("description", edit_comment.getText());
	                obj.put("comment_id", 1);
	         
	                ent.put("comment", obj);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				String contentForPost = ent.toString();
				Log.i(">>>>>>>>>>>>>>>>", contentForPost);
				
				BackendHelperSingleton.getInstance().postContent("http://10.0.2.2:3000/comments", contentForPost);
				
				//finish the activity
                Intent intent = new Intent();
                //need to be refactored
                intent.putExtra("description", edit_comment.getText().toString());
                intent.putExtra("comment_id", 1);
                intent.putExtra("userName","demo");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        
        OnClickListener radio_listener = new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	buttonProblemReport.setEnabled(true);
            	buttonProblemReport.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
            }
        };
        
        radio_big_picture.setOnClickListener(radio_listener);
        radio_details.setOnClickListener(radio_listener);
        radio_explanation.setOnClickListener(radio_listener);
        radio_advice.setOnClickListener(radio_listener);
        radio_alternative.setOnClickListener(radio_listener);
        radio_example.setOnClickListener(radio_listener);
	}
	
}
