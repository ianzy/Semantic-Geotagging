package geotagging.app;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class GeotaggingListofEntities extends ListActivity {
	
	private static final String[] HARDCODED_DATA = new String[] {
		"Plane crash", "Airfield fire", "Pumper unit 7", "Personnel", "Something new"
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, HARDCODED_DATA));
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);
        
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
            		int position, long id) {
            		// When clicked, show a toast with the TextView text
            		Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
            		Toast.LENGTH_SHORT).show();
            		Intent intent = new Intent();
                	intent.setClassName("geotagging.app","geotagging.app.GeotaggingCommentsType");
                	startActivity(intent);
            }
          });
	}
	
}
