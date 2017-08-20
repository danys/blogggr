package com.blogggr.json;

import com.blogggr.config.AppConfig;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Created by Daniel Sunnen on 30.10.16.
 */
public class JSONResponseBuilder {

  public static ObjectNode basicObject() {
    JsonNodeFactory factory = JsonNodeFactory.instance;
    ObjectNode root = factory.objectNode();
    root.put("apiVersion", AppConfig.apiVersion);
    return root;
  }

  public static ObjectNode generateErrorResponse(String errorMessage) {
    ObjectNode root = basicObject();
    root.put("error", errorMessage);
    return root;
  }

  public static ObjectNode generateSuccessResponse(Object data) {
    ObjectNode root = basicObject();
    root.putPOJO("data", data);
    return root;
  }

  public static ObjectNode generatePageSuccessResponse(Object data, PageData pageData) {
    ObjectNode root = generateSuccessResponse(data);
    root.putPOJO("paging", pageData);
    return root;
  }
}
