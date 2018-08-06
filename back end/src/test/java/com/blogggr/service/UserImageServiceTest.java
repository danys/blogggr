package com.blogggr.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.blogggr.dao.UserDao;
import com.blogggr.dao.UserImageDao;
import com.blogggr.entities.User;
import com.blogggr.entities.UserImage;
import com.blogggr.fakes.FakeFileStorageManager;
import com.blogggr.services.UserImageService;
import com.blogggr.utilities.FileStorageManager;
import com.blogggr.utilities.SimpleBundleMessageSource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Daniel Sunnen on 05.08.18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserImageServiceTest {

  @MockBean
  private UserImageDao userImageDao;

  @MockBean
  private UserDao userDao;

  @Autowired
  private UserImageService userImageService;

  @Configuration
  @ComponentScan(basePackages = {"com.blogggr.services", "com.blogggr.dao", "com.blogggr.utilities", "com.blogggr.fakes"})
  @EnableJpaRepositories(basePackages = {"com.blogggr.dao"})
  @EnableAutoConfiguration
  @EntityScan("com.blogggr.entities")
  public static class TestConfig {

    @Bean
    @Qualifier("userimage")
    public FileStorageManager fakeStorageManager() throws IOException{
      return new FakeFileStorageManager(Files.createTempDirectory("test").toFile().getAbsolutePath());
    }

    @Bean
    public SimpleBundleMessageSource messageSource() {
      return new SimpleBundleMessageSource();
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
      return new JavaMailSenderImpl();
    }

    @Bean
    public PasswordEncoder encoder() {
      return new BCryptPasswordEncoder(11);
    }
  }

  //Minimal 1 pixel PNG image
  /*private static final byte[] FILE_BYTES = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A,
      0x1A, 0x0A, 0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52, 0x00, 0x00, 0x00, 0x01, 0x00,
      0x00, 0x00, 0x01, 0x08, 0x04, 0x00, 0x00, 0x00, (byte) 0xB5, 0x1C, 0x0C, 0x02, 0x00, 0x00,
      0x00, 0x0B, 0x49, 0x44, 0x41, 0x54, 0x78, (byte) 0x9C, 0x63, 0x62, 0x38, 0x03, 0x00, 0x00,
      (byte) 0xD5, 0x00, (byte) 0xCF, (byte) 0x99, (byte) 0xFE, 0x4E, 0x55, 0x00, 0x00, 0x00, 0x00,
      0x49, 0x45, 0x4E, 0x44, (byte) 0xAE, 0x42, 0x60, (byte) 0x82};*/

  @Test
  public void postImage_Normal() throws URISyntaxException, IOException {
    byte[] fileBytes = Files.readAllBytes(Paths.get(this.getClass().getClassLoader().getResource("testImage.png").toURI()));
    MultipartFile file = new MockMultipartFile("file", fileBytes);
    User user = new User();
    user.setUserId(1L);
    user.setEmail("dan@dan.com");
    when(userDao.findById(1L)).thenReturn(user);
    when(userImageDao.findByName(any(String.class))).thenReturn(null);
    when(userImageDao.unsetCurrent(any(Long.class))).thenReturn(1);
    doNothing().when(userImageDao).save(any(UserImage.class));
    UserImage userImage = userImageService.postImage(1L, file);
  }
}
