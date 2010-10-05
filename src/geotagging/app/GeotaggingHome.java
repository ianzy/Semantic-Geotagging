package geotagging.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class GeotaggingHome extends ListActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     
        setListAdapter(new SimpleAdapter(this, prepareItems(),
        		android.R.layout.simple_list_item_1, new String[] { "title" },
                new int[] { android.R.id.text1 }));
        
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);
        
    }
    
    private List<Map<String, Object>> prepareItems() {
    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    	Map<String, Object> map = new HashMap<String, Object>();
    	
    	Intent intent = activityIntent("geotagging.app","geotagging.app.GeotaggingListofEntities");
    	map.put("title", "Enter information");
    	map.put("intent", intent);
    	list.add(map);
    	
    	intent = activityIntent("geotagging.app","geotagging.app.GeotaggingMap");
    	map = new HashMap<String, Object>();
    	map.put("title", "Map");
    	map.put("intent", intent);
    	list.add(map);
    	
    	return list;
    }
    
    private Intent activityIntent(String pkg, String componentName) {
        Intent result = new Intent();
        result.setClassName(pkg, componentName);
        return result;
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	Map<String, Object> itemAtPosition = (Map<String, Object>) l.getItemAtPosition(position);
        Intent intent = (Intent) itemAtPosition.get("intent");
        startActivity(intent);
    }
 
}