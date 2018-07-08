package com.blogggr.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Sunnen on 06.01.17.
 */
@Getter
@Setter
public class PageData {

  private Long totalCount; //total count of items
  private Long filteredCount; //filtered count of items
  private Integer pageItemsCount; //items per page

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String next;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String previous;
}
