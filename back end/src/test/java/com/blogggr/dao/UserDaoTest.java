package com.blogggr.dao;

import com.blogggr.dto.UserEnums;
import com.blogggr.dto.UserEnums.Sex;
import com.blogggr.entities.User;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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

  public static User createUser(String firstName, String lastName, String email, int status){
    User user = new User();
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setEmail(email);
    user.setPasswordHash("hash");
    user.setChallenge("challenge");
    user.setStatus(status);
    user.setLastChange(Timestamp.valueOf(LocalDateTime.now()));
    user.setSex(Sex.M);
    user.setLang(UserEnums.Lang.DE);
    return user;
  }

  @Test
  public void findByIdWithImages_Normal(){
    //TODO
  }
}
