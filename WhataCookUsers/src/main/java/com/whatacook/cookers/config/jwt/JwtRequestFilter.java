package com.whatacook.cookers.config.jwt;

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
import java.text.MessageFormat;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public JwtRequestFilter(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String requestToken = catchTokenFromHeaderOrParameter(request);

        String userEmailOrId = null;
        String jwtToken = null;

        if (jwtUtil.hasToken(requestToken)) {
            try {
                if (jwtUtil.isValidToken(requestToken)) {
                    jwtToken = jwtUtil.extractPrefix(requestToken);
                    userEmailOrId = jwtUtil.getUsernameFromToken(jwtToken);
                }
            }
            catch (IllegalArgumentException e) { logger.error("Unable to get JWT Token"); }
            catch (ExpiredJwtException e) { logger.error("JWT Token has expired"); }
            catch (Exception e) { logger.error(e.getMessage()); }
        }

        if (userEmailOrId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userService.loadUserByUsername(userEmailOrId);

            if (jwtUtil.verifyUserFromToken(jwtToken, userDetails)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        filterChain.doFilter(request, response);

    }

    private String catchTokenFromHeaderOrParameter(HttpServletRequest request){
        return (request.getHeader(jwtUtil.getHeader()) == null)
                ? String.format("Bearer %s", request.getParameter("token"))
                : request.getHeader(jwtUtil.getHeader());
    }

}
