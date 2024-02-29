package com.whatacook.cookers.view;

import com.whatacook.cookers.config.jwt.JwtTokenUtil;
import com.whatacook.cookers.model.auth.AuthRequestDto;
import com.whatacook.cookers.model.responses.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.whatacook.cookers.model.responses.Response.error;
import static com.whatacook.cookers.utilities.Util.msgError;

@Service
@Validated
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public ResponseEntity<?> authenticationByLogin(AuthRequestDto AuthRequestDto) {

        Response response = error(msgError("Authenticate a User"));
        ResponseEntity<?> responseEntity = ResponseEntity.ofNullable(response);

        try {
            authenticate(AuthRequestDto);
            responseEntity = ResponseEntity.ok(
                    Response.success("TOKEN", jwtTokenUtil.generateToken(AuthRequestDto)));
        } catch (Exception e) {
            response.addMessage(e.getMessage());
        }

        return responseEntity;
    }

    private void authenticate(AuthRequestDto AuthRequestDto) throws DisabledException, BadCredentialsException {
        String username = AuthRequestDto.getUsername();
        String password = AuthRequestDto.getPassword();
        UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(username, password);
        authenticationManager.authenticate(user);
    }

}
