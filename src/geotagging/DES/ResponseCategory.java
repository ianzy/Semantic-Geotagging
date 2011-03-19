package geotagging.DES;

public class ResponseCategory {
	private String name;
	private int category_id;
	private int count;
	private boolean importanTag;
	
	public boolean isImportanTag() {
		return importanTag;
	}
	public void setImportanTag(boolean importanTag) {
		this.importanTag = importanTag;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCategory_id() {
		return category_id;
	}
	public void setCategory_id(int categoryId) {
		category_id = categoryId;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
