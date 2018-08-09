package com.blogggr.controllers;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Created by Daniel Sunnen on 09.08.18.
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest
public class IndexControllerTest {

  @Autowired
  private WebApplicationContext context;

  private MockMvc mvc;

  private ObjectMapper mapper;

  @Before
  public void setup() {
    mvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(springSecurity())
        .build();
    mapper = new ObjectMapper();
  }

  @Test
  public void forward_Redirect() throws Exception{
    mvc.perform(get("/notFound"))
        .andExpect(status().isOk());
  }

  @Test
  public void forward_Normal() throws Exception{
    mvc.perform(get("/index.html"))
        .andExpect(status().isOk())
    .andExpect(content().string("<html><p>Hello blogggr!</p></html>"));
  }
}
