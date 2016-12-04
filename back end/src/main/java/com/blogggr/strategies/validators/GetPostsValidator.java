package com.blogggr.strategies.validators;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 04.12.16.
 */
public class GetPostsValidator extends GenericValidator {

    public GetPostsValidator(){
        //nothing to do
    }

    public static final String titleKey = "title";
    public static final String posterUserIDKey = "posterUserID";
    public static final String visibilityKey = "visibility"; //can be "onlyGlobal", "all", "onlyFriends", "onlyCurrentUser"

    @Override
    protected boolean validate(Map<String,String> input, String body){
        String idStr = null;
        Long id = null;
        if (input.containsKey(posterUserIDKey)) {
            idStr = input.get(posterUserIDKey);
            try{
                long idp = Long.parseLong(idStr);
                id = new Long(idp);
            }
            catch(NumberFormatException e){
                //Not a valid number
                errorMessage = "Poster user ID not a valid number!";
                return false;
            }
        }
        //Check if titleKey is present
        String title = null;
        if (input.containsKey(titleKey)){
            title = input.get(titleKey);
            if (title.length()<3 || title.length()>100){
                errorMessage = "Title have a length between 3 and 100 characters!";
                return false;
            }
        }
        //Check the visibility setting
        String visibility = null;
        if (input.containsKey(visibilityKey)) {
            visibility = input.get(visibilityKey);
            if ((visibility!=null) &&
                    (visibility.compareTo("onlyGlobal")!=0) &&
                    (visibility.compareTo("all")!=0) &&
                    (visibility.compareTo("onlyFriends")!=0) &&
                    (visibility.compareTo("onlyCurrentUser")!=0)){
                errorMessage = "If provided visibility must be either of: 'onlyGlobal', 'all', 'onlyFriends', 'onlyCurrentUser'!";
                return false;
            }
        }
        return true;
    }
}
