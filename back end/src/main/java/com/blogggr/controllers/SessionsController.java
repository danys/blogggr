package com.blogggr.controllers;

import com.blogggr.config.AppConfig;
import com.blogggr.models.AppModel;
import com.blogggr.models.AppModelImpl;
import com.blogggr.services.SessionService;
import com.blogggr.strategies.auth.NoAuthorization;
import com.blogggr.strategies.invoker.InvokePostSessionService;
import com.blogggr.strategies.responses.PostResponse;
import com.blogggr.strategies.validators.SessionPostDataValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Daniel Sunnen on 13.11.16.
 */
@RestController
@RequestMapping(AppConfig.baseUrl)
public class SessionsController {

    public static final String sessionPath = "/sessions";

    private SessionService sessionService;

    public SessionsController(SessionService sessionService){
        this.sessionService = sessionService;
    }

    //POST /sessions
    @RequestMapping(path = sessionPath, method = RequestMethod.POST)
    public ResponseEntity createUser(@RequestBody String bodyData){
        AppModel model = new AppModelImpl(new NoAuthorization(), new SessionPostDataValidator(), new InvokePostSessionService(sessionService), new PostResponse());
        return model.execute(null,null,bodyData);
    }
}
