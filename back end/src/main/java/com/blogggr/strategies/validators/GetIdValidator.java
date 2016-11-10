package com.blogggr.strategies.validators;

import com.blogggr.strategies.ValidationStrategy;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 10.11.16.
 */
public class GetIdValidator implements ValidationStrategy {

    private String errorMessage;

    private final String idName = "id";

    public GetIdValidator(){
        //
    }

    public boolean inputIsValid(Map<String,String> input, String body){
        if (!input.containsKey(idName)){
            errorMessage = "Error getting resource id!";
            return false;
        }
        String idStr = input.get(idName);
        Long id;
        try{
            id = Long.parseLong(idStr);
        }
        catch(NumberFormatException e){
            errorMessage = "Resource id not a valid number!";
            return false;
        }
        errorMessage="";
        return true;
    }

    public String getError(){
        return errorMessage;
    }
}
