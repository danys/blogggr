package com.blogggr.strategies.invoker;

import com.blogggr.exceptions.NotAuthorizedException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.exceptions.SessionExpiredException;
import com.blogggr.requestdata.SessionPutData;
import com.blogggr.requestdata.UserPostData;
import com.blogggr.services.SessionService;
import com.blogggr.strategies.ServiceInvocationStrategy;
import com.blogggr.strategies.auth.AuthenticatedAuthorization;
import com.blogggr.strategies.validators.IdValidator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 17.11.16.
 */

public class InvokeUpdateSessionService implements ServiceInvocationStrategy {

    private SessionService sessionService;

    public InvokeUpdateSessionService(SessionService sessionService){
        this.sessionService = sessionService;
    }

    public Object invokeService(Map<String,String> input, String body, Long userID) throws ResourceNotFoundException, NotAuthorizedException {
        if (!input.containsKey(IdValidator.idName)){
            return null;
        }
        String idStr = input.get(IdValidator.idName);
        Long id;
        try{
            id = Long.parseLong(idStr);
        }
        catch(NumberFormatException e){
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        SessionPutData sessionPutData;
        try{
            sessionPutData = mapper.readValue(body, SessionPutData.class);
        }
        catch(JsonParseException e){
            return null;
        }
        catch(JsonProcessingException e){
            return null;
        }
        catch(IOException e){
            return null;
        }
        try {
            sessionService.updateSession(id, userID, sessionPutData);
        }
        catch(SessionExpiredException e){
            throw new NotAuthorizedException(AuthenticatedAuthorization.sessionExpiredText);
        }
        return null;
    }
}
