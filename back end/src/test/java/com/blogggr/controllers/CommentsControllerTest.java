package com.blogggr.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.blogggr.config.AppConfig;
import com.blogggr.dto.CommentData;
import com.blogggr.dto.UserEnums.Lang;
import com.blogggr.entities.Comment;
import com.blogggr.entities.User;
import com.blogggr.security.UserPrincipal;
import com.blogggr.services.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

/**
 * Created by Daniel Sunnen on 07.08.18.
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest
public class CommentsControllerTest {

  @MockBean
  private CommentService commentService;

  @Autowired
  private WebApplicationContext context;

  private MockMvc mvc;

  private static final String BASE_URL = AppConfig.BASE_URL;

  private ObjectMapper mapper;

  @Before
  public void setup() {
    mvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(springSecurity())
        .build();
    mapper = new ObjectMapper();
  }

  public static UserPrincipal createUserPrincipal(String email, Long userId){
    User user = new User();
    user.setUserId(userId);
    user.setEmail(email);
    user.setLang(Lang.EN);
    return new UserPrincipal(user);
  }

  @Test
  public void createComment_AccessDenied_Without_Authentication() throws Exception{
    CommentData commentData = new CommentData();
    mvc.perform(post(BASE_URL + "/comments", commentData)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
    .andExpect(content().json("{'apiVersion': '1.0', 'errors': ['Access denied: Unauthenticated access not allowed!']}"));
  }

  @Test
  public void createComment_Normal() throws Exception{
    Comment comment = new Comment();
    comment.setCommentId(100L);
    comment.setText("commentText");
    when(commentService.createComment(any(Long.class),any(CommentData.class))).thenReturn(comment);
    CommentData commentData = new CommentData();
    commentData.setPostId(1L);
    commentData.setText("text");
    mvc.perform(post(BASE_URL + "/comments")
        .with(user(createUserPrincipal("dan@dan.com",1L)))
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(commentData)))
        .andExpect(status().isCreated())
        .andExpect(content().json("{'apiVersion': '1.0', 'data': null}"))
        .andExpect(header().string(AppConfig.LOCATION_HEADER_KEY,AppConfig.FULL_BASE_URL + "/comments/" + comment.getCommentId()));
  }

  @Test
  public void updateComment_AccessDenied_Without_Authentication() throws Exception{
    CommentData commentData = new CommentData();
    mvc.perform(put(BASE_URL + "/comments/1", commentData)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().json("{'apiVersion': '1.0', 'errors': ['Access denied: Unauthenticated access not allowed!']}"));
  }

  @Test
  public void updateComment_Normal() throws Exception{
    Comment comment = new Comment();
    comment.setCommentId(100L);
    comment.setText("commentText");
    doNothing().when(commentService).updateComment(any(Long.class),any(Long.class),any(CommentData.class));
    CommentData commentData = new CommentData();
    commentData.setPostId(1L);
    commentData.setText("text");
    mvc.perform(put(BASE_URL + "/comments/1")
        .with(user(createUserPrincipal("dan@dan.com",1L)))
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(commentData)))
        .andExpect(status().isOk())
        .andExpect(content().json("{'apiVersion': '1.0', 'data': null}"));
  }

  @Test
  public void deleteComment_AccessDenied_Without_Authentication() throws Exception{
    CommentData commentData = new CommentData();
    mvc.perform(delete(BASE_URL + "/comments/1", commentData)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().json("{'apiVersion': '1.0', 'errors': ['Access denied: Unauthenticated access not allowed!']}"));
  }

  @Test
  public void deleteComment_Normal() throws Exception{
    Comment comment = new Comment();
    comment.setCommentId(100L);
    comment.setText("commentText");
    doNothing().when(commentService).deleteComment(any(Long.class),any(Long.class));
    mvc.perform(delete(BASE_URL + "/comments/1")
        .with(user(createUserPrincipal("dan@dan.com",1L))))
        .andExpect(status().isOk())
        .andExpect(content().json("{'apiVersion': '1.0', 'data': null}"));
  }

  @Test
  public void getComment_AccessDenied_Without_Authentication() throws Exception{
    mvc.perform(get(BASE_URL + "/comments/1")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().json("{'apiVersion': '1.0', 'errors': ['Access denied: Unauthenticated access not allowed!']}"));
  }

  @Test
  public void getComment_Normal() throws Exception{
    Comment comment = new Comment();
    comment.setCommentId(100L);
    comment.setText("text1");
    when(commentService.getCommentById(any(Long.class),any(Long.class))).thenReturn(comment);
    mvc.perform(get(BASE_URL + "/comments/1")
        .with(user(createUserPrincipal("dan@dan.com",1L))))
        .andExpect(status().isOk())
        .andExpect(content().json("{'apiVersion': '1.0', 'data': {'commentId': 100, 'text': 'text1'}}"));
  }

  @Test
  public void getCommentsByPostId_AccessDenied_Without_Authentication() throws Exception{
    mvc.perform(get(BASE_URL + "/posts/1/comments")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().json("{'apiVersion': '1.0', 'errors': ['Access denied: Unauthenticated access not allowed!']}"));
  }

  @Test
  public void getCommentsByPostId_Normal() throws Exception{
    List<Comment> comments = new ArrayList<>();
    Comment comment = new Comment();
    comment.setText("commentText");
    comment.setCommentId(1L);
    comments.add(comment);
    when(commentService
        .getCommentsByPostId(any(Long.class),any(Long.class))).thenReturn(comments);
    mvc.perform(get(BASE_URL + "/posts/1/comments")
        .contentType(MediaType.APPLICATION_JSON)
        .with(user(createUserPrincipal("dan@dan.com",1L))))
        .andExpect(status().isOk())
        .andExpect(content().json("{'apiVersion': '1.0', 'data': [{'commentId': 1, 'text': 'commentText'}]}"));
  }
}
