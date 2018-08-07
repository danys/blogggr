package com.blogggr.controllers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.blogggr.config.AppConfig;
import com.blogggr.dto.CommentData;
import com.blogggr.entities.Comment;
import com.blogggr.responses.DataResponseBody;
import com.blogggr.services.CommentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Daniel Sunnen on 07.08.18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommentsControllerTest {

  @MockBean
  private CommentService commentService;

  private RestTemplate restTemplate;

  @LocalServerPort
  int port;

  private String baseUrl;

  @Before
  public void setUp(){
    this.restTemplate = new RestTemplate();
    this.baseUrl = "http://localhost:"+port+AppConfig.BASE_URL+"/comments";
  }

  @Test
  public void createComment_Normal(){
    Comment comment = new Comment();
    comment.setCommentId(100L);
    comment.setText("commentText");
    when(commentService.createComment(any(Long.class),any(CommentData.class))).thenReturn(comment);
    CommentData commentData = new CommentData();
    HttpEntity<CommentData> request = new HttpEntity<>(commentData);
    assertThat(restTemplate.exchange(baseUrl,HttpMethod.POST,request,DataResponseBody.class).getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }
}
