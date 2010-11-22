package geotagging.app;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class GeotaggingCommentsType extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //experimental code for submitting data to the back end    	
    	//getting data from intent extra
    	Bundle b = getIntent().getExtras();
        final String location = b.getString("location");
        final double lng = b.getDouble("lng");
        final double lat = b.getDouble("lat");
        //end of getting data
        
        
        setContentView(R.layout.comment_type);
        
        final EditText edit_comment = (EditText) findViewById(R.id.edit_comment);
        final RadioButton radio_request_for_help = (RadioButton) findViewById(R.id.radio_request_for_help);
        final RadioButton radio_problem_report = (RadioButton) findViewById(R.id.radio_problem_report);
        final RadioButton radio_situation_description = (RadioButton) findViewById(R.id.radio_situation_description);
        final RadioButton radio_something_else = (RadioButton) findViewById(R.id.radio_something_else);
        final Button submit_comment = (Button) findViewById(R.id.submit_comment);
             
        OnClickListener radio_listener = new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
                submit_comment.setEnabled(true);                
            }
        };
        radio_request_for_help.setOnClickListener(radio_listener);
        radio_problem_report.setOnClickListener(radio_listener);
        radio_situation_description.setOnClickListener(radio_listener);
        radio_something_else.setOnClickListener(radio_listener);
                
        submit_comment.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	StringBuffer message = new StringBuffer();
            	message.append("Submitting the comment\n\"" + edit_comment.getText() + "\"\nAs a\n");
            	if(radio_request_for_help.isChecked()){
            		message.append(radio_request_for_help.getText());
            	}
            	if(radio_problem_report.isChecked()){
            		message.append(radio_problem_report.getText());
            	}
            	if(radio_situation_description.isChecked()){
            		message.append(radio_situation_description.getText());
            	}
            	if(radio_something_else.isChecked()){
            		message.append(radio_something_else.getText());
            	}
            	Toast.makeText(GeotaggingCommentsType.this, message, Toast.LENGTH_LONG).show();
            	
                 	
            	
            	//experimental code for submitting data to the back end             	
            	//data posting code
            	URL url;
                HttpURLConnection connection = null; 
//                String contentForTest = "<entity><description>Test</description><lat>37.4</lat><lng>-122.3</lng><location>bayshore sunnyvale golf course Mountain View CA 94043</location><title>Fire</title></entity>";
                
                JSONObject obj = new JSONObject();
                JSONObject ent = new JSONObject();
                try {
					obj.put("location",location);
					obj.put("created_at","2010-11-03T07:39:57Z");
	                obj.put("title","New Entity From Android App");
	                obj.put("updated_at","2010-11-03T07:39:57Z");
	                obj.put("lng", lng);
	                obj.put("description", edit_comment.getText());
	                obj.put("lat", lat);
	         
	                ent.put("entity", obj);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				String contentForTest = ent.toString();
				Log.i(">>>>>>>>>>>>>>>>", contentForTest);
                
                //{"entity":{"location":"1000-1136 Fairmont Dr San Bruno, CA 94066","created_at":"2010-11-03T07:39:57Z","title":"House on fire","updated_at":"2010-11-03T07:39:57Z","lng":-122.441361,"id":15,"description":"1000-1136 Fairmont Dr\nSan Bruno, CA 94066","lat":37.624123,"icon_uri":""}}
                try {
                    //Create connection
                    url = new URL("http://geotagging.heroku.com/entities.json");
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", 
                         "application/json;charset=UTF-8");
              			
                    connection.setRequestProperty("Content-Length", 
                             Integer.toString(contentForTest.getBytes().length));
                    connection.setRequestProperty("Content-Language", "en-US");  
              			
                    connection.setUseCaches (false);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    //Send request
                    DataOutputStream wr = new DataOutputStream (
                                connection.getOutputStream ());
                    wr.writeBytes (contentForTest);
                    wr.flush ();
                    wr.close ();
                    Log.i("<<<<<<<<<<<<<<<<<<<<<<<here","test");
                    //Get Response	
                    InputStream is = connection.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    String line;
                    
                    StringBuffer response = new StringBuffer(); 
                    while((line = rd.readLine()) != null) {
                      response.append(line);
                      Log.i("line:", line);
                    }
                    Log.i("response---------------------", response.toString());
                    rd.close();
                    finish();

                  } catch (Exception e) {

                    e.printStackTrace();
                    

                  } finally {

                    if(connection != null) {
                      connection.disconnect(); 
                    }
                    
                  }
            }
        });
        
        
	}
}
