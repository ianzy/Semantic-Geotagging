package geotagging.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GeotaggingEntityProblem extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.entity_problem);
        
        Button buttonProblemReport = (Button) findViewById(R.id.buttonRespond);
        buttonProblemReport.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent();
            	intent.setClassName("geotagging.app","geotagging.app.GeotaggingEntityResponse");
            	startActivity(intent);
            }
        });
	}
	
}
