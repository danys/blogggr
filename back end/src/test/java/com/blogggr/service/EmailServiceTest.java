package com.blogggr.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.blogggr.services.EmailService;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Daniel Sunnen on 04.08.18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EmailServiceTest {

  @Autowired
  private EmailService emailService;

  @MockBean
  public JavaMailSender emailSender;

  @Test
  public void sendSimpleMailMessage_Normal() throws MessagingException {
    when(emailSender.createMimeMessage()).thenReturn(new MimeMessage(Session.getInstance(new Properties())));
    doNothing().when(emailSender).send(any(MimeMessage.class));
    emailService.sendSimpleMessage("dan@domain.com","Blabla","Hello");
  }

  @Test
  public void sendSimpleMailMessage_Exception() {
    when(emailSender.createMimeMessage()).thenReturn(new MimeMessage(Session.getInstance(new Properties())));
    doNothing().when(emailSender).send(any(MimeMessage.class));
    try {
      emailService.sendSimpleMessage("", "Blabla", "Hello");
    }catch(MessagingException e){
      assertThat(e.getMessage()).contains("Illegal address");
    }
  }
}
