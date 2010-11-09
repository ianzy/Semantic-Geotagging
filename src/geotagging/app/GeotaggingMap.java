package geotagging.app;

import geotagging.realtime.UpdateMapThread;
import geotagging.utils.GeotaggingItemizedOverlay;
import geotagging.views.BaloonInMapView;
import geotagging.views.GeotaggingMapView;
import geotagging.views.TransparentPanel;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class GeotaggingMap extends MapActivity {
	
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
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geotagging_map);
        
        //Experiment code, should be deleted
        
        //Start the street view for a given GeoPoint
        ImageButton imgbtn = (ImageButton) this.findViewById(R.id.search_imgbutton);
        imgbtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Uri geoUri = Uri.parse("google.streetview:cbll=46.813812,-71.207378&cbp=1,99.56,,1,-5.27&mz=21");
            	Intent mapCall = new Intent(Intent.ACTION_VIEW, geoUri);  
            	startActivity(mapCall); 
            }
        });
        
        //Inflate for the longpress layout
        TransparentPanel tp;
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        tp = (TransparentPanel)layoutInflater.inflate(R.layout.long_press_layout, null);
        GeotaggingMapView mapView = (GeotaggingMapView) this.findViewById(R.id.mapview);
        mapView.setCustomizedChildView(tp);
        mapView.setGeotaggingMapActivity(this);
        //End of experiment
        
        initializeViews();
        checkNetworkAvailability();
        initializeMap();
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

                    	Toast.makeText(getApplicationContext(), "You selected" +HARDCODED_DATA[which],
                		Toast.LENGTH_SHORT).show();
                		Intent intent = new Intent();
                    	intent.setClassName("geotagging.app","geotagging.app.GeotaggingCommentsType");
                    	startActivity(intent);
                    }
                })
                .create();
	        case DIALOG_FOR_MAPMODE:
	        	return new AlertDialog.Builder(GeotaggingMap.this)
                .setTitle("Select a map mode:")
                .setItems(ITEMS_FOR_MAPMODE, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    	Toast.makeText(getApplicationContext(), "You selected" +ITEMS_FOR_MAPMODE[which],
                		Toast.LENGTH_SHORT).show();
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
        Button button = (Button) findViewById(R.id.btnNewEntity);
        button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				showDialog(DIALOG_FOR_ENTITY_TYPES);
        }});
        
        //Popup window in the mapview
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
            	baloon.setVisibility(View.GONE);
            }
        });
        Button btnCancel = (Button) baloon.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	baloon.setVisibility(View.GONE);
            }
        });
	}
	
	private void checkNetworkAvailability () {
		URL url;
		try {
			url = new URL(this.getResources().getString(R.string.GeotaggingAPIUri));
			URLConnection conn = url.openConnection();
			conn.getInputStream();
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
        Drawable drawable = this.getResources().getDrawable(R.drawable.amphitheater);
        itemizedoverlay = new GeotaggingItemizedOverlay(drawable, this, mc, this.baloon, mapView);
        UpdateMapThread updateMapT = new UpdateMapThread(drawable,this,mapOverlays, itemizedoverlay);
        updateMapT.start();
       
	}
	
}
