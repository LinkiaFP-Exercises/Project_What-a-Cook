package com.whatacook.cookers.config;

import com.whatacook.cookers.config.filter.AnyRequestFilter;
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

/**
 * Security configuration class for setting up Spring Security with WebFlux.
 * Configures security filters, authentication manager, and password encoding.
 * <p>
 * Annotations:
 * - @AllArgsConstructor: Generates an all-arguments constructor.
 * - @Configuration: Indicates that this class contains Spring configuration.
 * - @EnableWebFluxSecurity: Enables WebFlux security for the application.
 * - @EnableConfigurationProperties: Enables configuration properties for specified classes.
 * <p>
 * Fields:
 * - jwtUtil: Utility class for handling JWT operations.
 * - globalValues: Class containing global values and configurations.
 * <p>
 * Methods:
 * - filterChain(ServerHttpSecurity httpSecurity, AnyRequestFilter anyRequestFilter, ReactiveAuthenticationManager reactiveAuthenticationManager):
 * Configures the security filter chain.
 * - passwordEncoder(): Provides a PasswordEncoder bean for encoding passwords.
 * - reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService, PasswordEncoder passwordEncoder):
 * Configures the ReactiveAuthenticationManager.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@AllArgsConstructor
@Configuration
@EnableWebFluxSecurity
@EnableConfigurationProperties({JwtUtil.class, GlobalValues.class})
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final GlobalValues globalValues;

    /**
     * Configures the security filter chain.
     *
     * @param httpSecurity                  The ServerHttpSecurity instance.
     * @param anyRequestFilter              The custom request filter.
     * @param reactiveAuthenticationManager The reactive authentication manager.
     * @return The configured SecurityWebFilterChain.
     */
    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity httpSecurity, AnyRequestFilter anyRequestFilter,
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
                                .pathMatchers(jwtUtil.getForgotPass()).permitAll()
                                .anyExchange().authenticated()
                ).addFilterAt(anyRequestFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    /**
     * Provides a PasswordEncoder bean for encoding passwords.
     *
     * @return The BCryptPasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the ReactiveAuthenticationManager.
     *
     * @param userDetailsService The ReactiveUserDetailsService instance.
     * @param passwordEncoder    The PasswordEncoder instance.
     * @return The configured ReactiveAuthenticationManager.
     */
    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService,
                                                                       PasswordEncoder passwordEncoder) {
        var authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder);
        return authenticationManager;
    }
}
