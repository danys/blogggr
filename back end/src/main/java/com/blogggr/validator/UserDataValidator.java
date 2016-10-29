package com.blogggr.validator;

import java.util.Map;

/**
 * Created by Daniel Sunnen on 29.10.16.
 */
public class UserDataValidator {

    //Names of the request fields
    private final String firstNameField = "firstName";
    private final String lastNameField = "lastName";
    private final String emailField = "email";
    private final String passwordField = "password";

    //The extracted data
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    private String extractData(Map<String, String> map, String field){
        if (map.containsKey(field)) return map.get(field);
        return null;
    }

    public UserDataValidator(Map<String, String> userData){
        //Extract first name, last name, email and password
        firstName = extractData(userData, firstNameField);
        lastName = extractData(userData, lastNameField);
        email = extractData(userData, emailField);
        password = extractData(userData, passwordField);
    }

    public boolean validate(){
        if (firstName == null || lastName == null || email == null || password == null) return false;
        if (firstName.length()<3 || lastName<3 || password<8) return false;
        if (!email.contains("@")) return false;
        int atIndex = email.indexOf("@");
        int dotIndex = email.indexOf(".",atIndex);
        if (dotIndex==-1) return false;
        return true;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
