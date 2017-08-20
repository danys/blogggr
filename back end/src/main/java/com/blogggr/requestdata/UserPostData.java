package com.blogggr.requestdata;

/**
 * Created by Daniel Sunnen on 04.11.16.
 */
public class UserPostData {

  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private String passwordRepeat;
  private String sex;
  private String lang;

  public UserPostData() {
    //
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPasswordRepeat() {
    return passwordRepeat;
  }

  public void setPasswordRepeat(String passwordRepeat) {
    this.passwordRepeat = passwordRepeat;
  }

  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }
}
