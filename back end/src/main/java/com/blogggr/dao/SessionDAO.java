package com.blogggr.dao;

import com.blogggr.entities.Session;

/**
 * Created by Daniel Sunnen on 13.11.16.
 */
public interface SessionDAO extends GenericDAO<Session>{

    Session getSessionBySessionHash(String sessionHash);
}
