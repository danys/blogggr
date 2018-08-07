package com.blogggr.responses;

import java.util.List;
import lombok.Getter;

/**
 * Created by Daniel Sunnen on 12.01.17.
 */
@Getter
public class RandomAccessListPage<T> {

  private List<T> pageItems;
  private PageMetaData pageData;

  public RandomAccessListPage(List<T> pageItems, PageMetaData pageData) {
    this.pageItems = pageItems;
    this.pageData = pageData;
  }
}
