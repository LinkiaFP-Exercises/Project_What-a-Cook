package com.whatacook.cookers.config.jwt;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Custom user details class that extends Spring Security's User class.
 * Adds additional fields for email and ID.
 * <p>
 * Annotations:
 * - @Getter: Generates getter methods for all fields.
 * <p>
 * Fields:
 * - email: The email address of the user.
 * - id: The unique identifier of the user.
 * <p>
 * Methods:
 * - CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, String email, String id):
 * Constructor to initialize the custom user details.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@Getter
public class CustomUserDetails extends User {

    private final String email;
    private final String id;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, String email, String id) {
        super(username, password, authorities);
        this.email = email;
        this.id = id;
    }
}
