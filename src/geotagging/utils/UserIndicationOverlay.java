package geotagging.utils;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class UserIndicationOverlay extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> mOverlays;
//	private Context mContext;
	
	public UserIndicationOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		// TODO Auto-generated constructor stub
		mOverlays = new ArrayList<OverlayItem>();
//		this.mContext = context;
	}

	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}
	
	public void removeAllOverlays() {
		mOverlays.clear();
//		this.removeAllOverlays();
	}
	
//	@Override
//	protected boolean onTap(int index) {
//
//		OverlayItem item = mOverlays.get(index);
//		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
//		dialog.setTitle(item.getTitle());
//		dialog.setMessage(item.getSnippet());
//		dialog.show();
//		
//		return true;
//	}
	
	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

}
