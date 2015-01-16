package com.koodohub.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.koodohub.security.JsonViews;
import com.koodohub.util.RandomUtil;
import org.hibernate.validator.constraints.Email;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.ws.rs.FormParam;

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

    //    @NotNull
    @FormParam("fullname")
    @Size(min=4, max=20)
    @Column(name = "fullname", nullable = false)
    @JsonView(JsonViews.User.class)
    private String fullname;

//    @NotNull
    @Email @FormParam("email")
    @Column(name = "email", nullable = false)
    @JsonView(JsonViews.User.class)
    private String email;

//    @NotNull
    @FormParam("password")
    @Size(min = 6, max = 100)
    @Column(name = "password", nullable = false)
    private String password;

    @Id
//    @NotNull
    @FormParam("username")
    @Size(max = 30)
    @Column(name = "username", nullable = false)
    @JsonView(JsonViews.User.class)
    private String username;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "activated", nullable = false)
    private boolean activated;

    @Size(min = 0, max = 255)
    @Column(name = "activationkey", length = 255)
    private String activationKey;

    @Column(name = "avatarlink", length = 255)
    private String avatarLink;

    @Embedded
    private final AuditUpdate auditUpdate;

    public User() {
        this.auditUpdate = new AuditUpdate();
    }

    public void init(final String fullName, final String email, final String password,
                final String userName, final String role) {
        this.auditUpdate.init();
        this.fullname = fullName;
        this.email = email;
        this.username = userName;
        this.password = passwordEncoder.encode(password);
        this.role = role;
        this.activated = false;
        this.activationKey = RandomUtil.generateActivationKey();
        this.avatarLink = "davatars/avatar_"+RandomUtil.randInt(1, 7)+".jpg";
    }

    public boolean isCorrectPassword(final String password) {
        return passwordEncoder.matches(password, this.password);
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void updatePassword(String password) {
        this.password = passwordEncoder.encode(password);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }

    public String getFullName() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public boolean isActivated() {
        return this.activated;
    }

    public String getActivationKey() {
        return activationKey;
    }

    public String getRole() {
        return role;
    }


    public String getAvatarLink() {
        return avatarLink;
    }
}
