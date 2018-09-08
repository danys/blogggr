package com.blogggr.utilities;

import com.blogggr.dto.out.CommentDto;
import com.blogggr.dto.out.FriendDto;
import com.blogggr.dto.out.OnlyPostDto;
import com.blogggr.dto.out.PostDto;
import com.blogggr.dto.out.PostImageDto;
import com.blogggr.dto.out.SimplePostDto;
import com.blogggr.dto.out.UserDto;
import com.blogggr.dto.out.UserImageDto;
import com.blogggr.dto.out.UserWithImageDto;
import com.blogggr.entities.Comment;
import com.blogggr.entities.Friend;
import com.blogggr.entities.Post;
import com.blogggr.entities.PostImage;
import com.blogggr.entities.User;
import com.blogggr.entities.UserImage;
import java.util.stream.Collectors;
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
    return modelMapper.map(user, UserDto.class);
  }

  public UserWithImageDto toUserWithImageDto(User user) {
    user.getImage();
    return modelMapper.map(user, UserWithImageDto.class);
  }

  public PostDto toPostDto(Post post) {
    PostDto postDto = modelMapper.map(post, PostDto.class);
    postDto.setTimestamp(post.getTimestamp());
    return postDto;
  }

  public SimplePostDto toSimplePostDto(Post post) {
    SimplePostDto postDto = modelMapper.map(post, SimplePostDto.class);
    postDto.setTimestamp(post.getTimestamp());
    postDto.setImage(post.getPostImages().stream().map(postImage -> modelMapper.map(postImage, PostImageDto.class)).collect(
        Collectors.toList()));
    return postDto;
  }

  public OnlyPostDto toOnlyPostDto(Post post) {
    OnlyPostDto postDto = modelMapper.map(post, OnlyPostDto.class);
    postDto.setTimestamp(post.getTimestamp());
    return postDto;
  }

  public CommentDto toCommentDto(Comment comment){
    return modelMapper.map(comment, CommentDto.class);
  }

  public FriendDto toFriendDto(Friend friend){
    FriendDto friendDto = modelMapper.map(friend, FriendDto.class);
    friendDto.setUserId1(friend.getUser1());
    friendDto.setUserId2(friend.getUser2());
    friendDto.setLastActionUserId(friend.getLastActionUserId());
    return friendDto;
  }

  public UserImageDto toUserImageDto(UserImage userImage){
    return modelMapper.map(userImage, UserImageDto.class);
  }

  public PostImageDto toPostImageDto(PostImage postImage){
    return modelMapper.map(postImage, PostImageDto.class);
  }
}
