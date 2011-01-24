package geotagging.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class GeotaggingEntityListOfProblems extends Activity {

	private ArrayAdapter<String> entity_problems_adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.entity_problems_list);
        
        entity_problems_adapter = new ArrayAdapter<String>(this, R.layout.entity_problems);
        entity_problems_adapter.add("Responder 1: Here's a problem...");
        entity_problems_adapter.add("Responder 2: Here's a problem...");
        entity_problems_adapter.add("Responder 3: Here's a problem...");
        entity_problems_adapter.add("Responder 4: Here's a problem...");
        
        ListView pairedListView = (ListView) findViewById(R.id.entity_problems_list);
        pairedListView.setAdapter(entity_problems_adapter);
        pairedListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent();
				Bundle b = new Bundle();
            	b.putString("entity_id", "13");
				intent.setClassName("geotagging.app","geotagging.app.GeotaggingCommentDetails");
				intent.putExtras(b);
				startActivity(intent);
			}
		});
	}
	
}
