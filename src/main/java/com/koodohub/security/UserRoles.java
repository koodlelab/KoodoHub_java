package com.koodohub.security;

import java.util.Map;

public class UserRoles {

    private final String name;

    private final Map<String, Boolean> roles;


    public UserRoles(String userName, Map<String, Boolean> roles)
    {
        this.name = userName;
        this.roles = roles;
    }


    public String getName()
    {
        return this.name;
    }


    public Map<String, Boolean> getRoles()
    {
        return this.roles;
    }

}