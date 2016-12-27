package com.blogggr.json;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 27.12.16.
 */
public class JsonFilter {

    private Map<String, JsonFilter> keysFilter;

    public JsonFilter(Map<String, JsonFilter> keysFilter){
        this.keysFilter = keysFilter;
    }

    public boolean containsKey(String key){
        return keysFilter.containsKey(key);
    }

    public JsonFilter get(String key){
        return keysFilter.get(key);
    }
}
