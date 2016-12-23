package com.blogggr.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Daniel Sunnen on 10.11.16.
 */
public class JsonTransformer {

    public static JsonNode filterFieldsOfFlatObject(Object data, Set<String> keysToKeep){
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.convertValue(data, JsonNode.class);
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        ObjectNode root = nodeFactory.objectNode();
        Iterator<Map.Entry<String,JsonNode>>  it = node.fields();
        while(it.hasNext()){
            Map.Entry<String,JsonNode> curNode = it.next();
            if (keysToKeep.contains(curNode.getKey())){
                root.set(curNode.getKey(),curNode.getValue());
            }
        }
        return root;
    }

    public static JsonNode filterFieldsOfTwoLevelObject(Object data, Map<String, Set<String>> keysToKeep){
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.convertValue(data, JsonNode.class);
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        ObjectNode root = nodeFactory.objectNode();
        Iterator<Map.Entry<String,JsonNode>>  it = node.fields();
        while(it.hasNext()){
            Map.Entry<String,JsonNode> curNode = it.next();
            if (keysToKeep.containsKey(curNode.getKey())){
                root.set(curNode.getKey(),filterFieldsOfFlatObject(curNode.getValue(),keysToKeep.get(curNode.getKey())));
            }
        }
        return root;
    }
}
