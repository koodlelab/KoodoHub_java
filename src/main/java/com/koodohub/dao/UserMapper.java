package com.koodohub.dao;

import com.koodohub.domain.User;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements ResultSetMapper<User> {

    @Override
    public User map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new User(rs.getString("fullname"),
                rs.getString("email"), rs.getString("password"), rs.getString("username"),
                rs.getString("role"));
    }
}
