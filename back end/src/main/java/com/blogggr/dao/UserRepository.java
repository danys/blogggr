package com.blogggr.dao;

import com.blogggr.entities.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Daniel Sunnen on 01.06.18.
 */
public interface UserRepository extends CrudRepository<User, Long>{

  User findByEmail(String email);
}
