package geotagging.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GeotaggingEntityListOfProblems extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.entity_list_of_problems);
        
        Button buttonProblemReport = (Button) findViewById(R.id.buttonProblem1);
        buttonProblemReport.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent();
            	intent.setClassName("geotagging.app","geotagging.app.GeotaggingEntityProblem");
            	startActivity(intent);
            }
        });
	}
	
}
