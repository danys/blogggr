package com.blogggr.services;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * Created by Daniel Sunnen on 21.06.18.
 */
@Component
public class EmailService {

  @Autowired
  public JavaMailSender emailSender;

  public void sendSimpleMessage(String to, String subject, String html) throws MessagingException {
    MimeMessage mimeMessage = emailSender.createMimeMessage();
    mimeMessage.setContent(html, "text/html");
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
    helper.setTo(to);
    helper.setSubject(subject);
    helper.setFrom("noreply@blogggr.com");
    emailSender.send(mimeMessage);
  }
}
