package com.koodohub.core;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.FormParam;

public class Member {

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(11);
    @NotNull
    @FormParam("fullName")
    @Size(min=4, max=20)
    private String fullName;
    @NotNull
    @Email @FormParam("email")
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

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

    public void encryptPassword() {
        this.password = passwordEncoder.encode(this.password);
    }

}
