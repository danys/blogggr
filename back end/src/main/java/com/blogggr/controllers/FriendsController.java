package com.blogggr.controllers;

import com.blogggr.config.AppConfig;
import com.blogggr.dto.FriendDataBase;
import com.blogggr.dto.FriendDataUpdate;
import com.blogggr.dto.out.UserWithImageDto;
import com.blogggr.entities.Friend;
import com.blogggr.entities.User;
import com.blogggr.responses.ResponseBuilder;
import com.blogggr.security.UserPrincipal;
import com.blogggr.services.FriendService;
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
@RequestMapping(AppConfig.BASE_URL)
public class FriendsController {

  public static final String FRIENDS_PATH = "/friends";

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private FriendService friendService;

  @Autowired
  private DtoConverter dtoConverter;

  /**
   * POST /friends
   *
   * @param friendData the data to create a new friendship
   * @param userPrincipal the logged in user
   */
  @PostMapping(value = FRIENDS_PATH)
  public ResponseEntity createFriendship(@Valid @RequestBody FriendDataBase friendData,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    logger.info("[POST /friends] User: {}, id1: {}, id2: {}", userPrincipal.getUser().getEmail(),
        friendData.getUserId1(), friendData.getUserId2());
    Friend friend = friendService.createFriend(userPrincipal.getUser().getUserId(), friendData);
    return ResponseBuilder
        .postSuccessResponse(AppConfig.FULL_BASE_URL + FRIENDS_PATH + '/' + friend.getId().getUserOneId() + '/' + friend.getId().getUserTwoId());
  }

  /**
   * PUT /friends/id1/id2
   *
   * @param id the userId of a user
   * @param id2 the userId of another user
   * @param friendData the friendship update data
   * @param userPrincipal the logged in user
   */
  @PutMapping(path = FRIENDS_PATH + "/{id:[\\d]+}/{id2:[\\d]+}")
  public ResponseEntity updateFriendship(@PathVariable String id, @PathVariable String id2,
      @Valid @RequestBody FriendDataUpdate friendData,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
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
  @GetMapping(path = FRIENDS_PATH)
  public ResponseEntity getFriends(@AuthenticationPrincipal UserPrincipal userPrincipal) {
    logger.info(
        "[GET /friends] User: {}", userPrincipal.getUser().getEmail());
    List<User> friends = friendService.getFriends(userPrincipal.getUser().getUserId());
    List<UserWithImageDto> userDtos = friends.stream().map(user -> dtoConverter.toUserWithImageDto(user))
        .collect(Collectors.toList());
    return ResponseBuilder.getSuccessResponse(userDtos);
  }

  /**
   * GET /friends/id1/id2 Retrieve a single friendship
   * @param id1 id1 of the friendship
   * @param id2 id2 of the friendship
   * @param userPrincipal
   * @return
   */
  @GetMapping(value = FRIENDS_PATH + "/{id1:[\\d]+}/{id2:[\\d]+}")
  public ResponseEntity getFriendship(@PathVariable String id1,@PathVariable String id2,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    logger.info("[GET /friends/{}/{}] - User: {}", id1, id2, userPrincipal.getUser().getEmail());
    Friend friend = friendService
        .getFriend(Long.parseLong(id1),Long.parseLong(id2), userPrincipal.getUser().getUserId());
    return ResponseBuilder.getSuccessResponse(dtoConverter.toFriendDto(friend));
  }

  /**
   * DELETE /friends/id1/id2
   *
   * @param id1 the id1 of the friendship
   * @param id2 the id2 of the friendship
   * @param userPrincipal the logged in user
   */
  @DeleteMapping(value = FRIENDS_PATH + "/{id1:[\\d]+}//{id2:[\\d]+}")
  public ResponseEntity deleteFriend(@PathVariable String id1, @PathVariable String id2,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    logger.info("[DELETE /friends/{}/{}] User: {}", id1, id2, userPrincipal.getUser().getEmail());
    friendService.deleteFriend(Long.parseLong(id1),Long.parseLong(id2), userPrincipal.getUser().getUserId());
    return ResponseBuilder.deleteSuccessResponse();
  }
}
