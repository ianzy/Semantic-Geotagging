package geotagging.app;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class GeotaggingHomeWithTab extends TabActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);

	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab
	    
	    intent = new Intent().setClass(this, GeotaggingMap.class);

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("map").setIndicator("Map",
	                      res.getDrawable(R.drawable.ic_menu_mapmode))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, GeotaggingListofEntities.class);
	    spec = tabHost.newTabSpec("list").setIndicator("List",
	                      res.getDrawable(R.drawable.ic_menu_info_details))
	                  .setContent(intent);
	    tabHost.addTab(spec);


	    tabHost.setCurrentTab(0);
	}
}
