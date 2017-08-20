package com.blogggr.requestdata;

/**
 * Created by Daniel Sunnen on 19.11.16.
 */
public class PostData {

  private String title;
  private String textBody;
  private Boolean isGlobal;

  public PostData() {
    //
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTextBody() {
    return textBody;
  }

  public void setTextBody(String textBody) {
    this.textBody = textBody;
  }

  public Boolean getGlobal() {
    return isGlobal;
  }

  public void setGlobal(Boolean global) {
    isGlobal = global;
  }
}
