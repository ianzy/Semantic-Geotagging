package geotagging.DES;

import android.graphics.Bitmap;

public class Comment {
	private String userImg;
	private String userName;
	private String description;
	private String commentImg;
	private Bitmap actualUserImg;
	private Bitmap actualCommentImg;
	private String time;
	private int commentId;
	
	private int entity_id;
	private int category_id;
	
	public int getEntity_id() {
		return entity_id;
	}
	public void setEntity_id(int entityId) {
		entity_id = entityId;
	}
	public int getCategory_id() {
		return category_id;
	}
	public void setCategory_id(int categoryId) {
		category_id = categoryId;
	}
	public int getCommentId() {
		return commentId;
	}
	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}
	public String getUserImg() {
		return userImg;
	}
	public void setUserImg(String userImg) {
		this.userImg = userImg;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCommentImg() {
		return commentImg;
	}
	public void setCommentImg(String commentImg) {
		this.commentImg = commentImg;
	}
	public Bitmap getActualUserImg() {
		return actualUserImg;
	}
	public void setActualUserImg(Bitmap actualUserImg) {
		this.actualUserImg = actualUserImg;
	}
	public Bitmap getActualCommentImg() {
		return actualCommentImg;
	}
	public void setActualCommentImg(Bitmap actualImg) {
		this.actualCommentImg = actualImg;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}

}
