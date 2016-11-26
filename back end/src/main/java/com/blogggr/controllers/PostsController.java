package com.blogggr.controllers;

import com.blogggr.config.AppConfig;
import com.blogggr.models.AppModel;
import com.blogggr.models.AppModelImpl;
import com.blogggr.services.PostService;
import com.blogggr.services.UserService;
import com.blogggr.strategies.auth.AuthenticatedAuthorization;
import com.blogggr.strategies.invoker.InvokeDeletePostService;
import com.blogggr.strategies.invoker.InvokePostPostService;
import com.blogggr.strategies.invoker.InvokePostPutService;
import com.blogggr.strategies.responses.DeleteResponse;
import com.blogggr.strategies.responses.PostResponse;
import com.blogggr.strategies.responses.PutResponse;
import com.blogggr.strategies.validators.IdValidator;
import com.blogggr.strategies.validators.PostPostDataValidator;
import com.blogggr.strategies.validators.PostPutDataValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    public ResponseEntity createPost(@RequestBody String bodyData, @RequestHeader Map<String,String> header){
        AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService), new PostPostDataValidator(), new InvokePostPostService(postService), new PostResponse());
        return model.execute(null,header,bodyData);
    }

    //PUT /posts
    @RequestMapping(path = postsPath+"/{id}", method = RequestMethod.PUT)
    public ResponseEntity updatePost(@PathVariable String id,@RequestBody String bodyData, @RequestHeader Map<String,String> header){
        AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService), new PostPutDataValidator(), new InvokePostPutService(postService), new PutResponse());
        Map<String,String> map = new HashMap<>();
        map.put("id", id);
        return model.execute(map,header,bodyData);
    }

    //DELETE /posts
    @RequestMapping(path = postsPath+"/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deletePost(@PathVariable String id, @RequestHeader Map<String,String> header){
        Map<String,String> map = new HashMap<>();
        map.put("id", id);
        AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService), new IdValidator(), new InvokeDeletePostService(postService), new DeleteResponse());
        return model.execute(map,header,null);
    }

    //GET /posts/id
    /*@RequestMapping(path = postsPath+"/{id}", method = RequestMethod.GET)
    public ResponseEntity getPost(@PathVariable String id, @RequestHeader Map<String,String> header){
        Map<String,String> map = new HashMap<>();
        map.put("id", id);
        AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService), new IdValidator(), new InvokeGetPostService(postService), new GetResponse());
        return model.execute(map,header,null);
    }*/
}
