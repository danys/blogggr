package com.blogggr.controllers;

import static com.blogggr.controllers.CommentsControllerTest.createUserPrincipal;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blogggr.config.AppConfig;
import com.blogggr.entities.UserImage;
import com.blogggr.services.UserImageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Daniel Sunnen on 12.08.18.
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest
public class UserImageControllerTest {

  @MockBean
  private UserImageService userImageService;

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

  @Test
  public void postUserImage_Not_Authorized() throws Exception{
    UserImage userImage = new UserImage();
    userImage.setUserImageId(1L);
    userImage.setName("name");
    when(userImageService.postImage(any(Long.class),any(MultipartFile.class))).thenReturn(userImage);
    byte[] fileContent = new byte[]{0};
    MockMultipartFile file = new MockMultipartFile("file", fileContent);
    mvc.perform(multipart(BASE_URL + "/userimages")
        .file(file))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void postUserImage_Normal() throws Exception{
    UserImage userImage = new UserImage();
    userImage.setUserImageId(1L);
    userImage.setName("name");
    when(userImageService.postImage(any(Long.class),any(MultipartFile.class))).thenReturn(userImage);
    byte[] fileContent = new byte[]{0};
    MockMultipartFile file = new MockMultipartFile("file", fileContent);
    mvc.perform(multipart(BASE_URL + "/userimages")
        .file(file)
        .with(user(createUserPrincipal("dan@dan.com", 1L))))
        .andExpect(status().isCreated())
        .andExpect(content().json("{'apiVersion': '1.0', 'data': null}"))
        .andExpect(header().string(AppConfig.LOCATION_HEADER_KEY,
            AppConfig.FULL_BASE_URL + "/userimages/" + userImage.getName()));
  }

  @Test
  public void getUserImage_Normal() throws Exception{
    byte[] fileContent = new byte[]{0};
    Resource file = new ByteArrayResource(fileContent);
    when(userImageService.getUserImage(any(String.class))).thenReturn(file);
    mvc.perform(get(BASE_URL + "/userimages/file1")
        .with(user(createUserPrincipal("dan@dan.com", 1L))))
        .andExpect(status().isOk());
  }
}
