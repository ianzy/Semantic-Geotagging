package geotagging.app;

import geotagging.DAL.GeoCategoryDAL;
import geotagging.DES.ResponseCategory;
import geotagging.realtime.UpdateResponseCounterThread;
import geotagging.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class GeotaggingFollowUpCategories extends Activity {
	

	private GeoCategoryDAL categoryDAL;
	private List<ResponseCategory> categories;

	private int commentId;
	
	private FollowUpCategoryAdapter mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.comment_details);
        
        //get data from previous activity
        Bundle b = getIntent().getExtras();
        String userName = b.getString("userName");
        commentId = b.getInt("commentId");
        String description = b.getString("description");
		
        TextView tvUserName = (TextView)this.findViewById(R.id.comment_detail_user_name);
        tvUserName.setText(userName+" said,");
        
        TextView tvDescription = (TextView)this.findViewById(R.id.comment_detail_content);
        tvDescription.setText(description);
        
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		Bundle b = getIntent().getExtras();
        commentId = b.getInt("commentId");
        
        mAdapter = new FollowUpCategoryAdapter();
        
        //fetching the response categeory from local database
        categoryDAL = new GeoCategoryDAL(this);
        categories = categoryDAL.getResponseCategoriesByCommentId(commentId);
        for(int i=0; i<categories.size(); i++) {
        	mAdapter.addItem(categories.get(i));
        }
        
        ListView pairedListView = (ListView) findViewById(R.id.followup_categories_list);
        pairedListView.setAdapter(mAdapter);
        
        pairedListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent();
				Bundle b = new Bundle();
            	b.putInt("commentId", commentId);
            	b.putInt("category_id", categories.get(arg2).getCategory_id());
//            	Log.i("!!!!!!!!!!!!!!!!!!!!!!id", String.valueOf(categories.get(arg2).getCategory_id()));
				intent.setClassName("geotagging.app","geotagging.app.GeotaggingFollowUpList");
				b.putString("responseCategory", categories.get(arg2).getName());
				intent.putExtras(b);
				startActivity(intent);
			}
		});
        
        UpdateResponseCounterThread thread = new UpdateResponseCounterThread(this, commentId);
        thread.start();
	}
	
	//We must use a Handler object because we cannot update most UI 
    //objects while in a separate thread. 
    //When we send a message to the Handler it will get saved into 
    //a queue and get executed by the UI thread as soon as possible.
	private List<ResponseCategory> counters;
    public void updateCounter(List<ResponseCategory> counters) {
    	this.counters = counters;
    	handler.sendEmptyMessage(0);
    }
    
    private Handler handler = new Handler() {
        public void  handleMessage(Message msg) {
             //update your view from here only.
        	for (int i=0;i<counters.size();i++) {
        		mAdapter.updateItem(i, counters.get(i));
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

    private class FollowUpCategoryAdapter extends BaseAdapter {
		 
        private List<ResponseCategory> mData;
        private LayoutInflater mInflater;
 
        public FollowUpCategoryAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mData = new ArrayList<ResponseCategory>();
        }
 
        public void addItem(final ResponseCategory category) {
            mData.add(category);
            notifyDataSetChanged();
        }
        
        public void updateItem(int position, ResponseCategory category) {
        	ResponseCategory c = this.getItem(position);
        	c.setCategory_id(category.getCategory_id());
        	c.setCount(category.getCount());
        	c.setImportanTag(category.isImportanTag());
        	notifyDataSetChanged();
        }
 
        public int getCount() {
            return mData.size();
        }
 
        public ResponseCategory getItem(int position) {
            return mData.get(position);
        }
 
        public long getItemId(int position) {
            return position;
        }
 
        
        public View getView(int position, View convertView, ViewGroup parent) {
            System.out.println("getView " + position + " " + convertView);
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.entity_information, null);
                holder = new ViewHolder();
                holder.categoryName = (TextView)convertView.findViewById(R.id.comemnt_category);
                holder.counter = (TextView)convertView.findViewById(R.id.comment_category_counter);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            ResponseCategory c = mData.get(position);
            holder.categoryName.setText(c.getName());
            holder.counter.setText("("+String.valueOf(c.getCount())+")");
            return convertView;
        }
 
    }
 
    public static class ViewHolder {
        public TextView categoryName;
        public TextView counter;
    }

}
