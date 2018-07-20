package com.blogggr.dao;

import static com.blogggr.dao.UserDaoTest.createUser;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.blogggr.entities.User;
import com.blogggr.entities.UserImage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Daniel Sunnen on 20.07.18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserImageDaoTest {

  @Autowired
  private UserImageDao userImageDao;

  @Autowired
  private UserDao userDao;

  private UserImage createUserImage(User user, String name, boolean isCurrent, int width, int height){
    UserImage userImage = new UserImage();
    userImage.setUser(user);
    userImage.setName(name);
    userImage.setIsCurrent(isCurrent);
    userImage.setWidth(width);
    userImage.setHeight(height);
    return userImage;
  }

  @Test
  @Transactional
  public void findByName_Normal(){
    User user = createUser("Dan", "Sun", "dan@sunnen.me", 1);
    userDao.save(user);
    UserImage userImage = createUserImage(user, "blabla", true, 100, 100);
    userImageDao.save(userImage);
    assertThat(userImageDao.findByName("blabla")).isNotNull();
    assertThat(userImageDao.findByName("blabla2")).isNull();
  }

  @Test
  @Transactional
  public void unsetCurrent_Normal(){
    User user = createUser("Dan", "Sun", "dan@sunnen.me", 1);
    userDao.save(user);
    UserImage userImage = createUserImage(user, "blabla", true, 100, 100);
    userImageDao.save(userImage);
    UserImage userImage2 = createUserImage(user, "blabla2", true, 100, 100);
    userImageDao.save(userImage2);
    UserImage userImage3 = createUserImage(user, "blabla3", false, 100, 100);
    userImageDao.save(userImage3);
    assertThat(userImage.getIsCurrent()).isEqualTo(true);
    assertThat(userImage2.getIsCurrent()).isEqualTo(true);
    assertThat(userImage3.getIsCurrent()).isEqualTo(false);
    assertThat(userImageDao.unsetCurrent(user.getUserId())).isEqualTo(2);
    assertThat(userImage.getIsCurrent()).isEqualTo(false);
    assertThat(userImage2.getIsCurrent()).isEqualTo(false);
    assertThat(userImage3.getIsCurrent()).isEqualTo(false);
  }
}
