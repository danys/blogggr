package com.blogggr.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.blogggr.config.AppConfig;
import com.blogggr.dto.CommentData;
import com.blogggr.entities.Comment;
import com.blogggr.services.CommentService;
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

  @Before
  public void setup() {
    mvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(springSecurity())
        .build();
  }

  @Test
  public void createComment_AccessDenied_Without_Authentication() throws Exception{
    Comment comment = new Comment();
    comment.setCommentId(100L);
    comment.setText("commentText");
    when(commentService.createComment(any(Long.class),any(CommentData.class))).thenReturn(comment);
    CommentData commentData = new CommentData();
    mvc.perform(post(BASE_URL + "/comments", commentData)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
    .andExpect(content().json("{'apiVersion': '1.0', 'errors': ['Access denied: Unauthenticated access not allowed!']}"));
  }
}
