package com.koodohub.domain;

import javax.persistence.*;

@NamedQueries({
        @NamedQuery(
                name = "Relationship.findFollowingByUsername",
                query = "from Relationship r where r.follower = :username"
        ),
        @NamedQuery(
                name = "Relationship.findFollowedByUsername",
                query = "from Relationship r where r.followed = :username"
        )
})
@Entity
@Table(name = "relationships")
public class Relationship {

    @Id
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="following")
    private User following;

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
