package com.blogggr.dao;

import com.blogggr.entities.Post;
import org.springframework.stereotype.Repository;

/**
 * Created by Daniel Sunnen on 20.11.16.
 */
@Repository
public class PostDAOImpl extends GenericDAOImpl<Post> implements PostDAO {

    public PostDAOImpl(){
        super(Post.class);
    }
}
