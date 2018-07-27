package com.blogggr.dao;

import static com.blogggr.dao.UserImageDaoTest.createUserImage;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.blogggr.dto.UserEnums;
import com.blogggr.dto.UserEnums.Sex;
import com.blogggr.dto.UserSearchData;
import com.blogggr.entities.User;
import com.blogggr.entities.UserImage;
import com.blogggr.responses.PrevNextListPage;
import com.blogggr.responses.RandomAccessListPage;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Daniel Sunnen on 08.07.18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserDaoTest {

  @Autowired
  private UserDao userDao;

  @Autowired
  private UserImageDao userImageDao;

  private User user1;
  private User user2;
  private User user3;
  private User user4;
  private User user5;

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
  @Transactional
  public void findByIdWithImages_Normal(){
    User user = createUser("Dan", "Sun", "dan@sunnen.me", 1);
    userDao.save(user);
    UserImage userImage = createUserImage(user, "blabla", true, 100, 100);
    userImageDao.save(userImage);
    UserImage userImage2 = createUserImage(user, "blabla2", false, 100, 100);
    userImageDao.save(userImage2);
    user.getUserImages().add(userImage);
    user.getUserImages().add(userImage2);
    User dbUser = userDao.findByIdWithImages(user.getUserId());
    assertThat(dbUser.getEmail()).isEqualTo("dan@sunnen.me");
    assertThat(dbUser.getImage().getName()).isEqualTo("blabla");
    assertThat(dbUser.getUserImages().size()).isEqualTo(2);
  }

  @Test
  @Transactional
  public void findByIdWithImages_UserNotFound(){
    User user = createUser("Dan", "Sun", "dan@sunnen.me", 1);
    userDao.save(user);
    assertThat(userDao.findByIdWithImages(2L)).isNull();
  }

  private void initUsers(){
    User user1 = createUser("Daniel", "Schmidt", "dan@schmidt.com", 1);
    userDao.save(user1);
    this.user1 = user1;
    User user2 = createUser("Claude", "Shannon", "claude@abc.com", 1);
    userDao.save(user2);
    this.user2 = user2;
    User user3 = createUser("Bill", "Gates", "bill@microsoft.com", 1);
    userDao.save(user3);
    this.user3 = user3;
    User user4 = createUser("Larry", "Page", "larry@page.com", 1);
    userDao.save(user4);
    this.user4 = user4;
    User user5 = createUser("Billy", "Mega", "billy@mega.com", 1);
    userDao.save(user5);
    this.user5 = user5;
  }

  @Test
  @Transactional
  public void getUsers_Normal(){
    initUsers();
    //Test case 1
    RandomAccessListPage<User> query1Results = userDao.getUsers("dan", 100, 1);
    assertThat(query1Results.getPageItems().size()).isEqualTo(1);
    assertThat(query1Results.getPageData().getNPages()).isEqualTo(1);
    assertThat(query1Results.getPageData().getPageId()).isEqualTo(1);
    assertThat(query1Results.getPageData().getPageItemsCount()).isEqualTo(1);
    assertThat(query1Results.getPageData().getTotalCount()).isEqualTo(1);
    //Test case 2
    RandomAccessListPage<User> query2Results = userDao.getUsers("Bill", 1, 1);
    assertThat(query2Results.getPageItems().size()).isEqualTo(1);
    assertThat(query2Results.getPageData().getNPages()).isEqualTo(2);
    assertThat(query2Results.getPageData().getPageId()).isEqualTo(1);
    assertThat(query2Results.getPageData().getPageItemsCount()).isEqualTo(1);
    assertThat(query2Results.getPageData().getTotalCount()).isEqualTo(2);
    //Test case 3
    RandomAccessListPage<User> query3Results = userDao.getUsers("Bill", 100, 1);
    assertThat(query3Results.getPageItems().size()).isEqualTo(2);
    assertThat(query3Results.getPageData().getNPages()).isEqualTo(1);
    assertThat(query3Results.getPageData().getPageId()).isEqualTo(1);
    assertThat(query3Results.getPageData().getPageItemsCount()).isEqualTo(2);
    assertThat(query3Results.getPageData().getTotalCount()).isEqualTo(2);
    //Test case negative page number

  }

  @Test
  @Transactional
  public void getUsers_PageNum_Invalid(){
    initUsers();
    //Test case 1
    RandomAccessListPage<User> queryResults = userDao.getUsers("Dan", 1, -1);
    assertThat(queryResults.getPageItems().size()).isEqualTo(1);
    assertThat(queryResults.getPageData().getNPages()).isEqualTo(1);
    assertThat(queryResults.getPageData().getPageId()).isEqualTo(1);
    assertThat(queryResults.getPageData().getPageItemsCount()).isEqualTo(1);
    assertThat(queryResults.getPageData().getTotalCount()).isEqualTo(1);
    //Test case 2
    queryResults = userDao.getUsers("Dan", 1, null);
    assertThat(queryResults.getPageItems().size()).isEqualTo(1);
    assertThat(queryResults.getPageData().getNPages()).isEqualTo(1);
    assertThat(queryResults.getPageData().getPageId()).isEqualTo(1);
    assertThat(queryResults.getPageData().getPageItemsCount()).isEqualTo(1);
    assertThat(queryResults.getPageData().getTotalCount()).isEqualTo(1);
  }

  @Test
  @Transactional
  public void getUsersBySearchTerms_Normal(){
    initUsers();
    //Test case 1: no result
    UserSearchData searchData = new UserSearchData();
    searchData.setEmail("dan");
    searchData.setFirstName("dan");
    searchData.setLastName("dan");
    searchData.setMaxRecordsCount(100);
    PrevNextListPage<User> users = userDao.getUsersBySearchTerms(searchData);
    assertThat(users.getPageItems().size()).isEqualTo(0);
    assertThat(users.getPageData().getFilteredCount()).isEqualTo(0);
    assertThat(users.getPageData().getPageItemsCount()).isEqualTo(0);
    assertThat(users.getPageData().getTotalCount()).isEqualTo(5);
    assertThat(users.getPageData().getNext()).isNull();
    assertThat(users.getPageData().getPrevious()).isNull();
    //Test case 2: two results only one returned
    searchData = new UserSearchData();
    searchData.setMaxRecordsCount(1);
    searchData.setFirstName("Bill");
    users = userDao.getUsersBySearchTerms(searchData);
    assertThat(users.getPageItems().size()).isEqualTo(1);
    assertThat(users.getPageData().getFilteredCount()).isEqualTo(2);
    assertThat(users.getPageData().getPageItemsCount()).isEqualTo(1);
    assertThat(users.getPageData().getTotalCount()).isEqualTo(5);
    List<User> dbUsers = userDao.findAll();
    assertThat(users.getPageData().getNext()).contains("/users?firstName=Bill&after="+user3.getUserId()+"&maxRecordsCount=1");
    assertThat(users.getPageData().getPrevious()).isNull();
    //Test case 3: two results only one returned
    searchData = new UserSearchData();
    searchData.setMaxRecordsCount(1);
    searchData.setFirstName("Bill");
    searchData.setAfter(user3.getUserId());
    users = userDao.getUsersBySearchTerms(searchData);
    assertThat(users.getPageItems().size()).isEqualTo(1);
    assertThat(users.getPageData().getFilteredCount()).isEqualTo(2);
    assertThat(users.getPageData().getPageItemsCount()).isEqualTo(1);
    assertThat(users.getPageData().getTotalCount()).isEqualTo(5);
    assertThat(users.getPageData().getNext()).isNull();
    assertThat(users.getPageData().getPrevious()).contains("/users?firstName=Bill&before="+user5.getUserId()+"&maxRecordsCount=1");
  }

  @Test
  @Transactional
  public void getUsersBySearchTerms_Two_Users_Same_Prefix(){
    initUsers();
    User user6 = createUser("Billy", "Gatesby", "billy@microsoft.com", 1);
    userDao.save(user6);
    UserSearchData searchData = new UserSearchData();
    searchData.setMaxRecordsCount(1);
    searchData.setFirstName("Bil");
    searchData.setLastName("Gate");
    searchData.setEmail("bill");
    PrevNextListPage<User> users = userDao.getUsersBySearchTerms(searchData);
    assertThat(users.getPageItems().size()).isEqualTo(1);
    assertThat(users.getPageData().getFilteredCount()).isEqualTo(2);
    assertThat(users.getPageData().getPageItemsCount()).isEqualTo(1);
    assertThat(users.getPageData().getTotalCount()).isEqualTo(6);
    assertThat(users.getPageData().getNext()).isNotNull();
    assertThat(users.getPageData().getPrevious()).isNull();
  }

  @Test
  @Transactional
  public void getUsersBySearchTerms_No_Filter(){
    initUsers();
    UserSearchData searchData = new UserSearchData();
    searchData.setMaxRecordsCount(1);
    PrevNextListPage<User> users = userDao.getUsersBySearchTerms(searchData);
    assertThat(users.getPageItems().size()).isEqualTo(1);
    assertThat(users.getPageData().getFilteredCount()).isEqualTo(5);
    assertThat(users.getPageData().getPageItemsCount()).isEqualTo(1);
    assertThat(users.getPageData().getTotalCount()).isEqualTo(5);
    assertThat(users.getPageData().getNext()).isNotNull();
    assertThat(users.getPageData().getPrevious()).isNull();
  }
}
