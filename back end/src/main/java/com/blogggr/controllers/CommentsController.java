package com.blogggr.controllers;

import com.blogggr.config.AppConfig;
import com.blogggr.models.AppModel;
import com.blogggr.models.AppModelImpl;
import com.blogggr.services.CommentService;
import com.blogggr.services.UserService;
import com.blogggr.strategies.auth.AuthenticatedAuthorization;
import com.blogggr.strategies.invoker.InvokePostCommentService;
import com.blogggr.strategies.responses.DeleteResponse;
import com.blogggr.strategies.responses.GetResponse;
import com.blogggr.strategies.responses.PostResponse;
import com.blogggr.strategies.responses.PutResponse;
import com.blogggr.strategies.validators.CommentPostDataValidator;
import com.blogggr.strategies.validators.CommentPutDataValidator;
import com.blogggr.strategies.validators.IdValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 05.12.16.
 */
@RestController
@RequestMapping(AppConfig.baseUrl)
public class CommentsController {

    public static final String commentsPath = "/comments";

    private UserService userService;
    private CommentService commentService;

    public CommentsController(UserService userService, CommentService commentService){
        this.userService = userService;
        this.commentService = commentService;
    }

    //POST /comments
    @RequestMapping(path = commentsPath, method = RequestMethod.POST)
    public ResponseEntity createComment(@RequestBody String bodyData, @RequestHeader Map<String,String> header){
        AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService), new CommentPostDataValidator(), new InvokePostCommentService(commentService), new PostResponse());
        return model.execute(null,header,bodyData);
    }

    //PUT /comments
    @RequestMapping(path = commentsPath+"/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateComment(@PathVariable String id,@RequestBody String bodyData, @RequestHeader Map<String,String> header){
        AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService), new CommentPutDataValidator(), new InvokeCommentPutService(commentService), new PutResponse());
        Map<String,String> map = new HashMap<>();
        map.put("id", id);
        return model.execute(map,header,bodyData);
    }

    //DELETE /comments
    @RequestMapping(path = commentsPath+"/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteComment(@PathVariable String id, @RequestHeader Map<String,String> header){
        Map<String,String> map = new HashMap<>();
        map.put("id", id);
        AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService), new IdValidator(), new InvokeDeleteCommentService(postService), new DeleteResponse());
        return model.execute(map,header,null);
    }

    //GET /comments/id
    @RequestMapping(path = commentsPath+"/{id}", method = RequestMethod.GET)
    public ResponseEntity getComment(@PathVariable String id, @RequestHeader Map<String,String> header){
        Map<String,String> map = new HashMap<>();
        map.put("id", id);
        AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService), new IdValidator(), new InvokeGetCommentService(postService), new GetResponse());
        return model.execute(map,header,null);
    }

    //GET /posts/id/comments
    @RequestMapping(path = PostsController.postsPath+"/{id}"+commentsPath, method = RequestMethod.GET)
    public ResponseEntity getCommentsByPostId(@PathVariable String id, @RequestHeader Map<String,String> header){
        Map<String,String> map = new HashMap<>();
        map.put("id", id);
        AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService), new IdValidator(), new InvokeGetCommentsService(postService), new GetResponse());
        return model.execute(map,header,null);
    }

}
