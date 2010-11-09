package geotagging.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GeotaggingEntityInformation extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.entity_information);
        
        Button buttonProblemReport = (Button) findViewById(R.id.buttonProblemReport);
        buttonProblemReport.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent();
            	intent.setClassName("geotagging.app","geotagging.app.GeotaggingEntityListOfProblems");
            	startActivity(intent);
            }
        });
        
        Button btnHelp = (Button) findViewById(R.id.btnPlane);
        btnHelp.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent();
            	intent.setClassName("geotagging.app","geotagging.app.GeotaggingResponseList");
            	Bundle b = new Bundle();

            	b.putString("entity_id", "13");

            	intent.putExtras(b);
            	startActivity(intent);
            }
        });
        
        
	}
}
