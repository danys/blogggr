package com.blogggr.requestdata;

/**
 * Created by Daniel Sunnen on 13.11.16.
 */
public class SessionPostData {

    private String email;
    private String password;

    public SessionPostData(){
        //
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

}
