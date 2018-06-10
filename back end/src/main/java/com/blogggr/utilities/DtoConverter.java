package com.blogggr.utilities;

import com.blogggr.dto.out.CommentDto;
import com.blogggr.dto.out.FriendDto;
import com.blogggr.dto.out.PostDto;
import com.blogggr.dto.out.UserDto;
import com.blogggr.dto.out.UserWithImageDto;
import com.blogggr.entities.Comment;
import com.blogggr.entities.Friend;
import com.blogggr.entities.Post;
import com.blogggr.entities.User;
import org.modelmapper.ModelMapper;

/**
 * Created by Daniel Sunnen on 07.06.18.
 */
public class DtoConverter {

  private ModelMapper modelMapper;

  public DtoConverter(ModelMapper modelMapper){
    this.modelMapper = modelMapper;
  }

  public UserDto toUserDto(User user) {
    user.getImage();
    UserDto userDto = modelMapper.map(user, UserDto.class);
    return userDto;
  }

  public UserWithImageDto toUserWithImageDto(User user) {
    user.getImage();
    UserWithImageDto userWithImageDto = modelMapper.map(user, UserWithImageDto.class);
    return userWithImageDto;
  }

  public PostDto toPostDto(Post post) {
    PostDto postDto = modelMapper.map(post, PostDto.class);
    return postDto;
  }

  public CommentDto toCommentDto(Comment comment){
    CommentDto commentDto = modelMapper.map(comment, CommentDto.class);
    return commentDto;
  }

  public FriendDto toFriendDto(Friend friend){
    FriendDto friendDto = modelMapper.map(friend, FriendDto.class);
    friendDto.setUserId1(friend.getUser1());
    friendDto.setUserId2(friend.getUser2());
    return friendDto;
  }
}
