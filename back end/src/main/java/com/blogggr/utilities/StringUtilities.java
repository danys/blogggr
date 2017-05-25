package com.blogggr.utilities;

import java.util.List;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 21.11.16.
 */
public class StringUtilities {

    //Compacts a title. Truncation occurs if the title is too long
    public static String compactTitle(String title){
        title = title.toLowerCase();
        StringBuilder sb = new StringBuilder();
        char[] chars = title.toCharArray();
        int len=0;
        for(int i=0;i<chars.length;i++){
            if (chars[i]!=' ') {sb.append(chars[i]);len++;}
            else {sb.append('-');len++;}
            if (len>30) break;
        }
        return sb.toString();
    }

    public static String buildQueryStringFromListOfKVPairs(List<Map.Entry<String,String>> listKVs){
        StringBuilder sb = new StringBuilder();
        Map.Entry<String, String> entry;
        for(int i=0;i<listKVs.size();i++){
            entry = listKVs.get(i);
            if (entry.getKey()!=null && entry.getKey().compareTo("null")!=0 &&
                    entry.getValue()!=null && entry.getValue().compareTo("null")!=0) {
                if (i!=0 && sb.length()>0) sb.append("&");
                sb.append(entry.getKey()+"="+entry.getValue());
            }
        }
        return sb.toString();
    }
}
