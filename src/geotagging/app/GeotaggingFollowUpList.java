package geotagging.app;

import geotagging.DES.Comment;
import geotagging.realtime.UpdateFollowupCommentThread;
import geotagging.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class GeotaggingFollowUpList extends ListActivity {
	
	private static final int NEW_RESPONSE = 0x443122;
	private List<Comment> cs;
	private UpdateFollowupCommentThread updateFollowupCommentThread;
	private FollowUpListAdapter mAdapter;
	private int commentId;
	private int categoryId;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.response_list_layout);
        
        //get entity_id from previous activity
        Bundle b = getIntent().getExtras();
        commentId = b.getInt("commentId");
        categoryId = b.getInt("category_id");
//        Log.i("!!!!!!!!!!!!!!!!!!!!!!id", String.valueOf(categoryId));
        String categoryName = b.getString("responseCategory");
        TextView tv = (TextView)this.findViewById(R.id.title_name);
        tv.setText(categoryName);
        Button btnAdd = (Button)this.findViewById(R.id.add_comments_button);
        
        // Ugly code
        if(categoryName.charAt(categoryName.length()-1) == 's') {
        	categoryName = categoryName.substring(0, categoryName.length()-1);
        }
        btnAdd.setText("Add A New "+categoryName);
        
        mAdapter = (FollowUpListAdapter)getLastNonConfigurationInstance();
        if(null == mAdapter) {
        	mAdapter = new FollowUpListAdapter();
        	setListAdapter(mAdapter);
        	updateFollowupCommentThread = new UpdateFollowupCommentThread(this, commentId, UpdateFollowupCommentThread.INITIALIZE_MODE);
            updateFollowupCommentThread.start();
        } else {
        	setListAdapter(mAdapter);
        }
        
        
        
        //Set the onclick event listener for listview items
        ListView pairedListView = this.getListView();
        pairedListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				Log.i(">>>>>>>>>>>>>>>>>>>>>>>>>", String.valueOf(arg2));
				Intent intent = new Intent();
				
				Comment c = mAdapter.getItem(arg2);
				Bundle b = new Bundle();
				b.putInt("commentId",c.getEntity_id());
				b.putString("description", c.getDescription());
				b.putString("userName", c.getUserName());
				b.putString("userImg", c.getUserImg());
				intent.setClassName("geotagging.app","geotagging.app.GeotaggingFollowUpCategories");
				intent.putExtras(b);
				startActivity(intent);
			}
		});
        
    }

	@Override
    public Object onRetainNonConfigurationInstance() {
        return this.mAdapter;
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
    	// trigger off background sync
    	findViewById(R.id.btn_title_refresh).setVisibility(
                View.GONE );
        findViewById(R.id.title_refresh_progress).setVisibility(
                View.VISIBLE);
        updateFollowupCommentThread = new UpdateFollowupCommentThread(this, commentId, UpdateFollowupCommentThread.SYNC_MODE);
        updateFollowupCommentThread.start();
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
		if (requestCode == NEW_RESPONSE) {
            if (resultCode == RESULT_OK) {
//            	Log.i("~~~~~~~~~~~~~~~~~~~here", String.valueOf(this.categoryId));
//            	Log.i("~~~~~~~~~~~~~~~~~~~here", String.valueOf(data.getStringExtra("created_at")));
            	if(data.getIntExtra("category_id", -1) == this.categoryId) {
//            		Log.i("~~~~~~~~~~~~~~~~~~~inside if", String.valueOf(data.getIntExtra("category_id", -1)));
            		Comment c = new Comment();
	            	Drawable d = this.getResources().getDrawable(R.drawable.default_user_icon);
					c.setActualUserImg(((BitmapDrawable)d).getBitmap());
	            	c.setTime(data.getStringExtra("created_at"));
	            	c.setDescription(data.getStringExtra("description"));
	            	c.setUserName(data.getStringExtra("userName"));
	            	c.setEntity_id(data.getIntExtra("id", -1));
	            	c.setImportantTag(data.getBooleanExtra("important_tag", false));
//		            	String categoryName = data.getStringExtra("category_name");
	            	mAdapter.addItem(c);
	            	this.onRefreshClick(null);
            	}
            	
            }
		}
            
	}
    
    public void onComposeClick(View v) {
    	Bundle b = new Bundle();
    	b.putInt("comment_id", commentId);
    	b.putInt("category_id", this.categoryId);
    	Intent intent = new Intent();
    	intent.putExtras(b);
    	intent.setClassName("geotagging.app","geotagging.app.GeotaggingEntityResponse");
    	startActivityForResult(intent, NEW_RESPONSE);
    }

    /** Handle "search" title-bar action. */
    public void onSearchClick(View v) {
        UIUtils.goSearch(this);
    }
    
    //We must use a Handler object because we cannot update most UI 
    //objects while in a separate thread. 
    //When we send a message to the Handler it will get saved into 
    //a queue and get executed by the UI thread as soon as possible.
    public void updateAdapter(List<Comment> cs, int mode) {
    	this.cs = cs;
    	handler.sendEmptyMessage(mode);
    }
    
    private Handler handler = new Handler() {
        public void  handleMessage(Message msg) {
             //update your view from here only.
        	switch(msg.what) {
        	case UpdateFollowupCommentThread.INITIALIZE_MODE:
        		for (int i = 0; i < cs.size(); i++) {
        			if(cs.get(i).getCategory_id() == categoryId)
        				mAdapter.addItem(cs.get(i));
                }
        		break;
        	case UpdateFollowupCommentThread.SYNC_MODE:
        		boolean existFlag;
        		if(null != cs)
        		for (int i = 0; i < cs.size(); i++) {
        			existFlag = false;
        			for(int ii=0; ii<mAdapter.getCount(); ii++) {
        				if(mAdapter.getItem(ii).getEntity_id() == cs.get(i).getEntity_id()) {
        					existFlag = true;
        					break;
        				}
        					
        			}
        			if(existFlag) {
        				continue;
        			}
        			if(cs.get(i).getCategory_id() == categoryId) {
        				mAdapter.addItem(cs.get(i));
        			}
        			
                }
        		
        		findViewById(R.id.btn_title_refresh).setVisibility(
                        View.VISIBLE );
                findViewById(R.id.title_refresh_progress).setVisibility(
                        View.GONE);
        		break;
        	}
    		
        	
        }
    };
 
    private class FollowUpListAdapter extends BaseAdapter {
 
        private ArrayList<Comment> mData = new ArrayList<Comment>();
        private LayoutInflater mInflater;
 
        public FollowUpListAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
 
        public void addItem(final Comment comment) {
            mData.add(0,comment);
            notifyDataSetChanged();
        }
 
        public int getCount() {
            return mData.size();
        }
 
        public Comment getItem(int position) {
            return mData.get(position);
        }
 
        public long getItemId(int position) {
            return position;
        }
 
        
        public View getView(int position, View convertView, ViewGroup parent) {
            System.out.println("getView " + position + " " + convertView);
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item, null);
                holder = new ViewHolder();
                holder.username = (TextView)convertView.findViewById(R.id.title_list_item);
//	                holder.image = (ImageView)convertView.findViewById(R.id.image_list_item);
                holder.counter = (TextView)convertView.findViewById(R.id.counter_list_item);
                holder.time = (TextView)convertView.findViewById(R.id.time_list_item);
                holder.content = (TextView)convertView.findViewById(R.id.content_list_item);
                holder.importantImage = ((ImageView)convertView.findViewById(R.id.list_item_important_tag));
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            Comment c = mData.get(position);
            holder.content.setText(c.getDescription());
//	            holder.image.setImageBitmap(c.getActualUserImg());
            holder.counter.setText("+"+String.valueOf(c.getCommentCounter())+" more");
            holder.time.setText(UIUtils.TimeParser(c.getTime()));
            holder.username.setText(c.getUserName());
            
            holder.importantImage.setImageDrawable(null);
            if(c.isImportantTag()) {
            	holder.importantImage
            	.setImageDrawable(GeotaggingFollowUpList.this
            							.getResources()
            							.getDrawable(R.drawable.cg_icon_exclamation));
            }
            return convertView;
        }
 
    }
 
    public static class ViewHolder {
        public TextView username;
        public TextView content;
        public TextView counter;
//	        public ImageView image;
        public TextView time;
        public ImageView importantImage;
    }
}
