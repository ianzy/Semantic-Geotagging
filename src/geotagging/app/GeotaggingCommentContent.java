package geotagging.app;

import geotagging.DES.ResponseCategory;
import geotagging.realtime.UpdateResponseCounterThread;
import geotagging.utils.UIUtils;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GeotaggingCommentContent extends Activity implements IResponseCounter {
	
	private int commentId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.comment_content);
        
        //get data from previous activity
        Bundle b = getIntent().getExtras();
        String userName = b.getString("userName");
        commentId = b.getInt("commentId");
        String description = b.getString("description");
        String responsesCount = b.getString("count");  
        
        Button btnViewResponses = (Button)this.findViewById(R.id.comment_content_btn_see);
        btnViewResponses.setText("See follow-up responses (" + responsesCount +")");
		
        TextView tvUserName = (TextView)this.findViewById(R.id.comment_content_user_name);
        tvUserName.setText(userName+" said,");
        
        TextView tvDescription = (TextView)this.findViewById(R.id.comment_content_content);
        tvDescription.setText(description);
        
	}
	
	@Override
	public void onResume() {
		super.onResume();
		UpdateResponseCounterThread thread = new UpdateResponseCounterThread(this, commentId);
        thread.start();
	}
	
	private List<ResponseCategory> counters;
    public void updateCounter(List<ResponseCategory> counters) {
    	this.counters = counters;
    	handler.sendEmptyMessage(0);
    }
    
    private Handler handler = new Handler() {
        public void  handleMessage(Message msg) {
        	int sumResponseCount = 0;
        	for(ResponseCategory c : counters) {
				sumResponseCount += c.getCount();
			}
			Button btnViewResponses = (Button)GeotaggingCommentContent.this.findViewById(R.id.comment_content_btn_see);
	        btnViewResponses.setText("See follow-up responses (" + String.valueOf(sumResponseCount) +")");
	        if(sumResponseCount == 0) {
	        	btnViewResponses.setClickable(false);
	        } else {
	        	btnViewResponses.setClickable(true);
	        }
        }
    };
	
	/** Handle "home" title-bar action. */
    public void onHomeClick(View v) {
        UIUtils.goHome(this);
    }
    
    public void onBackClick(View v) {
    	UIUtils.goBack(this);
    }
    
    public void onAlertClick(View v) {
    	Intent intent = new Intent();
		intent.setClassName("geotagging.app","geotagging.app.GeotaggingAlert");
		this.startActivity(intent);
    }
    
    public void onClearAllClick(View v) {
    	Intent intent = new Intent();
		intent.setClassName("geotagging.app","geotagging.app.GeotaggingAlert");
		this.startActivity(intent);
    }
    
    public void onFollowUpCategoryClick(View v) {
    	Intent intent = new Intent();
		Bundle b = new Bundle();
		b.putInt("commentId",commentId);
		intent.setClassName("geotagging.app","geotagging.app.GeotaggingFollowUpCategories");
		intent.putExtras(b);
		startActivity(intent);
    }
    
    public void onComposeCategoryClick(View v) {
    	Intent intent = new Intent();
		Bundle b = new Bundle();
		b.putInt("commentId",commentId);
		intent.setClassName("geotagging.app","geotagging.app.GeotaggingCategoriesForResponding");
		intent.putExtras(b);
		startActivity(intent);
    }

}
