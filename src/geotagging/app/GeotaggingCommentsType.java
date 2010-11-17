package geotagging.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class GeotaggingCommentsType extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.comment_type);
        
        final EditText edit_comment = (EditText) findViewById(R.id.edit_comment);
        final RadioButton radio_request_for_help = (RadioButton) findViewById(R.id.radio_request_for_help);
        final RadioButton radio_problem_report = (RadioButton) findViewById(R.id.radio_problem_report);
        final RadioButton radio_situation_description = (RadioButton) findViewById(R.id.radio_situation_description);
        final RadioButton radio_something_else = (RadioButton) findViewById(R.id.radio_something_else);
        final Button submit_comment = (Button) findViewById(R.id.submit_comment);
             
        OnClickListener radio_listener = new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
                submit_comment.setEnabled(true);                
            }
        };
        radio_request_for_help.setOnClickListener(radio_listener);
        radio_problem_report.setOnClickListener(radio_listener);
        radio_situation_description.setOnClickListener(radio_listener);
        radio_something_else.setOnClickListener(radio_listener);
                
        submit_comment.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	StringBuffer message = new StringBuffer();
            	message.append("Submitting the comment\n\"" + edit_comment.getText() + "\"\nAs a\n");
            	if(radio_request_for_help.isChecked()){
            		message.append(radio_request_for_help.getText());
            	}
            	if(radio_problem_report.isChecked()){
            		message.append(radio_problem_report.getText());
            	}
            	if(radio_situation_description.isChecked()){
            		message.append(radio_situation_description.getText());
            	}
            	if(radio_something_else.isChecked()){
            		message.append(radio_something_else.getText());
            	}
            	Toast.makeText(GeotaggingCommentsType.this, message, Toast.LENGTH_LONG).show();
            }
        });
        
        
	}
}
