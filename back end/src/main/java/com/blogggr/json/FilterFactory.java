package com.blogggr.json;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 29.12.16.
 */
public class FilterFactory {

    public static JsonFilter getPostFilter(){
        Map<String, JsonFilter> filterMap = new HashMap<>();
        filterMap.put("postID",null);
        filterMap.put("shortTitle",null);
        filterMap.put("textBody",null);
        filterMap.put("timestamp",null);
        filterMap.put("title",null);
        filterMap.put("global",null);
        Map<String, JsonFilter> userFilterMap = new HashMap<>();
        userFilterMap.put("userID",null);
        userFilterMap.put("email",null);
        userFilterMap.put("lastName",null);
        userFilterMap.put("firstName",null);
        JsonFilter userFilter = new JsonFilter(userFilterMap);
        filterMap.put("user",userFilter);
        Map<String, JsonFilter> commentFilterMap = new HashMap<>();
        commentFilterMap.put("commentID",null);
        commentFilterMap.put("text",null);
        commentFilterMap.put("timestamp",null);
        commentFilterMap.put("user",userFilter);
        JsonFilter commentFilter = new JsonFilter(commentFilterMap);
        filterMap.put("commentsArray",commentFilter);
        JsonFilter jsonFilter = new JsonFilter(filterMap);
        return jsonFilter;
    }

    public static JsonFilter getCommentFilter(){
        Map<String, JsonFilter> filterMap = new HashMap<>();
        filterMap.put("commentID",null);
        filterMap.put("text",null);
        filterMap.put("timestamp",null);
        Map<String, JsonFilter> userFilterMap = new HashMap<>();
        userFilterMap.put("userID",null);
        userFilterMap.put("email",null);
        userFilterMap.put("lastName",null);
        userFilterMap.put("firstName",null);
        JsonFilter userFilter = new JsonFilter(userFilterMap);
        filterMap.put("user",userFilter);
        JsonFilter jsonFilter = new JsonFilter(filterMap);
        return jsonFilter;
    }

    public static JsonFilter getUserFilter(){
        Map<String, JsonFilter> userFilterMap = new HashMap<>();
        userFilterMap.put("userID",null);
        userFilterMap.put("email",null);
        userFilterMap.put("lastName",null);
        userFilterMap.put("firstName",null);
        JsonFilter jsonFilter = new JsonFilter(userFilterMap);
        return jsonFilter;
    }
}
