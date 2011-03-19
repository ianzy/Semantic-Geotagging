package geotagging.utils;

import geotagging.DES.Entity;
import geotagging.app.GeotaggingMap;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class GeotaggingItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	private ArrayList<OverlayItem> mOverlays;
	private ArrayList<Entity> mEntities;

	private OverlayItem selectedItem;
	private Entity selectedEntity;
	
	private GeotaggingMap observer;
	
	
	public GeotaggingItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		
		mOverlays = new ArrayList<OverlayItem>();
		mEntities = new ArrayList<Entity>();
		
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
	
	public List<Entity> getEntities() {
		return mEntities;
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
