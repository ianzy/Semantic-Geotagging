package geotagging.views;

import geotagging.app.GeotaggingMap;
import geotagging.app.R;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class GeotaggingMapView extends MapView {
	
	private long startTime;
	private long endTime;
	//used for removing tp view in my map view
	private long clickStartTime;
	private TransparentPanel tp;
	//need to refactor
	private boolean tpRemoved = true;
	private GeotaggingMap mapActivity;

	public GeotaggingMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public GeotaggingMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if(ev.getAction() == MotionEvent.ACTION_DOWN){
	         //record the start time
			startTime = ev.getEventTime();
			clickStartTime = ev.getEventTime();
			endTime = ev.getEventTime()+600;
		}else if(ev.getAction() == MotionEvent.ACTION_UP){
			//record the end time
			endTime = ev.getEventTime();
		}else if(ev.getAction() == MotionEvent.ACTION_MOVE) {
			startTime = ev.getEventTime();
		}
	
		//verify, this is the long press situation
		if(endTime - startTime > 1000){
			//we have a 1000ms duration touch
			//propagate your own event
			long timespan = endTime - startTime;
			Log.i("test", "Long press works"+timespan);
			final GeoPoint p = this.getProjection().fromPixels(
	            (int) ev.getX(),
	            (int) ev.getY());
			Log.i("Long press result", p.getLatitudeE6()+", "+p.getLongitudeE6());
			
			if (false == tpRemoved) {
				this.removeView(this.tp);
			}
			this.addView(this.tp, new MapView.LayoutParams(260,60,p,
					MapView.LayoutParams.BOTTOM_CENTER));
			tpRemoved = false;
			
			//Street View on this GeoPoint
			Button btnStreetView = (Button) this.findViewById(R.id.streetview_button);
			btnStreetView.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	            	Uri geoUri = Uri.parse("google.streetview:cbll="+p.getLatitudeE6()/1000000f+","+p.getLongitudeE6()/1000000f+"&cbp=1,180,,0,1.0");
	            	Intent mapCall = new Intent(Intent.ACTION_VIEW, geoUri);  
	            	mapActivity.startActivity(mapCall); 
	            }
	        });
			
			//add entity button
			Button btnAdd = (Button) this.findViewById(R.id.add_inpanel_button);
			btnAdd.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	            	mapActivity.showDialog(1); 
	            }
	        });
			
			return true; //notify that you handled this event (do not propagate)
	    }
		
		if (endTime - clickStartTime < 500) {
			//remove the view when user click on other place
			if (!tpRemoved) {
				this.removeView(this.tp);
				tpRemoved = true;
			}
			
		}
	    return super.onTouchEvent(ev);
	}
	
	public void setCustomizedChildView(TransparentPanel tp) {
		this.tp = tp;
	}
	
	public void setGeotaggingMapActivity(GeotaggingMap mapActivity) {
		this.mapActivity = mapActivity;
	}
}
