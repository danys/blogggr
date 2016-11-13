package com.blogggr.requestdata;

/**
 * Created by Daniel Sunnen on 13.11.16.
 */
public class SessionPostData {

    private String email;
    private String password;
    private Boolean rememberMe;

    public SessionPostData(String email, String password, Boolean rememberMe){
        this.email = email;
        this.password = password;
        this.rememberMe = rememberMe;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Boolean isRememberMe() {
        return rememberMe;
    }
}
