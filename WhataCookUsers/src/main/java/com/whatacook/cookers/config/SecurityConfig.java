package com.whatacook.cookers.config;

import com.whatacook.cookers.config.jwt.JwtRequestFilter;
import com.whatacook.cookers.config.jwt.JwtUtil;
import com.whatacook.cookers.utilities.GlobalValues;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@AllArgsConstructor
@Configuration
@EnableWebFluxSecurity
@EnableConfigurationProperties({JwtUtil.class, GlobalValues.class})
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final GlobalValues globalValues;


    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity httpSecurity, JwtRequestFilter jwtRequestFilter,
                                              ReactiveAuthenticationManager reactiveAuthenticationManager) {

        return httpSecurity.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authenticationManager(reactiveAuthenticationManager)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(authorizeExchangeSpec ->
                        authorizeExchangeSpec
                                .pathMatchers(globalValues.getPathToCheckIfEmailAlreadyExists()).permitAll()
                                .pathMatchers(jwtUtil.getLoginUrl()).permitAll()
                                .pathMatchers(jwtUtil.getSignInUrl()).permitAll()
                                .anyExchange().authenticated()
                ).addFilterAt(jwtRequestFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService,
                                                                       PasswordEncoder passwordEncoder) {
        var authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder);
        return authenticationManager;
    }

}
