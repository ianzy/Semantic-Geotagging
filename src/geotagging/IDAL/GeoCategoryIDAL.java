package geotagging.IDAL;

import geotagging.DES.CommentCategory;
import geotagging.DES.ResponseCategory;

import java.util.List;

public interface GeoCategoryIDAL {
	List<CommentCategory> getCommentCategoriesByEntityId(int entity_id);
	
	List<ResponseCategory> getResponseCategoriesByCommentId(int comment_id);
	
	int getRemoteCommentCategories();
	int getRemoteResponseCategories();
	
	int createCommentCounters(int entity_id);
	int createResponseCounters(int comment_id);
}
