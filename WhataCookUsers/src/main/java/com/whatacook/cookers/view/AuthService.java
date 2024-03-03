package com.whatacook.cookers.view;

import com.whatacook.cookers.config.jwt.JwtUtil;
import com.whatacook.cookers.model.auth.AuthRequestDto;
import com.whatacook.cookers.model.responses.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.whatacook.cookers.model.responses.Response.error;
import static com.whatacook.cookers.utilities.Util.msgError;

@Service
@Validated
public class AuthService {

    private final ReactiveAuthenticationManager reactiveAuthenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(ReactiveAuthenticationManager reactiveAuthenticationManager, JwtUtil jwtUtil) {
        this.reactiveAuthenticationManager = reactiveAuthenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<?> authenticationByLogin(AuthRequestDto AuthRequestDto) {

        Response response = error(msgError("Authenticate a User"));
        ResponseEntity<?> responseEntity = ResponseEntity.ofNullable(response);

        try {
            authenticate(AuthRequestDto);
            responseEntity = ResponseEntity.ok(
                    Response.success("TOKEN", jwtUtil.generateToken(AuthRequestDto)));
        } catch (Exception e) {
            response.addMessage(e.getMessage());
        }

        return responseEntity;
    }

    private void authenticate(AuthRequestDto AuthRequestDto) throws DisabledException, BadCredentialsException {
        String username = AuthRequestDto.getUsername();
        String password = AuthRequestDto.getPassword();
        UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(username, password);
        reactiveAuthenticationManager.authenticate(user);
    }

}
