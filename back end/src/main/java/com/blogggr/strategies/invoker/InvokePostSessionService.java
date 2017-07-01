package com.blogggr.strategies.invoker;

import com.blogggr.config.AppConfig;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.exceptions.WrongPasswordException;
import com.blogggr.requestdata.SessionPostData;
import com.blogggr.services.SessionService;
import com.blogggr.services.SessionServiceImpl;
import com.blogggr.strategies.ServiceInvocationStrategy;
import com.blogggr.utilities.Cryptography;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 13.11.16.
 */
public class InvokePostSessionService implements ServiceInvocationStrategy{

    private SessionService sessionService;
    private Cryptography cryptography;

    public InvokePostSessionService(SessionService sessionService, Cryptography cryptography){
        this.sessionService = sessionService;
        this.cryptography = cryptography;
    }

    @Override
    public Object invokeService(Map<String,String> input, String body, Long userID) throws ResourceNotFoundException, DBException, WrongPasswordException, UnsupportedEncodingException{
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
        SessionServiceImpl.SessionDetails jwt = sessionService.createSession(sessionData);
        //Create location string and session id hash. Then return it as a map.
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put(AppConfig.authKey,jwt.jwt);
        //Format valid until timestamp as "YYYY-MM-DD HH:mm:ss"
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        responseMap.put(AppConfig.validityUntilKey,dateFormat.format(jwt.expiration));
        return responseMap;
    }
}
