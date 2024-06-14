package com.whatacook.cookers.config.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Custom security component for checking user permissions.
 * Provides a method to check if the authenticated user is either the owner or an admin.
 * <p>
 * Annotations:
 * - @Component: Indicates that this class is a Spring component.
 * <p>
 * Methods:
 * - isSelfOrAdmin(Authentication authentication, String userId, String email): Checks if the authenticated user is the owner or an admin.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@Component("customSecurity")
public class CustomSecurity {

    public boolean isSelfOrAdmin(Authentication authentication, String userId, String email) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return (userDetails.getId().equals(userId)) || (userDetails.getEmail().equals(email))
                || authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }
}
