package com.blogggr.strategies.validators;

import com.blogggr.strategies.ValidationStrategy;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 13.11.16.
 */
public abstract class GenericValidator implements ValidationStrategy{

    protected String errorMessage;

    @Override
    public boolean inputIsValid(Map<String, String> input, String body) {
        try{
            return validate(input, body);
        }
        catch(JsonParseException e){
            errorMessage = "JSON parse exception";
            return false;
        }
        catch(JsonProcessingException e){
            errorMessage = "JSON processing exception";
            return false;
        }
        catch(IOException e){
            errorMessage = "JSON input exception";
            return false;
        }
    }

    protected abstract boolean validate(Map<String, String> input, String body) throws JsonParseException, JsonProcessingException, IOException;

    @Override
    public final String getError(){
        return errorMessage;
    }
}
