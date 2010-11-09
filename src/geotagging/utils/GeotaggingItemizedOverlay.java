package geotagging.utils;

import geotagging.DES.Entity;
import geotagging.app.R;
import geotagging.views.BaloonInMapView;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class GeotaggingItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private ArrayList<Entity> mEntities = new ArrayList<Entity>();
	private Context mContext;
	
	private Boolean isIconClick;
	private OverlayItem selectedItem;
	private Entity selectedEntity;
	private MapController mc;
	private BaloonInMapView baloon;
	private MapView mv;
	
	
	public GeotaggingItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		
	}
	
	public GeotaggingItemizedOverlay(Drawable defaultMarker, Context context, MapController mc, BaloonInMapView baloon, MapView mv) {
		  super(boundCenterBottom(defaultMarker));
		  this.mContext = context;
		  this.mc = mc;
		  this.mv = mv;
		  this.baloon = baloon;
		  this.isIconClick = false;
		}
	
	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}
	
	public void addEntity(Entity entity){
		mEntities.add(entity);
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow){
		super.draw(canvas, mapView, shadow);
//		if (isIconClick) {
//			drawInfoWindow(canvas, mapView, shadow);
//		}
		
	}
	
//	@Override
//	public boolean onTap(GeoPoint p, MapView mapView)  {
//		MapController mc = mapView.getController();
//		mc.animateTo(p);
//		//isIconClick = true;
//		return true;
//	}
	
	@Override
	protected boolean onTap(int index) {
		selectedItem = mOverlays.get(index);
		selectedEntity = mEntities.get(index);
		
		//Translate GeoPoint in to pixel and then animate to a lower position
		Point screenPts = new Point();
		GeoPoint p = selectedItem.getPoint();
		mv.getProjection().toPixels(p, screenPts);
		p = mv.getProjection().fromPixels(screenPts.x, screenPts.y-80);
		
		mc.animateTo(p);
		
		if (isIconClick) {
			mv.removeView(baloon);
		}
		isIconClick = true;
		
		mv.addView(baloon, new MapView.LayoutParams(260,200,selectedItem.getPoint(),
				MapView.LayoutParams.BOTTOM_CENTER));
		baloon.setVisibility(View.VISIBLE);
		
		TextView title = (TextView) baloon.findViewById(R.id.txv_title);
		title.setText(selectedEntity.getTitle());
		TextView description = (TextView) baloon.findViewById(R.id.txv_description);
		description.setText(selectedEntity.getDescription());
		
		Button btnDetail = (Button) baloon.findViewById(R.id.btn_detail);
		btnDetail.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
        		Intent intent = new Intent();
            	intent.setClassName("geotagging.app","geotagging.app.GeotaggingEntityInformation");
            	mContext.startActivity(intent);
            }
        });

		return true;
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

}
