package com.koodohub.domain;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.FormParam;

public class UserEntry {

    @NotNull
    @FormParam("fullName")
    @Size(min=4, max=20)
    private String fullName;

    @NotNull
    @Email
    @FormParam("email")
    private String email;

    @NotNull @FormParam("password")
    @Size(min = 6, max = 100)
    private String password;

    @NotNull @FormParam("userName")
    @Size(max = 10)
    private String userName;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }



}
