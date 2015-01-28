package com.koodohub.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@NamedQueries({
        @NamedQuery(
                name = "Relationship.findFollowersByUser",
                query = "from Relationship r where r.followed = :username"
        ),
        @NamedQuery(
                name = "Relationship.findFollowingsByUser",
                query = "from Relationship r where r.following = :username"
        )
})
@Entity
@Table(name = "relationships")
public class Relationship implements Serializable {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="following")
    private User following;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed")
    private User followed;

    @Embedded
    private final AuditUpdate auditUpdate = new AuditUpdate();

    public void init(User follower, User followed) {
        auditUpdate.init();
        this.following = follower;
        this.followed = followed;
    }

    public User getFollowing() {
        return following;
    }

    public void setFollowing(User follower) {
        this.following = follower;
    }

    public User getFollowed() {
        return followed;
    }

    public void setFollowed(User followed) {
        this.followed = followed;
    }
}
