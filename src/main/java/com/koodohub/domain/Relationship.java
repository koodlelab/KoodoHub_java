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

    @Column(name = "follower", nullable = false)
    private String follower;

    @Column(name = "followed", nullable = false)
    private String followed;

    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public String getFollowed() {
        return followed;
    }

    public void setFollowed(String followed) {
        this.followed = followed;
    }
}
