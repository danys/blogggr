package com.blogggr.controllers;

import com.blogggr.config.AppConfig;
import com.blogggr.dto.FriendData;
import com.blogggr.dto.out.UserDto;
import com.blogggr.entities.Friend;
import com.blogggr.entities.User;
import com.blogggr.exceptions.DbException;
import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.responses.ResponseBuilder;
import com.blogggr.security.UserPrincipal;
import com.blogggr.services.FriendService;
import com.blogggr.services.UserService;
import com.blogggr.utilities.DtoConverter;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Daniel Sunnen on 11.12.16.
 */
@RestController
@RequestMapping(AppConfig.baseUrl)
public class FriendsController {

  public static final String friendsPath = "/friends";

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private FriendService friendService;

  @Autowired
  DtoConverter dtoConverter;

  /**
   * POST /friends
   *
   * @param friendData the data to create a new friendship
   * @param userPrincipal the logged in user
   */
  @PostMapping(value = friendsPath)
  public ResponseEntity createFriendship(@Valid FriendData friendData,
      @AuthenticationPrincipal UserPrincipal userPrincipal)
      throws NotAuthorizedException, ResourceNotFoundException {
    logger.info("[POST /friends] User: {}, id1: {}, id2: {}", userPrincipal.getUser().getEmail(),
        friendData.getUserId1(), friendData.getUserId2());
    Friend friend = friendService.createFriend(userPrincipal.getUser().getUserId(), friendData);
    return ResponseBuilder
        .postSuccessResponse(AppConfig.fullBaseUrl + friendsPath + '/' + friend.getId());
  }

  /**
   * PUT /friends/id1/id2
   *
   * @param id the userId of a user
   * @param id2 the userId of another user
   * @param friendData the friendship update data
   * @param userPrincipal the logged in user
   */
  @PutMapping(path = friendsPath + "/{id:[\\d]+}/{id2:[\\d]+}")
  public ResponseEntity updateFriendship(@PathVariable String id, @PathVariable String id2,
      @Valid FriendData friendData, @AuthenticationPrincipal UserPrincipal userPrincipal)
      throws ResourceNotFoundException, DbException, NotAuthorizedException {
    logger.info(
        "[PUT /friends/{}/{}] User: {}", id, id2, userPrincipal.getUser().getEmail());
    friendService
        .updateFriend(userPrincipal.getUser().getUserId(), Long.parseLong(id), Long.parseLong(id2),
            friendData);
    return ResponseBuilder.putSuccessResponse();
  }

  /**
   * GET /friends
   *
   * @param userPrincipal the logged in user
   */
  @GetMapping(path = friendsPath)
  public ResponseEntity getFriends(@AuthenticationPrincipal UserPrincipal userPrincipal)
      throws ResourceNotFoundException, DbException {
    logger.info(
        "[GET /friends] User: {}", userPrincipal.getUser().getEmail());
    List<User> friends = friendService.getFriends(userPrincipal.getUser().getUserId());
    List<UserDto> userDtos = friends.stream().map(user -> dtoConverter.toUserDto(user))
        .collect(Collectors.toList());
    return ResponseBuilder.getSuccessResponse(userDtos);
  }

  /**
   * GET /friends/id
   * Retrieve a single friendship
   *
   * @param id the id of the friendship to retrieve
   * @param userPrincipal the logged in user
   * @return
   * @throws ResourceNotFoundException
   * @throws NotAuthorizedException
   */
  @GetMapping(value = friendsPath + "/{id:[\\d]+}")
  public ResponseEntity getFriendship(@PathVariable String id,
      @AuthenticationPrincipal UserPrincipal userPrincipal)
      throws ResourceNotFoundException, NotAuthorizedException {
    logger.info("[GET /friends/id] Id: {}, User: {}", id, userPrincipal.getUser().getEmail());
    Friend friend = friendService.getFriend(Long.parseLong(id), userPrincipal.getUser().getUserId());
    return ResponseBuilder.getSuccessResponse(dtoConverter.toFriendDto(friend));
  }

  /**
   * DELETE /friends/id
   * @param id the id of the friendship
   * @param userPrincipal the logged in user
   * @return
   * @throws ResourceNotFoundException
   * @throws NotAuthorizedException
   */
  @DeleteMapping(value = friendsPath + "/{id:[\\d]+}")
  public ResponseEntity deleteFriend(@PathVariable String id,
      @AuthenticationPrincipal UserPrincipal userPrincipal)
      throws ResourceNotFoundException, NotAuthorizedException {
    logger.info("[DELETE /friends/id] Id: {}, User: {}", id, userPrincipal.getUser().getEmail());
    friendService.deleteFriend(Long.parseLong(id), userPrincipal.getUser().getUserId());
    return ResponseBuilder.deleteSuccessResponse();
  }
}
