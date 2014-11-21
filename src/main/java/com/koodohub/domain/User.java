package com.koodohub.domain;

import com.koodohub.security.Authorities;
import org.hibernate.validator.constraints.Email;
import org.joda.time.DateTime;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.FormParam;
import java.sql.Date;
import java.util.Collection;

public class User {

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

    private String role;

    public String getRole() {
        return role;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    private Date createdOn;

    private Date updatedOn;

    public User() {
    }

    public User(final String fullName, final String email, final String password,
                final String userName) {
        this.fullName = fullName;
        this.email = email;
        this.userName = userName;
        this.password = password;
    }

    public void prepareForSave() {
        this.password = passwordEncoder.encode(this.password);
        this.createdOn = this.updatedOn = new Date(System.currentTimeMillis());
        this.role = Authorities.ROLE_MEMBER;
    }

    public User(final String fullName, final String email, final String password,
                final String userName, final String role) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.role = role;
    }

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

}
