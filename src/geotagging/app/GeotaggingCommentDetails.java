package geotagging.app;

import geotagging.DAL.GeoCategoryDAL;
import geotagging.DES.Comment;
import geotagging.DES.ResponseCategory;
import geotagging.realtime.UpdateFollowupCommentThread;
import geotagging.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

public class GeotaggingCommentDetails extends Activity implements ExpandableListView.OnGroupClickListener,
ExpandableListView.OnChildClickListener{
	// used for asyn updates
	private List<Comment> cs;
	
	private GeoCategoryDAL categoryDAL;
	private List<ResponseCategory> categories;
	
	private static final int NEW_RESPONSE = 0x443322;
	private int commentId;
	
	private MyExpandableListAdapter mAdapter;
	
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
        
        mAdapter = new MyExpandableListAdapter();
        
//        ExpandableListView pairedListView = (ExpandableListView) findViewById(R.id.follow_response_list);
//        pairedListView.setAdapter(mAdapter);
//        pairedListView.setOnChildClickListener(this);
//        pairedListView.setOnGroupClickListener(this);
        
        //fetching the response categeory from local database
        categoryDAL = GeoCategoryDAL.getInstance();
        categories = categoryDAL.getResponseCategoriesByCommentId(commentId);
        
//        UpdateFollowupCommentThread t = new UpdateFollowupCommentThread(this, commentId, UpdateFollowupCommentThread.INITIALIZE_MODE);
//        t.start();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
		if (requestCode == NEW_RESPONSE) {
            if (resultCode == RESULT_OK) {
            	Comment c = new Comment();
            	Drawable d = this.getResources().getDrawable(R.drawable.default_user_icon);
				c.setActualUserImg(((BitmapDrawable)d).getBitmap());
            	c.setTime(data.getStringExtra("created_at"));
            	c.setDescription(data.getStringExtra("description"));
            	c.setUserName(data.getStringExtra("userName"));
            	c.setEntity_id(data.getIntExtra("id", -1));
            	String categoryName = data.getStringExtra("category_name");
            	List<Comment> cs = new ArrayList<Comment>();
            	cs.add(c);
            	
            	mAdapter.addResponsesWithGroup(categoryName, cs);
            }
		}
            
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
//        UpdateFollowupCommentThread t = new UpdateFollowupCommentThread(this, commentId, UpdateFollowupCommentThread.SYNC_MODE);
//        t.start();
    
    }
    
    public void onComposeClick(View v) {
    	Bundle b = new Bundle();
    	b.putInt("comment_id", commentId);
    	Intent intent = new Intent();
    	intent.putExtras(b);
    	intent.setClassName("geotagging.app","geotagging.app.GeotaggingEntityResponse");
    	startActivityForResult(intent, NEW_RESPONSE);
    }

    /** Handle "search" title-bar action. */
    public void onSearchClick(View v) {
        UIUtils.goSearch(this);
    }
	
	public void updateAdapter(List<Comment> cs, int mode) {
    	this.cs = cs;
    	handler.sendEmptyMessage(mode);
    }
    
    private Handler handler = new Handler() {
        public void  handleMessage(Message msg) {
            //update view from here only.
        	List<Comment> commetListOfSpecificCategory;
        	int category_id_categories;
        	
        	switch(msg.what) {
        	case UpdateFollowupCommentThread.INITIALIZE_MODE:
        		
            	for (int ii=0; ii<categories.size(); ii++) {
            		category_id_categories = categories.get(ii).getCategory_id();
            		commetListOfSpecificCategory = new ArrayList<Comment>();
            		for (int i = 0; i < cs.size(); i++) {
            			if(cs.get(i).getCategory_id() == category_id_categories) {
            				commetListOfSpecificCategory.add(cs.get(i));
            			}
        				
        			}
            		if(commetListOfSpecificCategory.size() != 0) {
            			mAdapter.addResponsesWithGroup(categories.get(ii).getName(), commetListOfSpecificCategory);
            		}
            		
                }
        		break;
        	case UpdateFollowupCommentThread.SYNC_MODE:
        		//ugly code....
        		boolean existingFlag = false;
        		if(null != cs)
            	for (int ii=0; ii<categories.size(); ii++) {
            		category_id_categories = categories.get(ii).getCategory_id();
            		commetListOfSpecificCategory = new ArrayList<Comment>();
            		for (int i = 0; i < cs.size(); i++) {
            			if(cs.get(i).getCategory_id() == category_id_categories) {
            				String groupName = categories.get(ii).getName();
            				List<Comment> existingComments = mAdapter.getChildren(groupName);
            				if(null != existingComments) {
            					existingFlag = false;
            					for(int j=0; j<existingComments.size(); j++) {
            						if(existingComments.get(j).getEntity_id() == cs.get(i).getEntity_id()) {
            							existingFlag = true;
            							break;
            						}	
            					}
            				}
            				if(existingFlag) {
            					break;
            				}
            				commetListOfSpecificCategory.add(cs.get(i));
            			}
        				
        			}
            		if(commetListOfSpecificCategory.size() != 0) {
            			mAdapter.addResponsesWithGroup(categories.get(ii).getName(), commetListOfSpecificCategory);
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
	
    public boolean onGroupClick(ExpandableListView parent, View v,
			int groupPosition, long id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		// TODO Auto-generated method stub
		
		return false;
	}
    
    private class MyExpandableListAdapter extends BaseExpandableListAdapter {

        private List<String> groups = new ArrayList<String>();; 
        private List<List<Comment>> children = new ArrayList<List<Comment>>(); 
        private LayoutInflater mInflater;
        
        public MyExpandableListAdapter() {
        	mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        
        public void addResponsesWithGroup(String p, List<Comment> commentList){
        	if(groups.contains(p)) {
        		int i = groups.indexOf(p);
        		List<Comment> existingCommentList = children.get(i);
        		existingCommentList.addAll(0, commentList);
        	} else {
        		groups.add(p);
            	children.add(commentList);
        	}
        	
        	notifyDataSetChanged();
        }
        
        public List<Comment> getChildren(String groupName) {
        	if(groups.contains(groupName)) {
        		int i = groups.indexOf(groupName);
        		return children.get(i);
        	}
        	return null;
        }
        
        public Object getChild(int groupPosition, int childPosition) {
            return children.get(groupPosition).get(childPosition);
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
            return children.get(groupPosition).size();  
        }

        public TextView getGenericView() {
            // Layout parameters for the ExpandableListView
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 64);

            TextView textView = new TextView(GeotaggingCommentDetails.this);
            textView.setLayoutParams(lp);
            // Center the text vertically
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            // Set the text starting position
            textView.setPadding(36, 0, 0, 0);
            textView.setTextSize(24);
            return textView;
        }
        
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) {
        	ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_response, null);
                holder = new ViewHolder();
                holder.username = (TextView)convertView.findViewById(R.id.response_list_user_name);
                holder.image = (ImageView)convertView.findViewById(R.id.list_item_user_img);
                holder.time = (TextView)convertView.findViewById(R.id.response_time_list_item);
                holder.content = (TextView)convertView.findViewById(R.id.response_content_list_item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            Comment c = children.get(groupPosition).get(childPosition);
            holder.content.setText(c.getDescription());
            holder.image.setImageBitmap(c.getActualUserImg());
            holder.time.setText(c.getTime());
            holder.username.setText(c.getUserName());
            return convertView;
        }

        public Object getGroup(int groupPosition) {
            return groups.get(groupPosition);
        }

        public int getGroupCount() {
            return groups.size();
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                ViewGroup parent) {
            TextView textView = getGenericView();
            textView.setText(getGroup(groupPosition).toString());
            return textView;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }     

    }

    public static class ViewHolder {
        public TextView username;
        public TextView content;
        public ImageView image;
        public TextView time;
    }
	
}
