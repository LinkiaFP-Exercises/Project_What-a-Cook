package com.whatacook.cookers.controler;

import com.whatacook.cookers.model.responses.Response;
import com.whatacook.cookers.model.users.UserJson;
import com.whatacook.cookers.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@Validated
public class UserController {

    private final UserService service;

    @GetMapping("${app.endpoint.users-activate}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<String>> activate(@RequestParam("activationCode") String activationCode) {
        return service.activateAccount(activationCode);
    }

    @GetMapping("${app.endpoint.users-resend}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Response> resendActivation(@RequestParam("emailToResend") String emailToResend) {
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

    @PostMapping("${app.endpoint.users-check-email}")
    public Mono<Response> existsByEmail(@Valid @RequestBody UserJson userJson) { return service.existsByEmail(userJson); }

    @GetMapping("${app.endpoint.find-by-email}")
    @PreAuthorize("hasRole('USER')")
    public Mono<Response> readOne(@Valid @RequestBody UserJson userJson) { return service.readOne(userJson); }

    @PutMapping("${app.endpoint.users}")
    @PreAuthorize("hasRole('USER')")
    public Mono<Response> update(@RequestBody UserJson userJson) { return service.updateOne(userJson); }

    @DeleteMapping("${app.endpoint.users}")
    @PreAuthorize("hasRole('USER')")
    public Mono<Response> deleteOne(@RequestBody UserJson userJson) { return service.deleteOne(userJson); }

}

