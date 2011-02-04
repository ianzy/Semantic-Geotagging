package geotagging.DES;

public class States {
	
	private String username;
	private String password;
	private int latest_entityid;
	private int latest_commentid;
	private int latest_responseid;
	private int cached_entityid;
	private int cached_responseid;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getLatest_entityid() {
		return latest_entityid;
	}
	public void setLatest_entityid(int latestEntityid) {
		latest_entityid = latestEntityid;
	}
	public int getLatest_commentid() {
		return latest_commentid;
	}
	public void setLatest_commentid(int latestCommentid) {
		latest_commentid = latestCommentid;
	}
	public int getLatest_responseid() {
		return latest_responseid;
	}
	public void setLatest_responseid(int latestResponseid) {
		latest_responseid = latestResponseid;
	}
	public int getCached_entityid() {
		return cached_entityid;
	}
	public void setCached_entityid(int cachedEntityid) {
		cached_entityid = cachedEntityid;
	}
	public int getCached_responseid() {
		return cached_responseid;
	}
	public void setCached_responseid(int cachedResponseid) {
		cached_responseid = cachedResponseid;
	}
	
}
