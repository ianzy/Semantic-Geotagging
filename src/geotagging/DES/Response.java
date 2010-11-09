package geotagging.DES;

import android.graphics.Bitmap;


public class Response {
	private int entity_id;
	private String username;
	private String time;
	private String resp;
	private String image;
	private String location;
	private double lat;
	private double lng;
	private Bitmap actualImg;
	
	public Bitmap getActualImg() {
		return actualImg;
	}
	public void setActualImg(Bitmap actualImg) {
		this.actualImg = actualImg;
	}
	public int getEntity_id() {
		return entity_id;
	}
	public void setEntity_id(int entityId) {
		entity_id = entityId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getResp() {
		return resp;
	}
	public void setResp(String resp) {
		this.resp = resp;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	
	
}
