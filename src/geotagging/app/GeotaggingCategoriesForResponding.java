package geotagging.app;

import geotagging.DAL.GeoCategoryDAL;
import geotagging.DES.ResponseCategory;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class GeotaggingCategoriesForResponding extends Activity implements IResponseCounter {
	private GeoCategoryDAL categoryDAL;
	private List<ResponseCategory> categories;

	private static final int NEW_RESPONSE = 0x443322;
	private int commentId;
	
	private FollowUpCategoryAdapter mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_details);        
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		Bundle b = getIntent().getExtras();
        commentId = b.getInt("commentId");
        
        mAdapter = new FollowUpCategoryAdapter();
        
        //fetching the response categeory from local database
        categoryDAL = GeoCategoryDAL.getInstance();
        categories = categoryDAL.getResponseCategoriesByCommentId(commentId);
        for(int i=0; i<categories.size(); i++) {
        	mAdapter.addItem(categories.get(i));
        }
        
        ListView pairedListView = (ListView) findViewById(R.id.followup_categories_list);
        pairedListView.setAdapter(mAdapter);
        
        pairedListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Bundle b = new Bundle();
		    	b.putInt("comment_id", commentId);
		    	b.putInt("category_id", categories.get(arg2).getCategory_id());
		    	Intent intent = new Intent();
		    	intent.putExtras(b);
		    	intent.setClassName("geotagging.app","geotagging.app.GeotaggingEntityResponse");
		    	startActivityForResult(intent, NEW_RESPONSE);
			}
		});
        
//        UpdateResponseCounterThread thread = new UpdateResponseCounterThread(this, commentId);
//        thread.start();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
		if (requestCode == NEW_RESPONSE) {
            if (resultCode == RESULT_OK) {
            	this.finish();
            }
		}
            
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
                holder.importantImage = (ImageView)convertView.findViewById(R.id.entity_information_important_tag);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            ResponseCategory c = mData.get(position);
            holder.categoryName.setText(c.getName());
//            holder.counter.setText("("+String.valueOf(c.getCount())+")");
            //force clear the image to prevent caching behavior
            holder.importantImage.setImageDrawable(null);
            if(c.isImportanTag()) {	
//            	Log.i("-------------------------", String.valueOf(c.getName()));
	            holder.importantImage
	        	.setImageDrawable(GeotaggingCategoriesForResponding.this
	        							.getResources()
	        							.getDrawable(R.drawable.cg_icon_exclamation));
            }
            return convertView;
        }
 
    }
 
    public static class ViewHolder {
        public TextView categoryName;
        public TextView counter;
        public ImageView importantImage;
    }

}
