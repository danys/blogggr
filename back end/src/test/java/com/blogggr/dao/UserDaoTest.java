package com.blogggr.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Daniel Sunnen on 08.07.18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserDaoTest {

  @Autowired
  private UserDao userDao;

  @Test
  public void findByIdWithImages_Normal(){
    //TODO
  }
}
