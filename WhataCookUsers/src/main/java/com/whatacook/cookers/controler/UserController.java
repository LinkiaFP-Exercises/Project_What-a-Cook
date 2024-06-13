package com.whatacook.cookers.controler;

import com.whatacook.cookers.config.jwt.AuthorizationUtil;
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
@RequestMapping("${app.endpoint.users}")
@Validated
public class UserController {

    private final UserService service;

    @PostMapping("${app.endpoint.users-check-email}")
    public Mono<Response> existsByEmail(@Valid @RequestBody UserJson userJson) {
        return service.existsByEmail(userJson);
    }

    @PostMapping("${app.endpoint.find-by-email}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Mono<Response> readOne(@Valid @RequestBody UserJson userJson) {
        return AuthorizationUtil.executeIfAuthorized(userJson, (json, userDetails) -> service.readOne(json));
    }

    @PutMapping()
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Mono<Response> update(@RequestBody UserJson userJson) {
        return AuthorizationUtil.executeIfAuthorized(userJson, (json, userDetails) -> service.updateOne(json));
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Mono<Response> deleteOne(@RequestParam("id") String id) {
        return AuthorizationUtil.executeIfAuthorized(new UserJson(id), (json, userDetails) -> service.deleteOne(json));
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

