package com.blogggr.dto;

import com.blogggr.dao.PostDao;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Sunnen on 10.06.18.
 */
@Getter
@Setter
public class PostSearchData extends PrevNextData<Long>{

  private Long posterUserId;

  @Size(min=3, max=100)
  private String title;

  @Enumerated(EnumType.STRING)
  private PostDao.Visibility visibility;
}
