package geotagging.utils;

import geotagging.app.R;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomArrayAdapter extends ArrayAdapter<String> {

	private Activity cx;
	private int[] drawables;
	
	public CustomArrayAdapter(Activity context, int resource, int textViewResourceId, int[] ds) {
		super(context, resource, textViewResourceId);
		// TODO Auto-generated constructor stub
		this.cx = context;
		this.drawables = ds;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	 // TODO Auto-generated method stub
	 //return super.getView(position, convertView, parent);
	 LayoutInflater inflater=cx.getLayoutInflater();
	 View row=inflater.inflate(R.layout.alert_dialog_list_type_item, parent, false);
	 TextView label=(TextView)row.findViewById(R.id.dialog_item_content);
	 label.setText(this.getItem(position));
	 ImageView icon=(ImageView)row.findViewById(R.id.dialog_item_icon);
	 icon.setImageResource(drawables[position]);

	 return row;
	}

}
