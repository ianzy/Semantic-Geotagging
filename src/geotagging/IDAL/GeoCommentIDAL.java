package geotagging.IDAL;

import geotagging.DES.Comment;

import java.util.List;

public interface GeoCommentIDAL {
	List<Comment> getCommentsByEntityID(String entity_id);
	List<Comment> getCommentsByCommentId(int commentId);
}
