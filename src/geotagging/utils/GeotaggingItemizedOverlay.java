package geotagging.utils;

import geotagging.DES.Entity;
import geotagging.app.GeotaggingMap;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class GeotaggingItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private ArrayList<Entity> mEntities = new ArrayList<Entity>();

	private OverlayItem selectedItem;
	private Entity selectedEntity;
	
	private GeotaggingMap observer;
	
	
	public GeotaggingItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		
	}
	
	//an other weird use of observer pattern
	public void addObserver(GeotaggingMap mapActivity) {
		this.observer = mapActivity;
	}
	
	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}
	
	public void addEntity(Entity entity){
		mEntities.add(entity);
	}
	
	@Override
	protected boolean onTap(int index) {
		selectedItem = mOverlays.get(index);
		selectedEntity = mEntities.get(index);
		GeoPoint p = selectedItem.getPoint();
		
		observer.onIconTapped(p, selectedEntity);
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
