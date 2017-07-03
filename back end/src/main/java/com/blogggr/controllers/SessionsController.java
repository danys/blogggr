package com.blogggr.controllers;

import com.blogggr.config.AppConfig;
import com.blogggr.models.AppModel;
import com.blogggr.models.AppModelImpl;
import com.blogggr.services.SessionService;
import com.blogggr.strategies.auth.NoAuthorization;
import com.blogggr.strategies.invoker.InvokePostSessionService;
import com.blogggr.strategies.responses.PostResponse;
import com.blogggr.strategies.validators.SessionPostDataValidator;
import com.blogggr.utilities.Cryptography;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Daniel Sunnen on 13.11.16.
 */
@RestController
@RequestMapping(AppConfig.baseUrl)
public class SessionsController {

    public static final String sessionPath = "/sessions";

    private SessionService sessionService;
    private Cryptography cryptography;

    private final Log logger = LogFactory.getLog(this.getClass());

    public SessionsController(SessionService sessionService, Cryptography cryptography){
        this.sessionService = sessionService;
        this.cryptography = cryptography;
    }

    //POST /sessions
    @RequestMapping(path = sessionPath, method = RequestMethod.POST)
    public ResponseEntity createSession(@RequestBody String bodyData){
        logger.debug("[POST /sessions]");
        AppModel model = new AppModelImpl(new NoAuthorization(), new SessionPostDataValidator(), new InvokePostSessionService(sessionService, cryptography), new PostResponse());
        return model.execute(null,null,bodyData);
    }
}
