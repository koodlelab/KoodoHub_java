package com.koodohub.jdbi;

import com.koodohub.core.Member;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberMapper implements ResultSetMapper<Member> {

    @Override
    public Member map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new Member(rs.getString("fullname"),
                rs.getString("email"), rs.getString("password"), rs.getString("username"));
    }
}
