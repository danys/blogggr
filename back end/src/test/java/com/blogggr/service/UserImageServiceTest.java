package com.blogggr.service;

import com.blogggr.dao.UserDao;
import com.blogggr.dao.UserImageDao;
import com.blogggr.services.UserImageService;
import com.blogggr.utilities.FileStorageManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Daniel Sunnen on 05.08.18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserImageServiceTest {

  @MockBean
  private UserImageDao userImageDao;

  @MockBean
  private UserDao userDao;

  @MockBean
  private FileStorageManager fileStorageManager;

  @Autowired
  private UserImageService userImageService;

  @Test
  public void postImage_Normal(){
    //
  }
}
