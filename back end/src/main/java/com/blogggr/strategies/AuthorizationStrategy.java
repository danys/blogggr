package com.blogggr.strategies;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 01.11.16.
 */
public interface AuthorizationStrategy {
    boolean isAuthorized(Map<String,String> header);

    Long getUserId(Map<String,String> header);

    String getError();
}
