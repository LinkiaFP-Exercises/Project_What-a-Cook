package com.whatacook.cookers.config.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("customSecurity")
public class CustomSecurity {

    public boolean isSelfOrAdmin(Authentication authentication, String userId, String email) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return (userDetails.getId().equals(userId)) || (userDetails.getEmail().equals(email))
                || authentication.getAuthorities().stream()
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }
}
