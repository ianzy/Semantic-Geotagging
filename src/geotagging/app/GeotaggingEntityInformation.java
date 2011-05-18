package geotagging.app;

import geotagging.DAL.GeoCategoryDAL;
import geotagging.DES.CommentCategory;
import geotagging.realtime.UpdateCommentCounterThread;
import geotagging.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class GeotaggingEntityInformation extends Activity {
	
	private GeoCategoryDAL categoryDAL;
	private CommentCategoryAdapter entity_information_adapter;
	private List<CommentCategory> categories;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entity_information_list);     
        
        Bundle b = getIntent().getExtras();
        String entityTitle = b.getString("entity_title");
        int entityIcon = b.getInt("icon");
        TextView title = (TextView)this.findViewById(R.id.entity_information_title);
        title.setText(entityTitle);
        ImageView icon = (ImageView)this.findViewById(R.id.entity_information_icon);
        icon.setImageDrawable(this.getResources().getDrawable(entityIcon));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//get entity id from previous activity
        Bundle bundleFromMapView = getIntent().getExtras();
        final int entityId = bundleFromMapView.getInt("entityId");
        
        entity_information_adapter = new CommentCategoryAdapter();
        
        //fetch categories from local cache database
        categoryDAL = GeoCategoryDAL.getInstance();
        categories = categoryDAL.getCommentCategoriesByEntityId(entityId);
        for(int i=0; i<categories.size(); i++) {
        	entity_information_adapter.addItem(categories.get(i));
        }
        
        ListView pairedListView = (ListView) findViewById(R.id.entity_information_list);
        pairedListView.setAdapter(entity_information_adapter);
        
        pairedListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent();
				Bundle b = new Bundle();
            	b.putInt("entity_id", entityId);
            	b.putInt("category_id", categories.get(arg2).getCategory_id());
				String information = ((TextView)((LinearLayout) arg1).getChildAt(0)).getText().toString();
				Log.i("~~~~~~~~~~~~~~~~~~~~~~~~", information);
				Log.i("~~~~~~~~~~~~~~~~~~~~~~~~", String.valueOf(categories.get(arg2).getCategory_id()));
				intent.setClassName("geotagging.app","geotagging.app.GeotaggingCommentsList");
				b.putString("commentCategory", categories.get(arg2).getName());
				intent.putExtras(b);
				startActivity(intent);
			}
		});
        
        UpdateCommentCounterThread thread = new UpdateCommentCounterThread(this, entityId);
        thread.start();
	}
	
	//We must use a Handler object because we cannot update most UI 
    //objects while in a separate thread. 
    //When we send a message to the Handler it will get saved into 
    //a queue and get executed by the UI thread as soon as possible.
	private List<CommentCategory> counters;
    public void updateCounter(List<CommentCategory> counters) {
    	this.counters = counters;
    	handler.sendEmptyMessage(0);
    }
    
    private Handler handler = new Handler() {
        public void  handleMessage(Message msg) {
             //update your view from here only.
        	for (int i=0;i<counters.size();i++) {
        		entity_information_adapter.updateItem(i, counters.get(i));
        	}
        }
    };
	
	
	 private class CommentCategoryAdapter extends BaseAdapter {
		 
        private List<CommentCategory> mData;
        private LayoutInflater mInflater;
 
        public CommentCategoryAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mData = new ArrayList<CommentCategory>();
        }
 
        public void addItem(final CommentCategory category) {
            mData.add(category);
            notifyDataSetChanged();
        }
 
        public int getCount() {
            return mData.size();
        }
 
        public CommentCategory getItem(int position) {
            return mData.get(position);
        }
 
        public long getItemId(int position) {
            return position;
        }
        
        public void updateItem(int position, CommentCategory category) {
        	CommentCategory c = this.getItem(position);
        	c.setCategory_id(category.getCategory_id());
        	c.setCount(category.getCount());
        	c.setImportanTag(category.isImportanTag());
        	notifyDataSetChanged();
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
            CommentCategory c = mData.get(position);
            holder.categoryName.setText(c.getName());
            holder.counter.setText("("+String.valueOf(c.getCount())+")");
            holder.importantImage.setImageDrawable(null);
            if(c.isImportanTag()) {	
	            holder.importantImage
	        	.setImageDrawable(GeotaggingEntityInformation.this
	        							.getResources()
	        							.getDrawable(R.drawable.cg_icon_exclamation));
            }
            return convertView;
        }
 
    }
 
    public static class ViewHolder {
        public TextView categoryName;
        public ImageView importantImage;
        public TextView counter;
    }
	
	@Override
	protected void onPause() {
		super.onPause();
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
    }

    /** Handle "search" title-bar action. */
    public void onSearchClick(View v) {
        UIUtils.goSearch(this);
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
}
