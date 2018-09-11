package com.blogggr.dto.out;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentWithImageDto {

  private Long commentId;
  private String text;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
  private Timestamp timestamp;
  private UserWithImageDto user;
}