package com.blogggr.dao;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.blogggr.dao.PostDao.Visibility;
import com.blogggr.dto.PostSearchData;
import com.blogggr.entities.Friend;
import com.blogggr.entities.Post;
import com.blogggr.entities.User;
import com.blogggr.responses.PrevNextListPage;
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
public class PostDaoTest {

  @Autowired
  private PostDao postDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private FriendDao friendDao;

  private Post createPost(String title, String shortTitle, String text, boolean isGlobal, User user){
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
  public void getPosts_DefaultVisibilityAllByTitle(){
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
  public void getPosts_DefaultVisibilityAllByTitleAndPostUser(){
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
    assertThat(postsResponse.getPageItems().get(0).getUser().getEmail()).isEqualTo("dan@domain.com");
    assertThat(postsResponse.getPageData().getFilteredCount()).isEqualTo(1);
    assertThat(postsResponse.getPageData().getTotalCount()).isEqualTo(4);
    assertThat(postsResponse.getPageData().getPageItemsCount()).isEqualTo(1);
  }

  @Test
  @Transactional
  public void getPosts_DefaultVisibilityAllTestOnlyMe(){
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
  public void getPosts_DefaultVisibilityAllGlobalTest(){
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
  public void getPosts_FriendVisibility_CurrentUser_Excluded(){
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
  public void getPosts_FriendVisibility_Normal(){
    User user2 = new User();
    user2.setEmail("joe@domain.com");
    userDao.save(user2);
    User user = new User();
    user.setEmail("jo@domain.com");
    userDao.save(user);
    User user3 = new User();
    user3.setEmail("john@domain.com");
    userDao.save(user3);
    Friend friendShip1 = friendDao.createFriendship(user, user2);
    friendShip1.setStatus(1);
    Friend friendShip2 = friendDao.createFriendship(user, user3);
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
    assertThat(postsResponse.getPageItems().get(1).getUser().getEmail()).isEqualTo("joe@domain.com");
    assertThat(postsResponse.getPageItems().get(0).getTitle()).isEqualTo("t3");
    assertThat(postsResponse.getPageItems().get(0).getUser().getEmail()).isEqualTo("john@domain.com");
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
  public void getPosts_OnlyGlobal_Normal(){
    User user2 = new User();
    user2.setEmail("joe@domain.com");
    userDao.save(user2);
    User user = new User();
    user.setEmail("jo@domain.com");
    userDao.save(user);
    User user3 = new User();
    user3.setEmail("john@domain.com");
    userDao.save(user3);
    Friend friendShip1 = friendDao.createFriendship(user, user2);
    friendShip1.setStatus(1);
    Friend friendShip2 = friendDao.createFriendship(user, user3);
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
    assertThat(postsResponse.getPageItems().get(0).getUser().getEmail()).isEqualTo("john@domain.com");
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
  public void getPosts_OnlyCurrent_Normal(){
    User user2 = new User();
    user2.setEmail("joe@domain.com");
    userDao.save(user2);
    User user = new User();
    user.setEmail("jo@domain.com");
    userDao.save(user);
    User user3 = new User();
    user3.setEmail("john@domain.com");
    userDao.save(user3);
    Friend friendShip1 = friendDao.createFriendship(user, user2);
    friendShip1.setStatus(1);
    Friend friendShip2 = friendDao.createFriendship(user, user3);
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
}
