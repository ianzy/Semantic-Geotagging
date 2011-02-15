package geotagging.IDAL;

import geotagging.DES.Comment;

import java.util.List;

public interface GeoCommentIDAL {
	List<Comment> getCachedCommentsByEntityIDAndCategoryID(int entity_id, int category_id);
	List<Comment> getRemoteCommentsByEntityIDAndCategoryID(int since_id, int entity_id, int category_id, int count);
	
	List<Comment> getCachedFollowUpCommentsByCommentId(int commentId);
	List<Comment> getRemoteFollowUpCommentsByCommentId(int since_id, int commentId, int count);
}
