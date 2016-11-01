package com.blogggr.strategies;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 01.11.16.
 */
public interface ServiceInvocationStrategy {

    Object invokeService(Map<String,String> input);
}
