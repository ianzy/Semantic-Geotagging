package geotagging.app;

import geotagging.DES.Comment;
import geotagging.realtime.UpdateCommentThread;
import geotagging.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class GeotaggingCommentsList extends ListActivity {
	private List<Comment> cs;
	private UpdateCommentThread updateCommentThread;
	private MyCustomAdapter mAdapter;

	 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new MyCustomAdapter();
        setContentView(R.layout.response_list_layout);
        setListAdapter(mAdapter);
        
        //Set the onclick event listener for listview items
        ListView pairedListView = this.getListView();
        pairedListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				Log.i(">>>>>>>>>>>>>>>>>>>>>>>>>", String.valueOf(arg2));
				Intent intent = new Intent();
				
				Comment c = cs.get(cs.size()-1-arg2);
				Bundle b = new Bundle();
				b.putInt("commentId",c.getCommentId());
				b.putString("description", c.getDescription());
				b.putString("userName", c.getUserName());
				b.putString("userImg", c.getUserImg());
				intent.setClassName("geotagging.app","geotagging.app.GeotaggingCommentDetails");
				intent.putExtras(b);
				startActivity(intent);
			}
		});
        
        //get entity_id from previous activity
        Bundle b = getIntent().getExtras();
        String entity_id = b.getString("entity_id");
        updateCommentThread = new UpdateCommentThread(this, entity_id);
        updateCommentThread.start();
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
    
    //We must use a Handler object because we cannot update most UI 
    //objects while in a separate thread. 
    //When we send a message to the Handler it will get saved into 
    //a queue and get executed by the UI thread as soon as possible.
    public void updateAdapter(List<Comment> cs) {
    	this.cs = cs;
    	handler.sendEmptyMessage(0);
    }
    
    private Handler handler = new Handler() {
        public void  handleMessage(Message msg) {
             //update your view from here only.

    		for (int i = 0; i < cs.size(); i++) {
    			mAdapter.addItem(cs.get(i));
            }
        	
        }
    };
 
    private class MyCustomAdapter extends BaseAdapter {
 
        private ArrayList<Comment> mData = new ArrayList<Comment>();
        private LayoutInflater mInflater;
 
        public MyCustomAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
 
        public void addItem(final Comment comment) {
            mData.add(0, comment);
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
                holder.image = (ImageView)convertView.findViewById(R.id.image_list_item);
                holder.time = (TextView)convertView.findViewById(R.id.time_list_item);
                holder.content = (TextView)convertView.findViewById(R.id.content_list_item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            Comment c = mData.get(position);
            holder.content.setText(c.getDescription());
            holder.image.setImageBitmap(c.getActualUserImg());
            holder.time.setText(c.getTime());
            holder.username.setText(c.getUserName());
            return convertView;
        }
 
    }
 
    public static class ViewHolder {
        public TextView username;
        public TextView content;
        public ImageView image;
        public TextView time;
    }
}
