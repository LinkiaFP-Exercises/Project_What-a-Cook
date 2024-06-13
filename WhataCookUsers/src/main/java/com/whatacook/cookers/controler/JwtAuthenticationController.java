package com.whatacook.cookers.controler;

import com.whatacook.cookers.model.auth.AuthRequestDto;
import com.whatacook.cookers.model.responses.Response;
import com.whatacook.cookers.model.users.UserJson;
import com.whatacook.cookers.model.users.UserJustToSave;
import com.whatacook.cookers.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping("${app.endpoint.auth-root}")
@CrossOrigin
public class JwtAuthenticationController {

    private final AuthService auth;

    @PostMapping("${app.endpoint.sign-in-url}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Response> register(@Valid @RequestBody UserJustToSave userJson) { return auth.registerNewUser(userJson); }

    @PostMapping(value = "${app.endpoint.login-url}")
    public Mono<ResponseEntity<Response>> createAuthenticationTokenByLogin(@RequestBody AuthRequestDto AuthRequestDto) {
        return auth.authenticationByLogin(AuthRequestDto);
    }

    @PostMapping("${app.endpoint.forgot-pass}")
    public Mono<ResponseEntity<Response>> forgotPassword(@RequestBody UserJson userJson) {
        return auth.sendEmailCodeToResetPassword(userJson);
    }

}
