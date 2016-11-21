package com.blogggr.utilities;

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
}
