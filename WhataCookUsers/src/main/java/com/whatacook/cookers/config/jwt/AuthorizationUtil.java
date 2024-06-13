package com.whatacook.cookers.config.jwt;

import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.responses.Response;
import com.whatacook.cookers.model.users.UserJson;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

public class AuthorizationUtil {

    public static Mono<Response> executeIfAuthorized(UserJson userJson,
                                                     BiFunction<UserJson, UserDetails, Mono<Response>> action) {
        return getAuthentication()
                .flatMap(authentication -> {
                    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                    if (isAdmin(authentication) || isOwnUser(userJson, userDetails)) {
                        return action.apply(userJson, userDetails);
                    } else {
                        return UserServiceException.mono("No tienes permiso para acceder a esta información.");
                    }
                });
    }

    private static Mono<Authentication> getAuthentication() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication);
    }

    private static boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }

    private static boolean isOwnUser(UserJson userJson, UserDetails userDetails) {
        String username = userDetails.getUsername();
        return (userJson.get_id() == null)
                ? username.contains(userJson.getEmail())
                : username.contains(userJson.get_id());
    }
}
