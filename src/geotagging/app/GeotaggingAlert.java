package geotagging.app;

import geotagging.utils.UIUtils;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GeotaggingAlert extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.app_alert);

       ((TextView)this.findViewById(android.R.id.empty)).setText("This functionality is not implemented yet...");
	}
	
	/** Handle "home" title-bar action. */
    public void onHomeClick(View v) {
        UIUtils.goHome(this);
    }
    
    public void onBackClick(View v) {
    	UIUtils.goBack(this);
    }

    /** Handle "refresh" title-bar action. */
    public void onRefreshClick(View v) {
    }

    /** Handle "search" title-bar action. */
    public void onSearchClick(View v) {
        UIUtils.goSearch(this);
    }
}
