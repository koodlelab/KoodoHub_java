package com.koodohub.dao;

import com.koodohub.domain.User;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.sql.Date;

@RegisterMapper(UserMapper.class)
public interface UserDAO extends UserDetailsService {

    @SqlUpdate("insert into users (fullName, email, password, userName, role, createdOn, updatedOn) " +
            "values (:fullName, :email, :password, :userName, :role, :createdOn, :updatedOn)")
    int create(@Bind("fullName") String fullName,
               @Bind("email") String email,
               @Bind("password") String password,
               @Bind("userName") String userName,
               @Bind("role") String role,
               @Bind("createdOn") Date createdTime,
               @Bind("updatedOn") Date updatedTime);

    @SqlQuery("select * from users where userName = :userName")
    User findByUsername(@Bind("userName") String username);

    @SqlQuery("select * from users where email = :email")
    User findByEmail(@Bind("email") String email);

    @Override
    @SqlQuery("select * from users where username = :loginName or email = :loginName")
    public UserDetails loadUserByUsername(@Bind("loginName") String loginName) throws UsernameNotFoundException;
}
