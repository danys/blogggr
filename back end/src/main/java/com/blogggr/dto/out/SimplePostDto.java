package com.blogggr.dto.out;

import com.blogggr.entities.PostImage;
import com.blogggr.utilities.DtoConverter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

/**
 * Created by Daniel Sunnen on 27.08.18.
 */
@Getter
@Setter
public class SimplePostDto {

  private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

  private Long postId;
  private String shortTitle;
  private String textBody;
  private String timestamp;
  private String title;
  private PostImageDto postImage;
  private UserDto user;

  public void setTimestamp(Timestamp ts) {
    if (ts != null) {
      this.timestamp = dateFormat.format(ts);
    }
  }

  public void setImage(List<PostImageDto> postImages){
    if ((postImages != null) && (postImages.size()>0)){
      postImage = postImages.get(0);
    }
  }
}
