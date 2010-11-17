package geotagging.app;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class GeotaggingEntityInformation extends Activity {
	
	private ArrayAdapter<String> entity_information_adapter;
	private String REQUEST_FOR_HELP = "Request for Help";
	private String SITUATION_DESCRIPTION = "Situation Description";
	private String PROBLEM_REPORT = "Problem Report";
	private String SOMETHING_ELSE = "Something Else";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.entity_information_list);
        
        entity_information_adapter = new ArrayAdapter<String>(this, R.layout.entity_information);
        entity_information_adapter.add(REQUEST_FOR_HELP);
        entity_information_adapter.add(SITUATION_DESCRIPTION);
        entity_information_adapter.add(PROBLEM_REPORT + "(4)");
        entity_information_adapter.add(SOMETHING_ELSE);

        ListView pairedListView = (ListView) findViewById(R.id.entity_information_list);
        pairedListView.setAdapter(entity_information_adapter);
        pairedListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent();
				Bundle b = new Bundle();
            	b.putString("entity_id", "13");
				String information = ((TextView) arg1).getText().toString();
				if(information.startsWith(REQUEST_FOR_HELP)){
					intent.setClassName("geotagging.app","geotagging.app.GeotaggingResponseList");
				}
				else if(information.startsWith(SITUATION_DESCRIPTION)){
					intent.setClassName("geotagging.app","geotagging.app.GeotaggingResponseList");
	            }
				else if(information.startsWith(PROBLEM_REPORT)){
					intent.setClassName("geotagging.app","geotagging.app.GeotaggingEntityListOfProblems");
				}
				else if(information.startsWith(SOMETHING_ELSE)){
					intent.setClassName("geotagging.app","geotagging.app.GeotaggingResponseList");
				}
				
				intent.putExtras(b);
				startActivity(intent);
			}
		});
	}
}
