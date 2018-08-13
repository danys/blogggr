package com.blogggr.dao;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.blogggr.dao.PostDao.Visibility;
import com.blogggr.dto.PostSearchData;
import com.blogggr.entities.Friend;
import com.blogggr.entities.Post;
import com.blogggr.entities.User;
import com.blogggr.responses.PrevNextListPage;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Daniel Sunnen on 23.07.18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PostDaoTest {

  @Autowired
  private PostDao postDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private FriendDao friendDao;

  private Post createPost(String title, String shortTitle, String text, boolean isGlobal,
      User user) {
    Post post = new Post();
    post.setTitle(title);
    post.setShortTitle(shortTitle);
    post.setTextBody(text);
    post.setIsGlobal(isGlobal);
    post.setUser(user);
    return post;
  }

  @Test
  @Transactional
  public void getPosts_DefaultVisibilityAllByTitle() {
    User user = new User();
    userDao.save(user);
    Post post1 = createPost("title1", "shortTitle1", "text1", true, user);
    postDao.save(post1);
    Post post2 = createPost("title2", "shortTitle2", "text2", false, user);
    postDao.save(post2);
    Post post3 = createPost("t3", "shortTitle3", "text3", false, user);
    postDao.save(post3);
    Post post4 = createPost("atitle", "shortTitle4", "text4", false, user);
    postDao.save(post4);
    //Test case 1: search by title
    PostSearchData postSearchData = new PostSearchData();
    postSearchData.setMaxRecordsCount(100);
    postSearchData.setTitle("title");
    PrevNextListPage<Post> postsResponse = postDao.getPosts(postSearchData, user);
    assertThat(postsResponse.getPageItems().size()).isEqualTo(3);
    assertThat(postsResponse.getPageData().getFilteredCount()).isEqualTo(3);
    assertThat(postsResponse.getPageData().getTotalCount()).isEqualTo(4);
    assertThat(postsResponse.getPageData().getPageItemsCount()).isEqualTo(3);
    assertThat(postsResponse.getPageItems().get(0).getTitle()).isEqualTo("atitle");
    assertThat(postsResponse.getPageItems().get(1).getTitle()).isEqualTo("title2");
    assertThat(postsResponse.getPageItems().get(2).getTitle()).isEqualTo("title1");
  }

  @Test
  @Transactional
  public void getPosts_DefaultVisibilityAllByTitleAndPostUser() {
    User user = new User();
    user.setEmail("jo@domain.com");
    userDao.save(user);
    User user2 = new User();
    user2.setEmail("dan@domain.com");
    userDao.save(user2);
    Post post1 = createPost("title1", "shortTitle1", "text1", true, user);
    postDao.save(post1);
    Post post2 = createPost("title2", "shortTitle2", "text2", true, user2);
    postDao.save(post2);
    Post post3 = createPost("t3", "shortTitle3", "text3", true, user2);
    postDao.save(post3);
    Post post4 = createPost("atitle", "shortTitle4", "text4", true, user);
    postDao.save(post4);
    //Test case 1: search by title
    PostSearchData postSearchData = new PostSearchData();
    postSearchData.setMaxRecordsCount(100);
    postSearchData.setTitle("title");
    postSearchData.setPosterUserId(user2.getUserId());
    PrevNextListPage<Post> postsResponse = postDao.getPosts(postSearchData, user);
    assertThat(postsResponse.getPageItems().size()).isEqualTo(1);
    assertThat(postsResponse.getPageItems().get(0).getTitle()).isEqualTo("title2");
    assertThat(postsResponse.getPageItems().get(0).getUser().getEmail())
        .isEqualTo("dan@domain.com");
    assertThat(postsResponse.getPageData().getFilteredCount()).isEqualTo(1);
    assertThat(postsResponse.getPageData().getTotalCount()).isEqualTo(4);
    assertThat(postsResponse.getPageData().getPageItemsCount()).isEqualTo(1);
  }

  @Test
  @Transactional
  public void getPosts_DefaultVisibilityAllTestOnlyMe() {
    User user = new User();
    user.setEmail("jo@domain.com");
    userDao.save(user);
    Post post1 = createPost("title1", "shortTitle1", "text1", false, user);
    postDao.save(post1);
    Post post2 = createPost("title2", "shortTitle2", "text2", true, user);
    postDao.save(post2);
    Post post3 = createPost("t3", "shortTitle3", "text3", false, user);
    postDao.save(post3);
    Post post4 = createPost("atitle", "shortTitle4", "text4", true, user);
    postDao.save(post4);
    //Test case 1
    PostSearchData postSearchData = new PostSearchData();
    postSearchData.setMaxRecordsCount(100);
    PrevNextListPage<Post> postsResponse = postDao.getPosts(postSearchData, user);
    assertThat(postsResponse.getPageItems().size()).isEqualTo(4);
    assertThat(postsResponse.getPageData().getFilteredCount()).isEqualTo(4);
    assertThat(postsResponse.getPageData().getTotalCount()).isEqualTo(4);
    assertThat(postsResponse.getPageData().getPageItemsCount()).isEqualTo(4);
  }

  @Test
  @Transactional
  public void getPosts_DefaultVisibilityAllGlobalTest() {
    User user = new User();
    user.setEmail("jo@domain.com");
    userDao.save(user);
    User user2 = new User();
    user2.setEmail("john@domain.com");
    userDao.save(user2);
    Post post1 = createPost("title1", "shortTitle1", "text1", false, user2);
    postDao.save(post1);
    Post post2 = createPost("title2", "shortTitle2", "text2", true, user2);
    postDao.save(post2);
    Post post3 = createPost("t3", "shortTitle3", "text3", false, user2);
    postDao.save(post3);
    Post post4 = createPost("atitle", "shortTitle4", "text4", true, user2);
    postDao.save(post4);
    //Test case 1
    PostSearchData postSearchData = new PostSearchData();
    postSearchData.setMaxRecordsCount(100);
    PrevNextListPage<Post> postsResponse = postDao.getPosts(postSearchData, user);
    assertThat(postsResponse.getPageItems().size()).isEqualTo(2);
    assertThat(postsResponse.getPageData().getFilteredCount()).isEqualTo(2);
    assertThat(postsResponse.getPageData().getTotalCount()).isEqualTo(4);
    assertThat(postsResponse.getPageData().getPageItemsCount()).isEqualTo(2);
    assertThat(postsResponse.getPageItems().get(0).getTitle()).isEqualTo("atitle");
    assertThat(postsResponse.getPageItems().get(1).getTitle()).isEqualTo("title2");
  }

  @Test
  @Transactional
  public void getPosts_FriendVisibility_CurrentUser_Excluded() {
    User user = new User();
    user.setEmail("jo@domain.com");
    userDao.save(user);
    Post post1 = createPost("title1", "shortTitle1", "text1", false, user);
    postDao.save(post1);
    Post post2 = createPost("title2", "shortTitle2", "text2", true, user);
    postDao.save(post2);
    Post post3 = createPost("t3", "shortTitle3", "text3", false, user);
    postDao.save(post3);
    Post post4 = createPost("atitle", "shortTitle4", "text4", true, user);
    postDao.save(post4);
    //Test case 1
    PostSearchData postSearchData = new PostSearchData();
    postSearchData.setMaxRecordsCount(100);
    postSearchData.setVisibility(Visibility.ONLY_FRIENDS);
    PrevNextListPage<Post> postsResponse = postDao.getPosts(postSearchData, user);
    assertThat(postsResponse.getPageItems().size()).isEqualTo(0);
    assertThat(postsResponse.getPageData().getFilteredCount()).isEqualTo(0);
    assertThat(postsResponse.getPageData().getTotalCount()).isEqualTo(4);
    assertThat(postsResponse.getPageData().getPageItemsCount()).isEqualTo(0);
  }

  @Test
  @Transactional
  public void getPosts_FriendVisibility_Normal() {
    User user2 = new User();
    user2.setEmail("joe@domain.com");
    userDao.save(user2);
    User user = new User();
    user.setEmail("jo@domain.com");
    userDao.save(user);
    User user3 = new User();
    user3.setEmail("john@domain.com");
    userDao.save(user3);
    Friend friendShip1 = friendDao.createFriendship(user, user, user2);
    friendShip1.setStatus(1);
    Friend friendShip2 = friendDao.createFriendship(user, user, user3);
    friendShip2.setStatus(1);
    Post post1 = createPost("title1", "shortTitle1", "text1", false, user);
    postDao.save(post1);
    Post post2 = createPost("title2", "shortTitle2", "text2", false, user2);
    postDao.save(post2);
    Post post3 = createPost("t3", "shortTitle3", "text3", false, user3);
    postDao.save(post3);
    Post post4 = createPost("atitle", "shortTitle4", "text4", false, user);
    postDao.save(post4);
    //Test case 1
    PostSearchData postSearchData = new PostSearchData();
    postSearchData.setMaxRecordsCount(100);
    postSearchData.setVisibility(Visibility.ONLY_FRIENDS);
    PrevNextListPage<Post> postsResponse = postDao.getPosts(postSearchData, user);
    assertThat(postsResponse.getPageItems().size()).isEqualTo(2);
    assertThat(postsResponse.getPageData().getFilteredCount()).isEqualTo(2);
    assertThat(postsResponse.getPageData().getTotalCount()).isEqualTo(4);
    assertThat(postsResponse.getPageData().getPageItemsCount()).isEqualTo(2);
    assertThat(postsResponse.getPageItems().get(1).getTitle()).isEqualTo("title2");
    assertThat(postsResponse.getPageItems().get(1).getUser().getEmail())
        .isEqualTo("joe@domain.com");
    assertThat(postsResponse.getPageItems().get(0).getTitle()).isEqualTo("t3");
    assertThat(postsResponse.getPageItems().get(0).getUser().getEmail())
        .isEqualTo("john@domain.com");
    //Test case 2: search by title
    postSearchData.setTitle("title");
    postsResponse = postDao.getPosts(postSearchData, user);
    assertThat(postsResponse.getPageItems().size()).isEqualTo(1);
    assertThat(postsResponse.getPageData().getFilteredCount()).isEqualTo(1);
    assertThat(postsResponse.getPageData().getTotalCount()).isEqualTo(4);
    assertThat(postsResponse.getPageData().getPageItemsCount()).isEqualTo(1);
  }

  @Test
  @Transactional
  public void getPosts_FriendVisibility_WithPoster__Normal() {
    User user2 = new User();
    user2.setEmail("joe@domain.com");
    userDao.save(user2);
    User user = new User();
    user.setEmail("jo@domain.com");
    userDao.save(user);
    User user3 = new User();
    user3.setEmail("john@domain.com");
    userDao.save(user3);
    Friend friendShip1 = friendDao.createFriendship(user, user, user2);
    friendShip1.setStatus(1);
    Friend friendShip2 = friendDao.createFriendship(user, user, user3);
    friendShip2.setStatus(1);
    Post post1 = createPost("title1", "shortTitle1", "text1", false, user);
    postDao.save(post1);
    Post post2 = createPost("title2", "shortTitle2", "text2", false, user2);
    postDao.save(post2);
    Post post3 = createPost("t3", "shortTitle3", "text3", false, user3);
    postDao.save(post3);
    Post post4 = createPost("atitle", "shortTitle4", "text4", false, user);
    postDao.save(post4);
    //Test case 1
    PostSearchData postSearchData = new PostSearchData();
    postSearchData.setMaxRecordsCount(100);
    postSearchData.setVisibility(Visibility.ONLY_FRIENDS);
    postSearchData.setPosterUserId(user2.getUserId());
    PrevNextListPage<Post> postsResponse = postDao.getPosts(postSearchData, user);
    assertThat(postsResponse.getPageItems().size()).isEqualTo(1);
    assertThat(postsResponse.getPageData().getFilteredCount()).isEqualTo(1);
    assertThat(postsResponse.getPageData().getTotalCount()).isEqualTo(4);
    assertThat(postsResponse.getPageData().getPageItemsCount()).isEqualTo(1);
    assertThat(postsResponse.getPageItems().get(0).getTitle()).isEqualTo("title2");
    assertThat(postsResponse.getPageItems().get(0).getUser().getEmail())
        .isEqualTo("joe@domain.com");
  }

  @Test
  @Transactional
  public void getPosts_OnlyGlobal_Normal() {
    User user2 = new User();
    user2.setEmail("joe@domain.com");
    userDao.save(user2);
    User user = new User();
    user.setEmail("jo@domain.com");
    userDao.save(user);
    User user3 = new User();
    user3.setEmail("john@domain.com");
    userDao.save(user3);
    Friend friendShip1 = friendDao.createFriendship(user, user, user2);
    friendShip1.setStatus(1);
    Friend friendShip2 = friendDao.createFriendship(user, user, user3);
    friendShip2.setStatus(1);
    Post post1 = createPost("title1", "shortTitle1", "text1", true, user);
    postDao.save(post1);
    Post post2 = createPost("title2", "shortTitle2", "text2", false, user2);
    postDao.save(post2);
    Post post3 = createPost("t3", "shortTitle3", "text3", true, user3);
    postDao.save(post3);
    Post post4 = createPost("atitle", "shortTitle4", "text4", false, user);
    postDao.save(post4);
    //Test case 1: search without title
    PostSearchData postSearchData = new PostSearchData();
    postSearchData.setMaxRecordsCount(100);
    postSearchData.setVisibility(Visibility.ONLY_GLOBAL);
    PrevNextListPage<Post> postsResponse = postDao.getPosts(postSearchData, user);
    assertThat(postsResponse.getPageItems().size()).isEqualTo(2);
    assertThat(postsResponse.getPageData().getFilteredCount()).isEqualTo(2);
    assertThat(postsResponse.getPageData().getTotalCount()).isEqualTo(4);
    assertThat(postsResponse.getPageData().getPageItemsCount()).isEqualTo(2);
    assertThat(postsResponse.getPageItems().get(0).getTitle()).isEqualTo("t3");
    assertThat(postsResponse.getPageItems().get(0).getUser().getEmail())
        .isEqualTo("john@domain.com");
    assertThat(postsResponse.getPageItems().get(1).getTitle()).isEqualTo("title1");
    assertThat(postsResponse.getPageItems().get(1).getUser().getEmail()).isEqualTo("jo@domain.com");
    //Test case 2: search by title
    postSearchData.setTitle("title");
    postsResponse = postDao.getPosts(postSearchData, user);
    assertThat(postsResponse.getPageItems().size()).isEqualTo(1);
    assertThat(postsResponse.getPageData().getFilteredCount()).isEqualTo(1);
    assertThat(postsResponse.getPageData().getTotalCount()).isEqualTo(4);
    assertThat(postsResponse.getPageData().getPageItemsCount()).isEqualTo(1);
  }

  @Test
  @Transactional
  public void getPosts_OnlyGlobal_WithPoster_Normal() {
    User user2 = new User();
    user2.setEmail("joe@domain.com");
    userDao.save(user2);
    User user = new User();
    user.setEmail("jo@domain.com");
    userDao.save(user);
    User user3 = new User();
    user3.setEmail("john@domain.com");
    userDao.save(user3);
    Friend friendShip1 = friendDao.createFriendship(user, user, user2);
    friendShip1.setStatus(1);
    Friend friendShip2 = friendDao.createFriendship(user, user, user3);
    friendShip2.setStatus(1);
    Post post1 = createPost("title1", "shortTitle1", "text1", true, user);
    postDao.save(post1);
    Post post2 = createPost("title2", "shortTitle2", "text2", false, user2);
    postDao.save(post2);
    Post post3 = createPost("t3", "shortTitle3", "text3", true, user3);
    postDao.save(post3);
    Post post4 = createPost("atitle", "shortTitle4", "text4", false, user);
    postDao.save(post4);
    //Test case 1: search without title
    PostSearchData postSearchData = new PostSearchData();
    postSearchData.setMaxRecordsCount(100);
    postSearchData.setVisibility(Visibility.ONLY_GLOBAL);
    postSearchData.setPosterUserId(user3.getUserId());
    PrevNextListPage<Post> postsResponse = postDao.getPosts(postSearchData, user);
    assertThat(postsResponse.getPageItems().size()).isEqualTo(1);
    assertThat(postsResponse.getPageData().getFilteredCount()).isEqualTo(1);
    assertThat(postsResponse.getPageData().getTotalCount()).isEqualTo(4);
    assertThat(postsResponse.getPageData().getPageItemsCount()).isEqualTo(1);
    assertThat(postsResponse.getPageItems().get(0).getTitle()).isEqualTo("t3");
    assertThat(postsResponse.getPageItems().get(0).getUser().getEmail())
        .isEqualTo("john@domain.com");
  }

  @Test
  @Transactional
  public void getPosts_OnlyCurrent_Normal() {
    User user2 = new User();
    user2.setEmail("joe@domain.com");
    userDao.save(user2);
    User user = new User();
    user.setEmail("jo@domain.com");
    userDao.save(user);
    User user3 = new User();
    user3.setEmail("john@domain.com");
    userDao.save(user3);
    Friend friendShip1 = friendDao.createFriendship(user, user, user2);
    friendShip1.setStatus(1);
    Friend friendShip2 = friendDao.createFriendship(user, user, user3);
    friendShip2.setStatus(1);
    Post post1 = createPost("title1", "shortTitle1", "text1", true, user);
    postDao.save(post1);
    Post post2 = createPost("title2", "shortTitle2", "text2", false, user2);
    postDao.save(post2);
    Post post3 = createPost("t3", "shortTitle3", "text3", true, user3);
    postDao.save(post3);
    Post post4 = createPost("atitle", "shortTitle4", "text4", false, user);
    postDao.save(post4);
    //Test case 1: search without title
    PostSearchData postSearchData = new PostSearchData();
    postSearchData.setMaxRecordsCount(100);
    postSearchData.setVisibility(Visibility.ONLY_CURRENT_USER);
    PrevNextListPage<Post> postsResponse = postDao.getPosts(postSearchData, user);
    assertThat(postsResponse.getPageItems().size()).isEqualTo(2);
    assertThat(postsResponse.getPageData().getFilteredCount()).isEqualTo(2);
    assertThat(postsResponse.getPageData().getTotalCount()).isEqualTo(4);
    assertThat(postsResponse.getPageData().getPageItemsCount()).isEqualTo(2);
    assertThat(postsResponse.getPageItems().get(0).getTitle()).isEqualTo("atitle");
    assertThat(postsResponse.getPageItems().get(0).getUser().getEmail()).isEqualTo("jo@domain.com");
    assertThat(postsResponse.getPageItems().get(1).getTitle()).isEqualTo("title1");
    assertThat(postsResponse.getPageItems().get(1).getUser().getEmail()).isEqualTo("jo@domain.com");
    //Test case 2: search by title
    postSearchData.setTitle("atit");
    postsResponse = postDao.getPosts(postSearchData, user);
    assertThat(postsResponse.getPageItems().size()).isEqualTo(1);
    assertThat(postsResponse.getPageData().getFilteredCount()).isEqualTo(1);
    assertThat(postsResponse.getPageData().getTotalCount()).isEqualTo(4);
    assertThat(postsResponse.getPageData().getPageItemsCount()).isEqualTo(1);
  }

  @Test
  @Transactional
  public void getPosts_After_Posts() {
    User user = new User();
    user.setEmail("joe@domain.com");
    userDao.save(user);
    Post post1 = createPost("title1", "shortTitle1", "text1", true, user);
    postDao.save(post1);
    Post post2 = createPost("title2", "shortTitle2", "text2", false, user);
    postDao.save(post2);
    Post post3 = createPost("t3", "shortTitle3", "text3", true, user);
    postDao.save(post3);
    Post post4 = createPost("atitle", "shortTitle4", "text4", false, user);
    postDao.save(post4);
    //Test case 1: search without title
    PostSearchData postSearchData = new PostSearchData();
    postSearchData.setMaxRecordsCount(1);
    postSearchData.setVisibility(Visibility.ONLY_CURRENT_USER);
    postSearchData.setAfter(post1.getPostId());
    PrevNextListPage<Post> postsResponse = postDao.getPosts(postSearchData, user);
    assertThat(postsResponse.getPageItems().size()).isEqualTo(1);
    assertThat(postsResponse.getPageItems().get(0).getPostId()).isEqualTo(post2.getPostId());
    assertThat(postsResponse.getPageData().getPageItemsCount()).isEqualTo(1);
    assertThat(postsResponse.getPageData().getTotalCount()).isEqualTo(4);
    assertThat(postsResponse.getPageData().getFilteredCount()).isEqualTo(4);
    assertThat(postsResponse.getPageData().getNext()).isNotNull();
    assertThat(postsResponse.getPageData().getPrevious()).isNotNull();
    assertThat(postsResponse.getPageData().getNext()).contains("before=" + post2.getPostId());
    assertThat(postsResponse.getPageData().getPrevious()).contains("after=" + post2.getPostId());
    //Test case 2
    postSearchData.setAfter(null);
    postsResponse = postDao.getPosts(postSearchData, user);
    assertThat(postsResponse.getPageItems().size()).isEqualTo(1);
    assertThat(postsResponse.getPageItems().get(0).getPostId()).isEqualTo(post4.getPostId());
    assertThat(postsResponse.getPageData().getPageItemsCount()).isEqualTo(1);
    assertThat(postsResponse.getPageData().getTotalCount()).isEqualTo(4);
    assertThat(postsResponse.getPageData().getFilteredCount()).isEqualTo(4);
    assertThat(postsResponse.getPageData().getNext()).isNotNull();
    assertThat(postsResponse.getPageData().getPrevious()).isNull();
    assertThat(postsResponse.getPageData().getNext()).contains("before=" + post4.getPostId());
  }

  @Test
  @Transactional
  public void getPosts_After_Before_Posts_Exception() {
    User user = new User();
    user.setEmail("joe@domain.com");
    userDao.save(user);
    //Test case 1: search without title
    PostSearchData postSearchData = new PostSearchData();
    postSearchData.setMaxRecordsCount(1);
    postSearchData.setVisibility(Visibility.ONLY_CURRENT_USER);
    postSearchData.setAfter(1L);
    postSearchData.setBefore(2L);
    try {
      postDao.getPosts(postSearchData, user);
    } catch (InvalidDataAccessApiUsageException e) {
      assertThat(e.getCause().getMessage()).contains("Cannot set both before and after");
    }
  }

  @Test
  @Transactional
  public void getPosts_After_Posts_User_And_Title() {
    User user2 = new User();
    user2.setEmail("joe@domain.com");
    userDao.save(user2);
    User user = new User();
    user.setEmail("jo@domain.com");
    userDao.save(user);
    User user3 = new User();
    user3.setEmail("john@domain.com");
    userDao.save(user3);
    Friend friendShip1 = friendDao.createFriendship(user, user, user2);
    friendShip1.setStatus(1);
    Friend friendShip2 = friendDao.createFriendship(user, user, user3);
    friendShip2.setStatus(1);
    Post post1 = createPost("title1", "shortTitle1", "text1", true, user2);
    postDao.save(post1);
    Post post2 = createPost("title2", "shortTitle2", "text2", false, user2);
    postDao.save(post2);
    Post post3 = createPost("t3", "shortTitle3", "text3", true, user3);
    postDao.save(post3);
    Post post4 = createPost("atitle", "shortTitle4", "text4", false, user3);
    postDao.save(post4);
    //Test case 1: search without title
    PostSearchData postSearchData = new PostSearchData();
    postSearchData.setMaxRecordsCount(1);
    postSearchData.setVisibility(Visibility.ONLY_FRIENDS);
    postSearchData.setAfter(post1.getPostId());
    postSearchData.setPosterUserId(user2.getUserId());
    postSearchData.setTitle("title");
    PrevNextListPage<Post> postsResponse = postDao.getPosts(postSearchData, user);
    assertThat(postsResponse.getPageItems().size()).isEqualTo(1);
    assertThat(postsResponse.getPageItems().get(0).getPostId()).isEqualTo(post2.getPostId());
    assertThat(postsResponse.getPageData().getPageItemsCount()).isEqualTo(1);
    assertThat(postsResponse.getPageData().getTotalCount()).isEqualTo(4);
    assertThat(postsResponse.getPageData().getFilteredCount()).isEqualTo(1);
  }

  @Test
  @Transactional
  public void buildNextPageUrl_Exception()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method method = PostDao.class
        .getDeclaredMethod("buildNextPageUrl", Long.class, Integer.class, Long.class, String.class,
            Visibility.class);
    method.setAccessible(true);
    try {
      method.invoke(postDao, null, null, 1L, "", Visibility.ALL);
      fail();
    } catch (Exception e) {
      //OK
    }
  }

  @Test
  @Transactional
  public void buildPreviousPageUrl_Exception()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method method = PostDao.class
        .getDeclaredMethod("buildPreviousPageUrl", Long.class, Integer.class, Long.class,
            String.class, Visibility.class);
    method.setAccessible(true);
    try {
      method.invoke(postDao, null, null, 1L, "", Visibility.ALL);
      fail();
    } catch (Exception e) {
      //OK
    }
  }

  @Test
  @Transactional
  public void buildListKV_Normal()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method method = PostDao.class
        .getDeclaredMethod("buildListKV", Long.class, Long.class, Integer.class, Long.class,
            String.class, Visibility.class);
    method.setAccessible(true);
    List<Object> list = (List<Object>) method.invoke(postDao, 1L, 1L, 1, 1L, "", Visibility.ALL);
    assertThat(list.size()).isEqualTo(6);
  }

  @Test
  @Transactional
  public void getPostByUserAndLabel_Normal() {
    User user = new User();
    user.setEmail("jo@domain.com");
    userDao.save(user);
    Post post = createPost("title1", "shortTitle1", "text1", true, user);
    postDao.save(post);
    Post dbPost = postDao.getPostByUserAndLabel(user.getUserId(), user.getUserId(), "shortTitle1");
    assertThat(dbPost).isNotNull();
    assertThat(dbPost.getUser().getEmail()).isEqualTo("jo@domain.com");
    assertThat(dbPost.getTitle()).isEqualTo("title1");
  }

  @Test
  @Transactional
  public void getPostByUserAndLabel_No_Result() {
    User user = new User();
    user.setEmail("jo@domain.com");
    userDao.save(user);
    Post post = createPost("title1", "shortTitle1", "text1", true, user);
    postDao.save(post);
    //Wrong title
    Post dbPost = postDao.getPostByUserAndLabel(user.getUserId(), user.getUserId(), "shortTitle2");
    assertThat(dbPost).isNull();
    //Wrong user
    dbPost = postDao.getPostByUserAndLabel(user.getUserId(), user.getUserId() + 1L, "shortTitle1");
    assertThat(dbPost).isNull();
    //Wrong user and wrong title
    dbPost = postDao.getPostByUserAndLabel(user.getUserId(), user.getUserId() + 1L, "shortTitle2");
    assertThat(dbPost).isNull();
  }
}
