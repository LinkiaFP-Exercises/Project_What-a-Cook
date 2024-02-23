package com.whatacook.cookers.controler;

import com.whatacook.cookers.config.jwt.JwtTokenUtil;
import com.whatacook.cookers.model.auth.AuthenticateResponseDto;
import com.whatacook.cookers.model.auth.AuthenticationRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtil jwtTokenUtil;

    public JwtAuthenticationController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping(value = "${security.jwt.login-url}")
    public ResponseEntity<?> createAuthenticationTokenByLogin(@RequestBody AuthenticationRequestDto authenticationRequest)
            throws Exception {

        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        final String TOKEN = jwtTokenUtil.generateToken(authenticationRequest.getUsername());

        return ResponseEntity.ok(new AuthenticateResponseDto(TOKEN));
    }

    @PostMapping(value = "${security.jwt.sign-in-url}")
    public ResponseEntity<?> createAuthenticationTokenBySigIn(@RequestBody AuthenticationRequestDto authenticationRequest)
            throws Exception {

        return null;
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

}
