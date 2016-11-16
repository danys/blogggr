package com.blogggr.strategies.invoker;

import com.blogggr.config.AppConfig;
import com.blogggr.controllers.SessionsController;
import com.blogggr.entities.Session;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.exceptions.WrongPasswordException;
import com.blogggr.requestdata.SessionPostData;
import com.blogggr.services.SessionService;
import com.blogggr.strategies.ServiceInvocationStrategy;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 13.11.16.
 */
public class InvokePostSessionService implements ServiceInvocationStrategy{

    private SessionService sessionService;

    public InvokePostSessionService(SessionService sessionService){
        this.sessionService = sessionService;
    }

    @Override
    public Object invokeService(Map<String,String> input, String body, Long userID) throws ResourceNotFoundException, WrongPasswordException{
        ObjectMapper mapper = new ObjectMapper();
        SessionPostData sessionData;
        try{
            sessionData = mapper.readValue(body, SessionPostData.class);
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
        Session session = sessionService.createSession(sessionData);
        //Create location string and session id hash. Then return it as a map.
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put(AppConfig.locationHeaderKey,AppConfig.fullBaseUrl + SessionsController.sessionPath + "/" + String.valueOf(session.getSessionid()));
        responseMap.put(AppConfig.authKey,session.getSessionhash());
        responseMap.put(AppConfig.validityUntilKey,session.getValidtill().toString().substring(0,19));
        return responseMap;
    }
}
