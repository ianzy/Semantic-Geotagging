package geotagging.app;

import geotagging.DES.Entity;
import geotagging.realtime.UpdateMapThread;
import geotagging.utils.GeotaggingItemizedOverlay;
import geotagging.utils.UIUtils;
import geotagging.views.BaloonInMapView;
import geotagging.views.GeotaggingMapView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

//Todo: use EVENT to handle the UI elements update
//http://10.0.2.2:3000/
public class GeotaggingMap extends MapActivity {
	
//	private GestureDetector mGestureDetector;
	
	private static final int DIALOG_FOR_ENTITY_TYPES = 1;
	private static final String[] HARDCODED_DATA = new String[] { //Need to refactored to dynamic data
		"Plane crash", "Airfield fire", "Pumper unit 7", "Personnel", "Something new"
	};
	private static final int DIALOG_FOR_MAPMODE = 2;
	private static final String[] ITEMS_FOR_MAPMODE = new String[] {
		"Map","Satellite", "Street View","Traffic"
	};
	private BaloonInMapView baloon; //Balloon layout
	private GeotaggingItemizedOverlay itemizedoverlay; //Overlay for the update thread
	
	private GeotaggingMapView mapView;
	private Boolean isIconClick = false;
	
	private Intent newEntityIntent;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geotagging_map);
        
        //Experiment code
//        this.mGestureDetector = new GestureDetector(this);
        //Start the street view for a given GeoPoint
//        ImageButton imgbtn = (ImageButton) this.findViewById(R.id.search_imgbutton);
//        imgbtn.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//            	Uri geoUri = Uri.parse("google.streetview:cbll=46.813812,-71.207378&cbp=1,99.56,,1,-5.27&mz=21");
//            	Intent mapCall = new Intent(Intent.ACTION_VIEW, geoUri);  
//            	startActivity(mapCall); 
//            }
//        });
        
//        mapview = (MapView) this.findViewById(R.id.mapview);
        //Inflate for the longpress layout
        
        mapView = (GeotaggingMapView) this.findViewById(R.id.mapview);
        mapView.addObserver(this);
        //End of experiment
        
        initializeViews();
        checkNetworkAvailability();
        initializeMap();
	}
	
	/** Handle "refresh" title-bar action. */
    public void onRefreshClick(View v) {
        // trigger off background sync

    }

    /** Handle "search" title-bar action. */
    public void onSearchClick(View v) {
        UIUtils.goSearch(this);
    }

	
	//experimental codes, handling a successful return of an activity
	static final int ADD_ENTITY = 12345;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        if (requestCode == ADD_ENTITY) {
            if (resultCode == RESULT_OK) {
            	
            	Entity entity = new Entity();
            	entity.setLocation(data.getStringExtra("location"));
            	entity.setUpdatedAt(Long.parseLong("1288794810002"));
            	entity.setDescription(data.getStringExtra("description"));
            	entity.setLat(String.valueOf(data.getDoubleExtra("lat", 0)));
            	entity.setLng(String.valueOf(data.getDoubleExtra("lng", 0)));
            	entity.setTitle(data.getStringExtra("title"));
            	
            	GeoPoint point = new GeoPoint(
                    (int) (data.getDoubleExtra("lat", 0) * 1E6), 
                    (int) (data.getDoubleExtra("lng", 0) * 1E6));
                
                
                OverlayItem overlayitem = new OverlayItem(point, "Opening Entity Information", "Opening Entity Information");
                
                if (entity.getUpdatedAt() > Long.parseLong("1288794810000")) {
                	Drawable d = this.getResources().getDrawable(R.drawable.amphitheater);
    	            d.setBounds(-10, -20, d.getIntrinsicWidth()-10, d.getIntrinsicHeight()-20);
    	            overlayitem.setMarker(d);
                }
//                    Drawable d = cx.getResources().getDrawable(R.drawable.androidmarker);
//                    d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
//                    overlayitem.setMarker(d);
                itemizedoverlay.addOverlay(overlayitem);
                itemizedoverlay.addEntity(entity);
                mapView.getOverlays().add(itemizedoverlay);
            }
        }
    }
	//Initialize the content of the dialogs
	 @Override
	    protected Dialog onCreateDialog(int id) {
		 switch (id) {
	        case DIALOG_FOR_ENTITY_TYPES:
	        	return new AlertDialog.Builder(GeotaggingMap.this)
                .setTitle("I want to comment on:")
                .setItems(HARDCODED_DATA, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

//                    	Toast.makeText(getApplicationContext(), "You selected" +HARDCODED_DATA[which],
//                		Toast.LENGTH_SHORT).show();
//                		startActivity(newEntityIntent);
                		startActivityForResult(newEntityIntent, ADD_ENTITY);
                    }
                })
                .create();
	        case DIALOG_FOR_MAPMODE:
	        	return new AlertDialog.Builder(GeotaggingMap.this)
                .setTitle("Select a map mode:")
                .setItems(ITEMS_FOR_MAPMODE, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

//                    	Toast.makeText(getApplicationContext(), "You selected" +ITEMS_FOR_MAPMODE[which],
//                		Toast.LENGTH_SHORT).show();
                    	if ("Map" == ITEMS_FOR_MAPMODE[which]) {
                    		MapView mapView = (MapView) findViewById(R.id.mapview);
                			mapView.setSatellite(false);
                			mapView.setStreetView(false);
                			mapView.setTraffic(false);
                    	}else if ("Satellite" == ITEMS_FOR_MAPMODE[which]) {
                    	   //Maybe I need a instance variable to store the map view reference.
                			MapView mapView = (MapView) findViewById(R.id.mapview);
                			mapView.setSatellite(true);
                		} else if ("Street View" == ITEMS_FOR_MAPMODE[which]) {
                			MapView mapView = (MapView) findViewById(R.id.mapview);
                			mapView.setStreetView(true);
                		} else if ("Traffic" == ITEMS_FOR_MAPMODE[which]) {
                			MapView mapView = (MapView) findViewById(R.id.mapview);
                			mapView.setTraffic(true);
                		}
                			
                    }
                })
                .create();
		 }
	     return null;
	 }
	
	//Override method for menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.map_menu, menu);
	    return true;
	}
	
	//Method handling the click event on menu
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.add_item:
	        
	        return true;
	    case R.id.search_item:
	    	//This does not belong to here, just for experiments
	        showDialog(DIALOG_FOR_MAPMODE);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	//Private method goes here
	//Initialize any views will appear on the MapView
	private void initializeViews() {
		//New Entity Button
//        Button button = (Button) findViewById(R.id.btnNewEntity);
//        button.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View view) {
//				showDialog(DIALOG_FOR_ENTITY_TYPES);
//        }});
        
        //Popup window in the mapview
//        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        baloon = (BaloonInMapView)layoutInflater.inflate(R.layout.baloon_layout, null);
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(230,50);
//        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
//        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//        baloon.setLayoutParams(layoutParams);
//        //The close button
//        ImageView imgView = (ImageView) baloon.findViewById(R.id.close_button);
//        imgView.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//            	baloon.setVisibility(View.GONE);
//            }
//        });
//        Button btnCancel = (Button) baloon.findViewById(R.id.btn_cancel);
//        btnCancel.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//            	baloon.setVisibility(View.GONE);
//            }
//        });
       
	}
	
	private void checkNetworkAvailability () {
		URL url;
		URLConnection conn = null;
		InputStream inputstream = null;
		try {
			url = new URL(this.getResources().getString(R.string.GeotaggingAPIUri));
			conn = url.openConnection();
			conn.setConnectTimeout(1000);
			inputstream = conn.getInputStream();
			inputstream.close();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			Log.i("test", "inside where i want onCreate");
			CharSequence text = "Remote server unavailable";
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(this, text, duration);
			toast.show();
			
		}
	}
	
	
	private void initializeMap() {
		MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        MapController mc = mapView.getController();
        String coordinates[] = {"37.623019","-122.441704"};
        double lat = Double.parseDouble(coordinates[0]);
        double lng = Double.parseDouble(coordinates[1]);
 
        GeoPoint p = new GeoPoint(
            (int) (lat * 1E6), 
            (int) (lng * 1E6));
 
        mc.animateTo(p);
        mc.setZoom(17);
        
        List<Overlay> mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.fire_icon);
        itemizedoverlay = new GeotaggingItemizedOverlay(drawable);
        itemizedoverlay.addObserver(this);
        UpdateMapThread updateMapT = new UpdateMapThread(drawable,this,mapOverlays, itemizedoverlay);
        updateMapT.start();
        
	}
	
	//observer methods for the map view
	//act as an observer, map activity observe the map view, when the add entity click
	//this method get called. If necessery, it will turn into an interface.
	public void onAddEntityClick(GeoPoint point, String location) {
		newEntityIntent = new Intent();
		newEntityIntent.setClassName("geotagging.app","geotagging.app.GeotaggingCommentsType");
    	
		Bundle b = new Bundle();
    	b.putString("location", location);
    	b.putDouble("lng", point.getLongitudeE6() / 1E6);
    	b.putDouble("lat", point.getLatitudeE6() / 1E6);
    	newEntityIntent.putExtras(b);
		this.showDialog(1); 
	}
	
	public void onStreetViewClick(Intent intent) {
		this.startActivity(intent); 
	}
	
	//observer methods for GeotaggingItemizedOverlay
	public void onIconTapped(GeoPoint p, Entity selectedEntity) {
		Point screenPts = new Point();
		MapController mc = mapView.getController();
		
		if (null == this.baloon) {
			LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        baloon = (BaloonInMapView)layoutInflater.inflate(R.layout.baloon_layout, null);
		    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(230,50);
	        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
	        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
	        baloon.setLayoutParams(layoutParams);
	        //The close button
	        ImageView imgView = (ImageView) baloon.findViewById(R.id.close_button);
	        imgView.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	            	mapView.removeView(baloon);
	            }
	        });
	        Button btnCancel = (Button) baloon.findViewById(R.id.btn_cancel);
	        btnCancel.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	            	mapView.removeView(baloon);
	            }
	        });
		}
		
		//map geopoint to px to ensure a correct center of map view
		mapView.getProjection().toPixels(p, screenPts);
		mc.animateTo(mapView.getProjection().fromPixels(screenPts.x, screenPts.y-80));
		
		if (isIconClick) {
			mapView.removeView(baloon);
		}
		isIconClick = true;
		
		mapView.addView(baloon, new MapView.LayoutParams(260,200,p,
				MapView.LayoutParams.BOTTOM_CENTER));
		
		TextView title = (TextView) baloon.findViewById(R.id.txv_title);
		title.setText(selectedEntity.getTitle());
		TextView description = (TextView) baloon.findViewById(R.id.txv_description);
		description.setText(selectedEntity.getDescription());
		
		final int id = selectedEntity.getId();
		
		Button btnDetail = (Button) baloon.findViewById(R.id.btn_detail);
		btnDetail.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
        		Intent intent = new Intent();
            	intent.setClassName("geotagging.app","geotagging.app.GeotaggingEntityInformation");
            	Bundle b = new Bundle();
            	b.putInt("entityId", id);
            	intent.putExtras(b);
            	startActivity(intent);
            }
        });
		 
	}
}
