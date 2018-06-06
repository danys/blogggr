package com.blogggr.utilities;

import com.blogggr.dto.UserDto;
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
    UserDto userDto = modelMapper.map(user, UserDto.class);
    return userDto;
  }
}
