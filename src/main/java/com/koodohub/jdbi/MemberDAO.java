package com.koodohub.jdbi;

import com.koodohub.core.Member;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public interface MemberDAO {

    @SqlUpdate("insert into members (fullName, email, password, userName) " +
            "values (:fullName, :email, :password, :userName)")
    int create(@BindBean Member member);

    @SqlQuery("select * from users where userName = :userName")
    Member findByUsername(@Bind("userName") String username);

}
