package geotagging.app;

import geotagging.DES.Comment;
import geotagging.realtime.UpdateFollowupCommentThread;
import geotagging.utils.BackendHelperSingleton;
import geotagging.utils.UIUtils;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class GeotaggingCommentDetails extends Activity {

	private ArrayAdapter<String> followup_response_adapter;
	private List<Comment> cs;
	
	private static final int NEW_COMMENT = 0x443322;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.comment_details);
        
        //get data from previous activity
        Bundle b = getIntent().getExtras();
        String userName = b.getString("userName");
        int commentId = b.getInt("commentId");
        String description = b.getString("description");
        String userImg = b.getString("userImg");
		
        TextView tvUserName = (TextView)this.findViewById(R.id.comment_detail_user_name);
        tvUserName.setText(userName);
        
        TextView tvDescription = (TextView)this.findViewById(R.id.comment_detail_content);
        tvDescription.setText(description);
        
        ImageView imUserImg = (ImageView)this.findViewById(R.id.comment_detail_user_image);
        imUserImg.setImageBitmap(BackendHelperSingleton.getInstance().fetchImage(userImg));
        
        
        Button buttonProblemReport = (Button) findViewById(R.id.buttonRespond);
        buttonProblemReport.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent();
            	intent.setClassName("geotagging.app","geotagging.app.GeotaggingEntityResponse");
            	startActivityForResult(intent, NEW_COMMENT);
            }
        });
        
        followup_response_adapter = new ArrayAdapter<String>(this, R.layout.entity_problems);
        
        ListView pairedListView = (ListView) findViewById(R.id.follow_response_list);
        pairedListView.setAdapter(followup_response_adapter);
        
        //
        UpdateFollowupCommentThread t = new UpdateFollowupCommentThread(this, commentId);
        t.start();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
		if (requestCode == NEW_COMMENT) {
            if (resultCode == RESULT_OK) {
            
            	followup_response_adapter.add("@"+data.getStringExtra("userName")+" said,"+data.getStringExtra("description"));
            }
		}
            
	}
	
	/** Handle "home" title-bar action. */
    public void onHomeClick(View v) {
        UIUtils.goHome(this);
    }

    /** Handle "refresh" title-bar action. */
    public void onRefreshClick(View v) {
    }

    /** Handle "search" title-bar action. */
    public void onSearchClick(View v) {
        UIUtils.goSearch(this);
    }
	
	public void updateAdapter(List<Comment> cs) {
    	this.cs = cs;
    	handler.sendEmptyMessage(0);
    }
    
    private Handler handler = new Handler() {
        public void  handleMessage(Message msg) {
             //update your view from here only.

    		for (int i = 0; i < cs.size(); i++) {
    			followup_response_adapter.add("@"+cs.get(i).getUserName()+" said,"+cs.get(i).getDescription());
            }
        	
        }
    };
	
}
