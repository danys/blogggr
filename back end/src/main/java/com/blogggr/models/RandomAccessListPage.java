package com.blogggr.models;

import com.blogggr.json.PageMetaData;

import java.util.List;

/**
 * Created by Daniel Sunnen on 12.01.17.
 */
public class RandomAccessListPage<T> {

  List<T> pageItems;
  PageMetaData pageData;

  public RandomAccessListPage(List<T> pageItems, PageMetaData pageData) {
    this.pageItems = pageItems;
    this.pageData = pageData;
  }

  public List<T> getPageItems() {
    return pageItems;
  }

  public PageMetaData getPageData() {
    return pageData;
  }
}
