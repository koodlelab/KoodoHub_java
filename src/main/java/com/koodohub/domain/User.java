package com.koodohub.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.koodohub.security.JsonViews;
import com.koodohub.util.RandomUtil;
import org.hibernate.validator.constraints.Email;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.ws.rs.FormParam;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    @JsonIgnore
    private String password;

    @Id
//    @NotNull
    @FormParam("username")
    @Size(max = 30)
    @Column(name = "username", nullable = false)
    @JsonView(JsonViews.User.class)
    private String username;

    @Column(name = "role", nullable = false)
    @JsonIgnore
    private String role;

    @Column(name = "activated", nullable = false)
    @JsonIgnore
    private boolean activated;

    @Size(min = 0, max = 255)
    @Column(name = "activationkey", length = 255)
    @JsonIgnore
    private String activationKey;

    @Column(name = "avatarlink", length = 255)
    private String avatarLink;

    @Column(name = "coverlink", length = 255)
    private String coverLink;

    @OneToMany(fetch = FetchType.LAZY, mappedBy="user",
            cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Project> projects = new HashSet<>();

//    @OneToMany(fetch = FetchType.LAZY, mappedBy="following",
//            cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonIgnore
//    private transient Set<Relationship> followings = new HashSet<>();
//
//    @OneToMany(fetch = FetchType.LAZY, mappedBy="followed",
//            cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonIgnore
//    private transient Set<Relationship> followers = new HashSet<>();

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
        this.coverLink = "dcovers/cover_"+RandomUtil.randInt(1, 7)+".jpg";
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
    public void setCoverLink(String coverLink) {
        this.coverLink = coverLink;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    //TODO remove this from view for security
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

    public String getCoverLink() { return coverLink; }

    public Set<Project> getProjects() {
        return this.projects;
    }

//    public Set<Relationship> getFollowing() {
//        return this.followings;
//    }
//
//    public Set<Relationship> getFollowed() {
//        return this.followers;
//    }
}
