package com.koodohub.jdbi;

import com.koodohub.core.Member;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public interface MemberDAO {

    @SqlUpdate("insert into members (name, email, password_digest) " +
            "values (:name, :email, :password_digest)")
    int create(@BindBean Member member);

    @SqlQuery("select * from users where email = :email")
    Member findByEmail(@Bind("email") String email);

    @SqlQuery("select * from users where name = :name")
    Member findByName(@Bind("name") String name);

}
