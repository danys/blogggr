package com.blogggr.strategies.validators;


import com.blogggr.config.AppConfig;
import com.blogggr.requestdata.SessionPostData;
import com.blogggr.requestdata.SessionPutData;
import com.blogggr.strategies.ValidationStrategy;
import com.blogggr.utilities.TimeUtilities;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 18.11.16.
 */
public class SessionPutDataValidator extends GenericValidator{

    public static final String idName = "id";

    public SessionPutDataValidator(){
        //
    }

    @Override
    protected boolean validate(Map<String,String> input, String body) throws JsonProcessingException,JsonParseException, IOException {
        //Check that the primary key in the map is a valid number
        if (!input.containsKey(idName)){
            errorMessage = "Error getting resource id!";
            return false;
        }
        if (!stringIsNumber(input.get(idName))){
            errorMessage = "Resource id not a valid number!";
            return false;
        }
        errorMessage="";
        //Check that the body contains a valid object
        ObjectMapper mapper = new ObjectMapper();
        SessionPutData sessionPutData = mapper.readValue(body, SessionPutData.class);
        Long validTill = sessionPutData.getValidTill();
        if (validTill==null){
            errorMessage = "Request body must contain a validTill key value pair!";
            return false;
        }
        //Check that the validity time is within the maximum specified in AppConfig
        Timestamp ts = TimeUtilities.getCurrentTimestamp();
        Long millisMax = ts.getTime()+AppConfig.sessionValidityMillis;
        if (validTill>millisMax){
            errorMessage = "Cannot extend session for longer than "+Long.toString(AppConfig.sessionValidityMillis/1000)+" seconds!";
            return false;
        }
        return true;
    }

}
