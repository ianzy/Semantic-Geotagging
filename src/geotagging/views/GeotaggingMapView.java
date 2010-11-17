package geotagging.views;

import geotagging.app.GeotaggingMap;
import geotagging.app.R;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.widget.Button;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class GeotaggingMapView extends MapView implements OnGestureListener{
	
	private GestureDetector mGestureDetector;
	private TransparentPanel tp;
	//need to refactor
	private boolean tpRemoved = true;
	private GeotaggingMap observer;
	private Context context;

	public GeotaggingMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mGestureDetector = new GestureDetector(this);
		this.context = context;
	}
	
	public GeotaggingMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mGestureDetector = new GestureDetector(this);
		this.context = context;
	}
	
	//a weird use of observer pattern
	public void addObserver(GeotaggingMap mapActivity) {
		this.observer = mapActivity;
	}
	
	//the GestureDetecotor will only handle certain kinds of event, e.g. long press
	//other events will be handle by the original event handler
	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		if (mGestureDetector.onTouchEvent(ev)) {
			return true;
		} else {
			return super.onTouchEvent(ev);
		}
	}


	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onLongPress(MotionEvent e) {
		final GeoPoint p = this.getProjection().fromPixels(
	            (int) e.getX(),
	            (int) e.getY());
		
		//the first triger the long press event. The child view needs to be inflated
		if ( null == this.tp) {
			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        tp = (TransparentPanel)layoutInflater.inflate(R.layout.long_press_layout, null);
		}
		
		Log.i("Long press result", p.getLatitudeE6()+", "+p.getLongitudeE6());
		
		if (false == tpRemoved) {
			this.removeView(this.tp);
		}
		this.addView(this.tp, new MapView.LayoutParams(260,60,p,
				MapView.LayoutParams.BOTTOM_CENTER));
		tpRemoved = false;
		
		//Street View on this GeoPoint
		Button btnStreetView = (Button) tp.findViewById(R.id.streetview_button);
		btnStreetView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Uri geoUri = Uri.parse("google.streetview:cbll="+p.getLatitudeE6()/1000000f+","+p.getLongitudeE6()/1000000f+"&cbp=1,180,,0,1.0");
            	Intent mapCall = new Intent(Intent.ACTION_VIEW, geoUri);  
            	//notify UI update for the ob
            	GeotaggingMapView.this.observer.onStreetViewClick(mapCall);
            	
            }
        });
		
		//add entity button
		Button btnAdd = (Button) this.findViewById(R.id.add_inpanel_button);
		btnAdd.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	GeotaggingMapView.this.observer.onAddEntityClick(); 
            }
        });
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		if (!tpRemoved) {
			this.removeView(this.tp);
			tpRemoved = true;
		}
		return false;
	}
}
