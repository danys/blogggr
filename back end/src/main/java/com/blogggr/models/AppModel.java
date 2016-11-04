package com.blogggr.models;

import org.springframework.http.ResponseEntity;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 01.11.16.
 */
public interface AppModel {

    ResponseEntity execute(Map<String,String> input, Map<String,String> header, String body);
}
