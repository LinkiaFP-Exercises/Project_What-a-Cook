package com.whatacook.cookers.controler;

import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.responses.Response;
import com.whatacook.cookers.model.users.UserJson;
import com.whatacook.cookers.service.UserService;
import com.whatacook.cookers.utilities.ValidEmail;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

@AllArgsConstructor
@RestController
@Validated
public class UserController {

    private final UserService service;

    @PostMapping("${app.endpoint.users-check-email}")
    public Mono<Response> existsByEmail(@Valid @RequestBody UserJson userJson) {
        return service.existsByEmail(userJson);
    }

    @PostMapping("${app.endpoint.find-by-email}")
    @PreAuthorize("hasRole('USER')")
    public Mono<Response> readOne(@Valid @RequestBody UserJson userJson) {
        return executeIfAuthorized(userJson, (json, userDetails) -> service.readOne(json));
    }

    @PutMapping("${app.endpoint.users}")
    @PreAuthorize("hasRole('USER')")
    public Mono<Response> update(@RequestBody UserJson userJson) {
        return executeIfAuthorized(userJson, (json, userDetails) -> service.updateOne(json));
    }

    @DeleteMapping("${app.endpoint.users}")
    @PreAuthorize("hasRole('USER')")
    public Mono<Response> deleteOne(@RequestBody UserJson userJson) {
        return executeIfAuthorized(userJson, (json, userDetails) -> service.deleteOne(json));
    }

    private Mono<Response> executeIfAuthorized(UserJson userJson,
                                                   BiFunction<UserJson, UserDetails, Mono<Response>> action) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                    boolean isAdmin = authentication.getAuthorities().stream()
                            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

                    String username = userDetails.getUsername();
                    boolean isOwnUser = (userJson.get_id() == null)
                                                    ? username.contains(userJson.getEmail())
                                                    :username.contains(userJson.get_id());

                    return (isAdmin || isOwnUser)
                            ? action.apply(userJson, userDetails)
                            : UserServiceException.mono("No tienes permiso para acceder a esta información.");
                });
    }

    @GetMapping("${app.endpoint.users-activate}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<String>> activate(@RequestParam("activationCode") String activationCode) {
        return service.activateAccount(activationCode);
    }

    @GetMapping("${app.endpoint.users-resend}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Response> resendActivation(@ValidEmail @RequestParam("emailToResend") String emailToResend) {
        return service.resendActivateCode(emailToResend);
    }

    @GetMapping("${app.endpoint.reset-pass}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<String>> resetPassword(@RequestParam("resetCode") String resetCode) {
        return service.resetPasswordByCode(resetCode);
    }

    @PostMapping("${app.endpoint.set-new-pass}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<String>> setNewPassword(@RequestBody UserJson userJson) {
        return service.setNewPasswordByCode(userJson);
    }

}

