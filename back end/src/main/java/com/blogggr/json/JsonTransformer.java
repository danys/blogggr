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

    public static JsonNode filterFieldsOfMultiLevelObject(Object data, JsonFilter keysFilter){
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.convertValue(data, JsonNode.class);
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        ObjectNode root = nodeFactory.objectNode();
        Iterator<Map.Entry<String,JsonNode>> it = node.fields();
        JsonFilter filter;
        while(it.hasNext()){
            Map.Entry<String,JsonNode> curNode = it.next();
            if (curNode.getValue().isArray()){
                //The current json item is an array
                if (keysFilter.containsKey(curNode.getKey()+arrayMarker)){
                    filter = keysFilter.get(curNode.getKey()+arrayMarker);
                    JsonNode tempNode = curNode.getValue();
                    if(filter!=null) {
                        ArrayNode aNode = (ArrayNode)tempNode;
                        ArrayNode arrayRoot = nodeFactory.arrayNode(aNode.size());
                        JsonNode currentArrayNode;
                        //Loop through the array
                        for(int i=0;i<aNode.size();i++){
                            currentArrayNode = aNode.get(i);
                            arrayRoot.add(filterFieldsOfMultiLevelObject(currentArrayNode,filter));
                        }
                        tempNode = arrayRoot;
                    }
                    root.set(curNode.getKey(),tempNode);
                }
            }
            else if (keysFilter.containsKey(curNode.getKey())){
                //The current json item is NOT an array
                filter = keysFilter.get(curNode.getKey());
                JsonNode tempNode = curNode.getValue();
                if(filter!=null) tempNode = filterFieldsOfMultiLevelObject(curNode.getValue(),filter);
                root.set(curNode.getKey(),tempNode);
            }
        }
        return root;
    }
}
