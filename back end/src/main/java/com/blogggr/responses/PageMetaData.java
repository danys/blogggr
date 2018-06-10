package com.blogggr.json;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Sunnen on 12.01.17.
 */
@Getter
@Setter
public class PageMetaData {

  private Long totalCount; //total count of items
  private Integer pageItemsCount; //items per page
  private Integer pageId; //the page id
  private Integer nPages; //number of pages
  private String pageUrl; //this page url
}
