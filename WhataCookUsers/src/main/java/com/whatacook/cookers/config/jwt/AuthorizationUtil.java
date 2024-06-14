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

/**
 * Utility class for handling authorization checks.
 * Provides methods to execute actions if the user is authorized.
 *
 * Methods:
 * - executeIfAuthorized(UserJson userJson, BiFunction<UserJson, UserDetails, Mono<Response>> action):
 *     Executes the given action if the user is authorized.
 * - getAuthentication(): Retrieves the current authentication object.
 * - isAuthorized(UserJson userJson, Authentication authentication): Checks if the user is authorized.
 * - isAdmin(Authentication authentication): Checks if the authenticated user has admin privileges.
 * - isOwnUser(UserJson userJson, UserDetails userDetails): Checks if the authenticated user is the owner.
 * - getUserDetails(Authentication authentication): Retrieves the UserDetails from the authentication object.
 *
 * @author
 * <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
public class AuthorizationUtil {

    public static Mono<Response> executeIfAuthorized(UserJson userJson,
                                                     BiFunction<UserJson, UserDetails, Mono<Response>> action) {
        return getAuthentication()
                .flatMap(authentication -> isAuthorized(userJson, authentication)
                        .flatMap(isAuthorized -> isAuthorized
                                ? action.apply(userJson, (UserDetails) authentication.getPrincipal())
                                : UserServiceException.mono("No tienes permiso para acceder a esta información.")));
    }

    private static Mono<Authentication> getAuthentication() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication);
    }

    private static Mono<Boolean> isAuthorized(UserJson userJson, Authentication authentication) {
        return Mono.just(isAdmin(authentication) || isOwnUser(userJson, getUserDetails(authentication)));
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

    private static UserDetails getUserDetails(Authentication authentication) {
        return (UserDetails) authentication.getPrincipal();
    }
}
