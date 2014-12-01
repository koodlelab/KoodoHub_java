package com.koodohub.jdbc;

import com.google.common.base.Optional;
import com.koodohub.domain.User;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

public class UserDAO extends AbstractDAO<User> implements UserDetailsService {

    public UserDAO(SessionFactory factory) {
        super(factory);
    }

    public User create(User user) {
        return persist(user);
    }

    public List<User> findAllUsers() {
        return list(namedQuery("User.findAll"));
    }

    public Optional<User> findByUsername(String username) {
        return Optional.fromNullable(uniqueResult(namedQuery("User.findByUsername")
                .setString("username", username)));
    }

    public Optional<User> findByEmail(String email) {
        return Optional.fromNullable(uniqueResult(namedQuery("User.findByEmail")
                .setString("email", email)));
    }

    public Optional<User> findByLogin(String login) {
        return Optional.fromNullable(uniqueResult(namedQuery("User.findByLogin")
                .setString("login", login)));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByUsername(username).get();
    }
}