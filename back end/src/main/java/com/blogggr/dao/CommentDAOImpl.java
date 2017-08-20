package com.blogggr.dao;

import com.blogggr.entities.Comment;
import org.springframework.stereotype.Repository;

/**
 * Created by Daniel Sunnen on 05.12.16.
 */
@Repository
public class CommentDAOImpl extends GenericDAOImpl<Comment> implements CommentDAO {

  public CommentDAOImpl() {
    super(Comment.class);
  }
}
