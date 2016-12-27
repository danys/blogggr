package com.blogggr.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Daniel Sunnen on 10.11.16.
 */
public class JsonTransformer {

    private static final String arrayMarker = "Array";

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

    public static JsonNode filterFieldsOfFlatArrayObject(Object data, Set<String> keysToKeep){
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.convertValue(data, JsonNode.class);
        if (!jsonNode.isArray()) return null; //error
        ArrayNode aNode = (ArrayNode)jsonNode;
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        ArrayNode arrayRoot = nodeFactory.arrayNode(aNode.size());
        JsonNode node;
        //Loop through the array
        for(int i=0;i<aNode.size();i++){
            node = aNode.get(i);
            ObjectNode oNode = nodeFactory.objectNode();
            Iterator<Map.Entry<String,JsonNode>>  it = node.fields();
            while(it.hasNext()){
                Map.Entry<String,JsonNode> curNode = it.next();
                if (keysToKeep.contains(curNode.getKey())){
                    oNode.set(curNode.getKey(),curNode.getValue());
                }
            }
            arrayRoot.add(oNode);
        }
        return arrayRoot;
    }

    public static JsonNode filterFieldsOfTwoLevelObject(Object data, Map<String, Set<String>> keysToKeep){
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.convertValue(data, JsonNode.class);
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        ObjectNode root = nodeFactory.objectNode();
        Iterator<Map.Entry<String,JsonNode>> it = node.fields();
        Set<String> keySet;
        while(it.hasNext()){
            Map.Entry<String,JsonNode> curNode = it.next();
            if (curNode.getValue().isArray()){
                //The current json item is an array
                if (keysToKeep.containsKey(curNode.getKey()+arrayMarker)){
                    keySet = keysToKeep.get(curNode.getKey()+arrayMarker);
                    if (keySet==null) root.set(curNode.getKey(),curNode.getValue());
                    else root.set(curNode.getKey(),filterFieldsOfFlatArrayObject(curNode.getValue(),keySet));
                }
            }
            else if (keysToKeep.containsKey(curNode.getKey())){
                //The current json item is NOT an array
                keySet = keysToKeep.get(curNode.getKey());
                if (keySet==null) root.set(curNode.getKey(),curNode.getValue());
                else root.set(curNode.getKey(),filterFieldsOfFlatObject(curNode.getValue(),keySet));
            }
        }
        return root;
    }
}
