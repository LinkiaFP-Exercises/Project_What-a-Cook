package com.whatacook.cookers.controler;

import com.whatacook.cookers.model.auth.AuthRequestDto;
import com.whatacook.cookers.model.responses.Response;
import com.whatacook.cookers.model.users.UserJson;
import com.whatacook.cookers.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@AllArgsConstructor
@RestController
@CrossOrigin
public class JwtAuthenticationController {

    private final AuthService auth;

    @PostMapping(value = "${security.jwt.login-url}")
    public Mono<ResponseEntity<Response>> createAuthenticationTokenByLogin(@RequestBody AuthRequestDto AuthRequestDto) {
        return auth.authenticationByLogin(AuthRequestDto);
    }

    @PostMapping("${security.jwt.forgot-pass}")
    public Mono<ResponseEntity<Response>> forgotPassword(@Valid @RequestBody UserJson userJson) {

        return Mono.empty();
    }

    @PostMapping("")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        // Verificar token y restablecer contraseña
        return ResponseEntity.ok().body("Contraseña actualizada con éxito.");
    }
}
