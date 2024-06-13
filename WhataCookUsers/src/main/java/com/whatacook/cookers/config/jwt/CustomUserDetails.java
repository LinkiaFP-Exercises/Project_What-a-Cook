package com.whatacook.cookers.config.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {

    private final String email;
    private final String id;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, String email, String id) {
        super(username, password, authorities);
        this.email = email;
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }
}
