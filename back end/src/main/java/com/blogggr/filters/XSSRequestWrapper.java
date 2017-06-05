package com.blogggr.filters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 05.06.17.
 */
public class XSSRequestWrapper extends HttpServletRequestWrapper {

    public XSSRequestWrapper(HttpServletRequest request){
        super(request);
    }

    private String sanitizeString(String value){
        if (value==null) return null;
        //TODO: Sanitization
        return value;
    }

    @Override
    public String getParameter(String name){
        String str = super.getParameter(name);
        return sanitizeString(str);
    }

    @Override
    public Map<String,String[]> getParameterMap(){
        Map<String, String[]> map = super.getParameterMap();
        Iterator<Map.Entry<String, String[]>> it = map.entrySet().iterator();
        Map.Entry<String, String[]> entry;
        String[] array;
        while(it.hasNext()){
            entry = it.next();
            array = entry.getValue();
            for(int i=0;i<array.length;i++){
                array[i]=sanitizeString(array[i]);
            }
            entry.setValue(array);
        }
        return map;
    }

    @Override
    public String[] getParameterValues(String name){
        String[] array = super.getParameterValues(name);
        for(int i=0;i<array.length;i++){
            array[i]=sanitizeString(array[i]);
        }
        return array;
    }
}
