package geotagging.app;

import geotagging.DAL.GeoCategoryDAL;
import geotagging.DES.CommentCategory;
import geotagging.utils.UIUtils;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class GeotaggingEntityInformation extends Activity {
	
	private GeoCategoryDAL categoryDAL;
	private ArrayAdapter<String> entity_information_adapter;
	private List<CommentCategory> categories;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
   
        setContentView(R.layout.entity_information_list);
        
        //get entity id from previous activity
        Bundle bundleFromMapView = getIntent().getExtras();
        final int entityId = bundleFromMapView.getInt("entityId");
        
        entity_information_adapter = new ArrayAdapter<String>(this, R.layout.entity_information, R.id.comemnt_category);
        
        //fetch categories from local cache database
        categoryDAL = new GeoCategoryDAL(this);
        categories = categoryDAL.getCommentCategoriesByEntityId(entityId);
        CommentCategory category;
        for(int i=0; i<categories.size(); i++) {
        	category = categories.get(i);
        	entity_information_adapter.add("["+category.getCount()+"]"+"    "+category.getName());
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
}
