package geotagging.app;

import geotagging.DES.Entity;
import geotagging.realtime.UpdateMapThread;
import geotagging.utils.CustomArrayAdapter;
import geotagging.utils.GeotaggingItemizedOverlay;
import geotagging.utils.UIUtils;
import geotagging.utils.UserIndicationOverlay;
import geotagging.views.BaloonInMapView;
import geotagging.views.GeotaggingMapView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

//Todo: use EVENT to handle the UI elements update
//http://10.0.2.2:3000/
public class GeotaggingMap extends MapActivity {
	
//	private GestureDetector mGestureDetector;
	
	private static final int DIALOG_FOR_ENTITY_TYPES = 1;
	
	// hard coded data, need to be refactored
	public static final String[] HARDCODED_DATA = new String[] { //Need to refactored to dynamic data
		"Fire", "Crime", "Earthquake", "Explosion", "Gas leak"
	};
	public static final int[] DRAWABLES = new int[] {
		R.drawable.fire_icon_small,
		R.drawable.crime,
		R.drawable.earthquake,
		R.drawable.explosion_icon,
		R.drawable.nuclear
	};
	public static final String[] ICONNAMES = new String[] {
		"fireicon",
		"crime",
		"earthquake",
		"explosionicon",
		"gasleak"
	};
	
	private static final int DIALOG_FOR_MAPMODE = 2;
	private static final String[] ITEMS_FOR_MAPMODE = new String[] {
		"Map","Satellite", "Street View","Traffic"
	};
	private BaloonInMapView baloon; //Balloon layout
	private GeotaggingItemizedOverlay itemizedoverlay; //Overlay for the update thread
	private UserIndicationOverlay userIndication;
	
	private GeotaggingMapView mapView;
	private Boolean isIconClick = false;
	
	
	private GeoPoint point;
	private String location;
	
	private boolean userIndicationFlag = true;
	private boolean entityIconsFlag = true;
	
	public boolean isEntityIconsFlag() {
		return entityIconsFlag;
	}

	public void setEntityIconsFlag(boolean entityIconsFlag) {
		this.entityIconsFlag = entityIconsFlag;
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ProgressDialog MyDialog = ProgressDialog.show( this, "Loading. " , " Please wait ... ", true);
        
        setContentView(R.layout.geotagging_map);
        
        mapView = (GeotaggingMapView) this.findViewById(R.id.mapview);
        mapView.addObserver(this);
        //End of experiment
        
        initializeViews();
        checkNetworkAvailability();
        initializeMap();
        initializeUserLocation();
        
        MyDialog.dismiss();
	}
	
	/** Handle "refresh" title-bar action. */
    public void onRefreshClick(View v) {
        // trigger off background sync
    	findViewById(R.id.btn_title_refresh).setVisibility(
                View.GONE );
        findViewById(R.id.title_refresh_progress).setVisibility(
                View.VISIBLE);
        Drawable drawable = this.getResources().getDrawable(R.drawable.fire_icon_small);
    	UpdateMapThread updateMapT = new UpdateMapThread(drawable,this, itemizedoverlay, UpdateMapThread.SYNC_MODE);
        updateMapT.start();
    }
    
    public void dismissProgressBar() {
    	handler.sendEmptyMessage(0);
    }
    
    private Handler handler = new Handler() {
        public void  handleMessage(Message msg) {
             //update your view from here only.
        	findViewById(R.id.btn_title_refresh).setVisibility(
                    View.VISIBLE );
            findViewById(R.id.title_refresh_progress).setVisibility(
                    View.GONE);
        	
        }
    };

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
//            	entity.setUpdatedAt();
            	entity.setDescription(data.getStringExtra("description"));
            	entity.setLat(String.valueOf(data.getDoubleExtra("lat", 0)));
            	entity.setLng(String.valueOf(data.getDoubleExtra("lng", 0)));
            	entity.setTitle(data.getStringExtra("title"));
            	entity.setId(data.getIntExtra("entity_id", -1));
            	entity.setIconURI(data.getStringExtra("iconName"));
            	
            	GeoPoint point = new GeoPoint(
                    (int) (data.getDoubleExtra("lat", 0) * 1E6), 
                    (int) (data.getDoubleExtra("lng", 0) * 1E6));
                
                
                OverlayItem overlayitem = new OverlayItem(point, "Opening Entity Information", "Opening Entity Information");
                
//              // code for changing the icons
//                	Drawable d = this.getResources().getDrawable(R.drawable.amphitheater);
//    	            d.setBounds(-10, -20, d.getIntrinsicWidth()-10, d.getIntrinsicHeight()-20);
//    	            overlayitem.setMarker(d);
                
                Drawable d = this.getResources().getDrawable(data.getIntExtra("drawableId", -1));
                d.setBounds(-10, -20, d.getIntrinsicWidth()-10, d.getIntrinsicHeight()-20);
                overlayitem.setMarker(d);
                
                itemizedoverlay.addOverlay(overlayitem);
                itemizedoverlay.addEntity(entity);
                //mapView.getOverlays().add(itemizedoverlay);
                this.dismissDialog(DIALOG_FOR_ENTITY_TYPES);
                
                //start the detail information activity right away after the submition
                Intent intent = new Intent();
            	intent.setClassName("geotagging.app","geotagging.app.GeotaggingEntityInformation");
            	Bundle b = new Bundle();
            	b.putInt("entityId", entity.getId());
            	b.putString("entity_title", entity.getTitle());
            			
            	b.putInt("icon", data.getIntExtra("drawableId", -1));
            	intent.putExtras(b);
            	startActivity(intent);
            }
        }
    }
	//Initialize the content of the dialogs
	 @Override
	    protected Dialog onCreateDialog(int id) {
		 switch (id) {
	        case DIALOG_FOR_ENTITY_TYPES:
//	        	return new AlertDialog.Builder(GeotaggingMap.this)
//                .setTitle("The new entity is a:")
//                .setItems(HARDCODED_DATA, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//
////                    	Toast.makeText(getApplicationContext(), "You selected" +HARDCODED_DATA[which],
////                		Toast.LENGTH_SHORT).show();
////                		startActivity(newEntityIntent);
//                		startActivityForResult(newEntityIntent, ADD_ENTITY);
//                    }
//                })
//                .create();
	        	LayoutInflater factory = LayoutInflater.from(this);
	            View customDialogView = factory.inflate(R.layout.alert_dialog_list_type_entity, null);
	            ListView pairedListView = (ListView)customDialogView.findViewById(R.id.alert_dialog_list_types);
//	            ArrayAdapter<String> entity_types_adapter = new ArrayAdapter<String>(this, R.layout.alert_dialog_list_type_item, R.id.dialog_item_content);
	            CustomArrayAdapter entity_types_adapter = new CustomArrayAdapter(this, R.layout.alert_dialog_list_type_item, R.id.dialog_item_content, DRAWABLES);
//	            View itemtmp = factory.inflate(R.layout.alert_dialog_list_type_item, null);
	            for(int i=0; i<HARDCODED_DATA.length; i++) {
	            	entity_types_adapter.add(HARDCODED_DATA[i]);
//	            	View item = entity_types_adapter.getView(i, itemtmp, pairedListView);
//	            	ImageView img = (ImageView)item.findViewById(R.id.dialog_item_icon);
//	            	img.setImageResource(R.drawable.default_user_icon);
	            }
	            pairedListView.setAdapter(entity_types_adapter);
	            pairedListView.setOnItemClickListener(new OnItemClickListener() {
	    			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
	    					long arg3) {
	    				Intent newEntityIntent = new Intent();
	    				newEntityIntent.setClassName("geotagging.app","geotagging.app.GeotaggingCommentsType");
	    		    	
	    				Bundle b = new Bundle();
	    		    	b.putString("location", location);
	    		    	b.putDouble("lng", point.getLongitudeE6() / 1E6);
	    		    	b.putDouble("lat", point.getLatitudeE6() / 1E6);
	    		    	b.putInt("drawableId", DRAWABLES[(int)arg3]);
	    		    	b.putString("iconName", ICONNAMES[(int)arg3]);
	    		    	Log.i("+++++++++++++++++++++", HARDCODED_DATA[(int)arg3]);
	    		    	Log.i("+++++++++++++++++++++", String.valueOf(DRAWABLES[(int)arg3]));
	    		    	newEntityIntent.putExtras(b);
	    				startActivityForResult(newEntityIntent, ADD_ENTITY);
	    			}
	    		});
	            
	            
	        	return new AlertDialog.Builder(GeotaggingMap.this)
                .setTitle("The new entity is a:")
                .setView(customDialogView)
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
	
	//fetch user location information
	private void initializeUserLocation() {

		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
	    public void onLocationChanged(Location location) {
	      // Called when a new location is found by the network location provider.
	    	GeoPoint point = new GeoPoint(
                    (int) (location.getLatitude() * 1E6), 
                    (int) (location.getLongitude() * 1E6));
                
                
            OverlayItem overlayitem = new OverlayItem(point, "You're here", String.valueOf(location.getLatitude())+", "+String.valueOf(location.getLongitude()));
            
            Drawable d = GeotaggingMap.this.getResources().getDrawable(R.drawable.red_dot);
            d.setBounds(-10, -20, d.getIntrinsicWidth()-10, d.getIntrinsicHeight()-20);
            overlayitem.setMarker(d);
            
            //this can be extended to drawing icons for different responder.
            userIndication.removeAllOverlays();
            userIndication.addOverlay(overlayitem);
            
            if(userIndicationFlag) {
            	userIndicationFlag = false;
            	mapView.getOverlays().add(userIndication);
            }
            
	    }

	    public void onStatusChanged(String provider, int status, Bundle extras) {}

	    public void onProviderEnabled(String provider) {}

	    public void onProviderDisabled(String provider) {}
	  };

	// Register the listener with the Location Manager to receive location updates
	  locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	
	  
		Drawable d = GeotaggingMap.this.getResources().getDrawable(R.drawable.red_dot);
		userIndication = new UserIndicationOverlay(d,GeotaggingMap.this);

	}
	
	private void checkNetworkAvailability () {
		URL url;
		URLConnection conn = null;
		InputStream inputstream = null;
		try {
			url = new URL(this.getResources().getString(R.string.GeotaggingAPIGetEntities));
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

        mapView.setBuiltInZoomControls(true);
        MapController mc = mapView.getController();
        String coordinates[] = {"37.410566","-122.059704"};
        double lat = Double.parseDouble(coordinates[0]);
        double lng = Double.parseDouble(coordinates[1]);
 
        GeoPoint p = new GeoPoint(
            (int) (lat * 1E6), 
            (int) (lng * 1E6));
        
        mc.animateTo(p);
        mc.setZoom(17);
        
        Drawable drawable = this.getResources().getDrawable(R.drawable.fire_icon_small);
        itemizedoverlay = new GeotaggingItemizedOverlay(drawable);
        itemizedoverlay.addObserver(this);
//        mapView.getOverlays().add(itemizedoverlay);
        UpdateMapThread updateMapT = new UpdateMapThread(drawable,this, itemizedoverlay, UpdateMapThread.INITIALIZE_MODE);
        updateMapT.start();
        
	}
	
	public void addOverlaysToMapView() {
		 mapView.getOverlays().add(itemizedoverlay);
	}
	
	//observer methods for the map view
	//act as an observer, map activity observe the map view, when the add entity click
	//this method get called. If necessery, it will turn into an interface.
	public void onAddEntityClick(GeoPoint point, String location) {
		this.point = point;
		this.location = location;
		this.showDialog(DIALOG_FOR_ENTITY_TYPES); 
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
		    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(250,190);
	        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
	        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
	        baloon.setLayoutParams(layoutParams);
	        //The close button
//	        ImageView imgView = (ImageView) baloon.findViewById(R.id.close_button);
//	        imgView.setOnClickListener(new OnClickListener() {
//	            public void onClick(View v) {
//	            	mapView.removeView(baloon);
//	            }
//	        });
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
		
		//change the width and height for the pop up window
		mapView.addView(baloon, new MapView.LayoutParams(290,250,p,
				MapView.LayoutParams.BOTTOM_CENTER));
		
		TextView title = (TextView) baloon.findViewById(R.id.txv_title);
		title.setText(selectedEntity.getTitle());
		TextView description = (TextView) baloon.findViewById(R.id.txv_description);
		description.setText(selectedEntity.getDescription());
		
		final int id = selectedEntity.getId();
		final String entityTitle = selectedEntity.getTitle();
		final String icon = selectedEntity.getIconURI();
		
		Button btnDetail = (Button) baloon.findViewById(R.id.btn_detail);
		btnDetail.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
        		Intent intent = new Intent();
            	intent.setClassName("geotagging.app","geotagging.app.GeotaggingEntityInformation");
            	Bundle b = new Bundle();
            	b.putInt("entityId", id);
            	b.putString("entity_title", entityTitle);
            	
            	int iconDrawable = -1;
            	for(int i=0; i<ICONNAMES.length; i++)
            		if(ICONNAMES[i].equals(icon)) {
            			iconDrawable = DRAWABLES[i];
            			break;
            		}
            			
            	b.putInt("icon", iconDrawable);
            	intent.putExtras(b);
            	startActivity(intent);
            }
        });
		 
	}
}
