package com.blogggr.controllers;

import com.blogggr.config.AppConfig;
import com.blogggr.models.AppModel;
import com.blogggr.models.AppModelImpl;
import com.blogggr.services.PostService;
import com.blogggr.services.UserService;
import com.blogggr.strategies.auth.AuthenticatedAuthorization;
import com.blogggr.strategies.invoker.InvokePostPostService;
import com.blogggr.strategies.responses.PostResponse;
import com.blogggr.strategies.validators.PostPostDataValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 19.11.16.
 */
@RestController
@RequestMapping(AppConfig.baseUrl)
public class PostsController {

    public static final String postsPath = "/posts";

    private UserService userService;
    private PostService postService;

    public PostsController(UserService userService, PostService postService){
        this.userService = userService;
        this.postService = postService;
    }

    //POST /posts
    @RequestMapping(path = postsPath, method = RequestMethod.POST)
    public ResponseEntity createUser(@RequestBody String bodyData, @RequestHeader Map<String,String> header){
        AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService), new PostPostDataValidator(), new InvokePostPostService(postService), new PostResponse());
        return model.execute(null,header,bodyData);
    }
}
