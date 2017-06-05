package com.blogggr.filters;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by Daniel Sunnen on 05.06.17.
 */
public class XSSRequestWrapper extends HttpServletRequestWrapper {

    private Pattern[] patterns;

    public XSSRequestWrapper(HttpServletRequest request){
        super(request);
        this.patterns = new Pattern[]{
                Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
                Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
                Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
                Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
                Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
                Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
                Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
                Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
                Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
                Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
        };
    }

    private String sanitizeString(String value){
        if (value==null) return null;
        if (value.length()==0) return value;
        for (Pattern pattern : patterns){
            value = pattern.matcher(value).replaceAll("");
        }
        return value;
    }

    private String sanitizeJSONString(String value) throws IOException{
        if (value==null) return null;
        if (value.length()==0) return value;
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.readValue(value, new TypeReference<Map<String, String>>(){});
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        Map.Entry<String, String> entry;
        String mapVal;
        while(it.hasNext()) {
            entry = it.next();
            mapVal = entry.getValue();
            entry.setValue(sanitizeString(mapVal));
        }
        return mapper.writeValueAsString(map);
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

    @Override
    public BufferedReader getReader() throws IOException{
        BufferedReader br = super.getReader();
        StringBuffer sb = new StringBuffer();
        String line;
        while((line = br.readLine())!=null) sb.append(line);
        line = sb.toString();
        line = sanitizeJSONString(line);
        InputStream is = new ByteArrayInputStream(line.getBytes());
        return new BufferedReader(new InputStreamReader(is));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException{
        ServletInputStream sis = super.getInputStream();
        Scanner s = new Scanner(sis).useDelimiter("\\A");
        String input = s.hasNext() ? s.next() : "";
        input = sanitizeJSONString(input);
        final byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
        ServletInputStream stream = new ServletInputStream() {
            private int lastIndexRetrieved = -1;
            private ReadListener readListener = null;

            @Override
            public boolean isFinished() {
                return (lastIndexRetrieved == inputBytes.length-1);
            }

            @Override
            public boolean isReady() {
                return isFinished();
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                this.readListener = readListener;
                if (!isFinished()) {
                    try {
                        readListener.onDataAvailable();
                    } catch (IOException e) {
                        readListener.onError(e);
                    }
                } else {
                    try {
                        readListener.onAllDataRead();
                    } catch (IOException e) {
                        readListener.onError(e);
                    }
                }
            }

            @Override
            public int read() throws IOException {
                int i;
                if (!isFinished()) {
                    i = inputBytes[lastIndexRetrieved+1];
                    lastIndexRetrieved++;
                    if (isFinished() && (readListener != null)) {
                        try {
                            readListener.onAllDataRead();
                        } catch (IOException ex) {
                            readListener.onError(ex);
                            throw ex;
                        }
                    }
                    return i;
                } else {
                    return -1;
                }
            }
        };
        return stream;
    }
}
