package com.blogggr.controllers;

import com.blogggr.config.AppConfig;
import com.blogggr.models.AppModel;
import com.blogggr.models.AppModelImpl;
import com.blogggr.services.CommentService;
import com.blogggr.services.UserService;
import com.blogggr.strategies.auth.AuthenticatedAuthorization;
import com.blogggr.strategies.invoker.InvokePostCommentService;
import com.blogggr.strategies.responses.PostResponse;
import com.blogggr.strategies.validators.CommentPostDataValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
