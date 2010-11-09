package geotagging.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GeotaggingEntityResponse extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.entity_response);
        
        Button buttonProblemReport = (Button) findViewById(R.id.buttonResponse);
        buttonProblemReport.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent();
            	intent.setClassName("geotagging.app","geotagging.app.GeotaggingEnteringInformation");
            	startActivity(intent);
            }
        });
	}
	
}
