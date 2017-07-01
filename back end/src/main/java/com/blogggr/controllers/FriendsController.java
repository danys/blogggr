package com.blogggr.controllers;

import com.blogggr.config.AppConfig;
import com.blogggr.models.AppModel;
import com.blogggr.models.AppModelImpl;
import com.blogggr.services.FriendService;
import com.blogggr.services.UserService;
import com.blogggr.strategies.auth.AuthenticatedAuthorization;
import com.blogggr.strategies.invoker.InvokeDeleteFriendService;
import com.blogggr.strategies.invoker.InvokeGetFriendsService;
import com.blogggr.strategies.invoker.InvokePostFriendService;
import com.blogggr.strategies.invoker.InvokePutFriendService;
import com.blogggr.strategies.responses.DeleteResponse;
import com.blogggr.strategies.responses.GetResponse;
import com.blogggr.strategies.responses.PostResponse;
import com.blogggr.strategies.responses.PutResponse;
import com.blogggr.strategies.validators.FriendPostDataValidator;
import com.blogggr.strategies.validators.FriendPutDataValidator;
import com.blogggr.strategies.validators.IdValidator;
import com.blogggr.strategies.validators.NoCheckValidator;
import com.blogggr.utilities.Cryptography;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 11.12.16.
 */
@RestController
@RequestMapping(AppConfig.baseUrl)
public class FriendsController {

    public static final String friendsPath = "/friends";

    private UserService userService;
    private FriendService friendService;
    private Cryptography cryptography;

    public FriendsController(UserService userService, FriendService friendService, Cryptography cryptography){
        this.userService = userService;
        this.friendService = friendService;
        this.cryptography = cryptography;
    }

    //POST /friends
    @RequestMapping(path = friendsPath, method = RequestMethod.POST)
    public ResponseEntity createFriendship(@RequestBody String bodyData, @RequestHeader Map<String,String> header){
        AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService, cryptography), new FriendPostDataValidator(), new InvokePostFriendService(friendService), new PostResponse());
        return model.execute(null,header,bodyData);
    }

    //PUT /friends/id1/id2
    @RequestMapping(path = friendsPath+"/{id}/{id2}", method = RequestMethod.PUT)
    public ResponseEntity updateFriendship(@PathVariable String id, @PathVariable String id2, @RequestBody String bodyData, @RequestHeader Map<String,String> header){
        AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService, cryptography), new FriendPutDataValidator(), new InvokePutFriendService(friendService), new PutResponse());
        Map<String,String> map = new HashMap<>();
        map.put("id", id);
        map.put("id2", id2);
        return model.execute(map,header,bodyData);
    }

    //GET /friends
    @RequestMapping(path = friendsPath, method = RequestMethod.GET)
    public ResponseEntity getFriends(@RequestParam Map<String,String> params, @RequestHeader Map<String,String> header){
        AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService, cryptography), new NoCheckValidator(), new InvokeGetFriendsService(friendService), new GetResponse());
        return model.execute(params,header,null);
    }

    //DELETE /friends/id
    @RequestMapping(path = friendsPath+"/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteFriend(@PathVariable String id, @RequestHeader Map<String,String> header){
        Map<String,String> map = new HashMap<>();
        map.put("id", id);
        AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService, cryptography), new IdValidator(), new InvokeDeleteFriendService(friendService), new DeleteResponse());
        return model.execute(map,header,null);
    }
}
