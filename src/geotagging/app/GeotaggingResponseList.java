package geotagging.app;

import geotagging.DES.Response;
import geotagging.realtime.UpdateResponseThread;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GeotaggingResponseList extends ListActivity {

	private List<Response> rs;
	private UpdateResponseThread updateResponseThread;
	private MyCustomAdapter mAdapter;
	
	//experimental code
	private int icm = 1;
	
	private Button refresh;
	 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new MyCustomAdapter();
        setContentView(R.layout.response_list_layout);
        setListAdapter(mAdapter);
        
        refresh = (Button) findViewById(R.id.response_refresh_button);
        refresh.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				setThread("14");
		        updateResponseThread.start();
		        refresh.setClickable(false);
			}
        	
        });
        
        //get entity_id from previous activity
        Bundle b = getIntent().getExtras();
        String entity_id = b.getString("entity_id");
        updateResponseThread = new UpdateResponseThread(this, entity_id);
        updateResponseThread.start();
    }
    //experimental code
    private void setThread(String entity_id) { 
    	Integer res = Integer.parseInt(entity_id)+icm++;
    	entity_id = res.toString();
		updateResponseThread = new UpdateResponseThread(this, entity_id);
    }
    
    //We must use a Handler object because we cannot update most UI 
    //objects while in a separate thread. 
    //When we send a message to the Handler it will get saved into 
    //a queue and get executed by the UI thread as soon as possible.
    public void updateAdapter(List<Response> rs) {
    	this.rs = rs;
    	handler.sendEmptyMessage(0);
    	refresh.setClickable(true);
    }
    
    private Handler handler = new Handler() {
        public void  handleMessage(Message msg) {
             //update your view from here only.

    		for (int i = 0; i < rs.size(); i++) {
    			mAdapter.addItem(rs.get(i));
            }
        	
        }
    };
 
    private class MyCustomAdapter extends BaseAdapter {
 
        private ArrayList<Response> mData = new ArrayList<Response>();
        private LayoutInflater mInflater;
 
        public MyCustomAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
 
        public void addItem(final Response resp) {
            mData.add(0, resp);
            notifyDataSetChanged();
        }
 
        public int getCount() {
            return mData.size();
        }
 
        public Response getItem(int position) {
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
            Response r = mData.get(position);
            holder.content.setText(r.getResp());
            holder.image.setImageBitmap(r.getActualImg());
            holder.time.setText(r.getTime());
            holder.username.setText(r.getUsername());
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


