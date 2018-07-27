package com.blogggr.dao;

import static com.blogggr.dao.UserDaoTest.createUser;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.blogggr.entities.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Daniel Sunnen on 23.07.18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GenericDaoImplTest {

  @Autowired
  private UserDao dao;

  @Test
  @Transactional
  public void crud_Normal(){
    User user1 = createUser("Daniel", "Schmidt", "dan@schmidt.com", 1);
    dao.save(user1);
    User userDb1 = dao.findById(user1.getUserId());
    long id = user1.getUserId();
    assertThat(userDb1.getEmail()).isEqualTo("dan@schmidt.com");
    user1.setEmail("d@schmidt.com");
    dao.update(user1);
    dao.deleteById(id);
    assertThat(dao.findById(id)).isNull();
    user1 = createUser("Daniel", "Schmidt", "dan@schmidt.com", 1);
    dao.save(user1);
    id = user1.getUserId();
    dao.delete(user1);
    assertThat(dao.findById(id)).isNull();
  }
}
