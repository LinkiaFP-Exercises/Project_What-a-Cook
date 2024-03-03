package com.whatacook.cookers.config.jwt;

import com.whatacook.cookers.model.auth.ActivationDto;
import com.whatacook.cookers.model.users.UserDTO;
import com.whatacook.cookers.utilities.Util;
import com.whatacook.cookers.view.ActivationService;
import com.whatacook.cookers.view.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final ActivationService activationService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public JwtRequestFilter(ActivationService activationService, UserService userService, JwtUtil jwtUtil) {
        this.activationService = activationService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

        @Override
        protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                        @NonNull FilterChain filterChain)
                throws ServletException, IOException {

            final String activationCode = request.getParameter("activationCode");
            final String emailToResend = request.getParameter("emailToResend");
            if (Util.notNullOrEmpty(activationCode)) {
                processActivationCode(activationCode, request);
            }
            else if (Util.notNullOrEmpty(emailToResend)) {
                processResendActvationMail(emailToResend, request);
            }
            else {
                final String requestToken = request.getHeader(jwtUtil.getHeader());
                if (jwtUtil.hasToken(requestToken)) {
                    processJwtToken(requestToken, request);
                }
            }

            filterChain.doFilter(request, response);
        }

    private void processResendActvationMail(String emailToResend, HttpServletRequest request) {
        UserDTO userDTO = userService.findDtoByMail(emailToResend).block();
        ActivationDto activationDto = activationService.findById(userDTO.get_id()).block();
        UserDetails userDetails = userService.loadUserByUsername(activationDto.getId());
        setSecurityContext(userDetails, request);
    }

    private void processActivationCode(String activationCode, HttpServletRequest request) {
            ActivationDto activationDto = activationService.findByCode(activationCode).block();
            UserDetails userDetails = userService.loadUserByUsername(activationDto.getId());
            setSecurityContext(userDetails, request);

        }

        private void processJwtToken(String requestToken, HttpServletRequest request) {
            try {
                if (jwtUtil.isValidToken(requestToken)) {
                    String jwtToken = jwtUtil.extractPrefix(requestToken);
                    String userEmailOrId = jwtUtil.getUsernameFromToken(jwtToken);
                    UserDetails userDetails = userService.loadUserByUsername(userEmailOrId);
                    if (jwtUtil.verifyUserFromToken(jwtToken, userDetails)) {
                        setSecurityContext(userDetails, request);
                    }
                }
            } catch (IllegalArgumentException e) {
                logger.error("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                logger.error("JWT Token has expired");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

        private void setSecurityContext(UserDetails userDetails, HttpServletRequest request) {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

}
