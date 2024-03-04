package com.whatacook.cookers.controler;

import com.whatacook.cookers.model.auth.AuthRequestDto;
import com.whatacook.cookers.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@CrossOrigin
public class JwtAuthenticationController {

    private final AuthService auth;

    @PostMapping(value = "${security.jwt.login-url}")
    public ResponseEntity<?> createAuthenticationTokenByLogin(@RequestBody AuthRequestDto AuthRequestDto) {
        return auth.authenticationByLogin(AuthRequestDto);
    }

}
