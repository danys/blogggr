package com.blogggr.controllers;

import com.blogggr.config.AppConfig;
import com.blogggr.models.AppModel;
import com.blogggr.models.AppModelImpl;
import com.blogggr.services.SessionService;
import com.blogggr.services.UserService;
import com.blogggr.strategies.auth.AuthenticatedAuthorization;
import com.blogggr.strategies.auth.NoAuthorization;
import com.blogggr.strategies.invoker.InvokeDeleteSessionService;
import com.blogggr.strategies.invoker.InvokePostSessionService;
import com.blogggr.strategies.responses.DeleteResponse;
import com.blogggr.strategies.responses.PostResponse;
import com.blogggr.strategies.validators.IdValidator;
import com.blogggr.strategies.validators.SessionPostDataValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 13.11.16.
 */
@RestController
@RequestMapping(AppConfig.baseUrl)
public class SessionsController {

    public static final String sessionPath = "/sessions";

    private SessionService sessionService;
    private UserService userService;

    public SessionsController(SessionService sessionService, UserService userService){
        this.sessionService = sessionService;
        this.userService = userService;
    }

    //POST /sessions
    @RequestMapping(path = sessionPath, method = RequestMethod.POST)
    public ResponseEntity createUser(@RequestBody String bodyData){
        AppModel model = new AppModelImpl(new NoAuthorization(), new SessionPostDataValidator(), new InvokePostSessionService(sessionService), new PostResponse());
        return model.execute(null,null,bodyData);
    }

    //PUT /sessions
    /*@RequestMapping(path = sessionPath+"/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateSession(@RequestParam String id, @RequestHeader Map<String,String> header) {
        Map<String,String> map = new HashMap<>();
        map.put("id", id);
        AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService), new IdValidator(), new InvokeUpdateSessionService(sessionService), new PutResponse());
        return model.execute(map,header,null);
    }*/

    //DELETE /sessions
    @RequestMapping(path = sessionPath+"/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteSession(@RequestParam String id, @RequestHeader Map<String,String> header){
        Map<String,String> map = new HashMap<>();
        map.put("id", id);
        AppModel model = new AppModelImpl(new AuthenticatedAuthorization(userService), new IdValidator(), new InvokeDeleteSessionService(sessionService), new DeleteResponse());
        return model.execute(map,header,null);
    }
}
