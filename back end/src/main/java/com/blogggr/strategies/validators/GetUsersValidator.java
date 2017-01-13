package com.blogggr.strategies.validators;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 11.01.17.
 */
public class GetUsersValidator extends GenericValidator  {

    public GetUsersValidator(){
        //
    }

    public static final String searchKey = "search";
    public static final String pageKey = "page";
    public static final String limitKey = "limit";

    private final int minimumSearchLen = 3;

    @Override
    protected boolean validate(Map<String,String> input, String body){
        if (input.containsKey(searchKey)){
            String searchString = input.get(searchKey);
            if (searchString.length()<minimumSearchLen) return false;
        }
        //If no string has been provided the input is also accepted => wildcard search
        if (input.containsKey(pageKey) && !stringIsNumber(input.get(pageKey))) {
            errorMessage = "Page parameter is not a valid number!";
            return false;
        }
        if (input.containsKey(limitKey) && !stringIsNumber(input.get(limitKey))) {
            errorMessage = "Limit parameter is not a valid number!";
            return false;
        }
        return true;
    }
}
