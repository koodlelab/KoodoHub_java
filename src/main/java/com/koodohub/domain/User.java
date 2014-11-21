package com.koodohub.domain;

import org.hibernate.validator.constraints.Email;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.FormParam;
import java.sql.Date;


@NamedQueries({
    @NamedQuery(
        name = "User.findAll",
        query = "from User u"
    ),
    @NamedQuery(
        name = "User.findByUsername",
        query = "from User u where u.username = :username"
    ),
    @NamedQuery(
        name = "User.findByEmail",
        query = "from User u where u.email = :email"
    ),
    @NamedQuery(
        name = "User.findByLogin",
        query = "from User u where u.username = :login or u.email = :login"
    )
})
@Entity
@Table(name = "users")
public class User {

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(11);

    @Column(name = "fullname", nullable = false)
    private String fullname;
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    @Id
    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "createdon", nullable = false)
    private Date createdOn;

    @Column(name = "updatedon", nullable = false)
    private Date updatedOn;

    public User(final String fullName, final String email, final String password,
                final String userName, final String role) {
        this.fullname = fullName;
        this.email = email;
        this.username = userName;
        this.password = password;
        this.password = passwordEncoder.encode(this.password);
        this.role = role;
        this.createdOn = this.updatedOn = new Date(System.currentTimeMillis());
    }

    public boolean isCorrectPassword(final String password) {
        return passwordEncoder.matches(password, this.password);
    }

    public String getFullName() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }
}
