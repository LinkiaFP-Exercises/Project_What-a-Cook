package com.whatacook.cookers.view;

import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.users.UserDTO;
import jakarta.validation.Valid;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Validated
public class ServiceComponentToLogin {

    private final UserDAO DAO;

    public ServiceComponentToLogin(UserDAO DAO) {
        this.DAO = DAO;
    }

    UserDetails validSpringUserToLogin(@Valid UserDTO userDTO) {
        return findUserByEmail(userDTO.getEmail())
                    .map(this::newValidUserBy).block();
    }

    private Mono<UserDTO> findUserByEmail(String email) {
        return DAO.findByEmail(email)
                .switchIfEmpty(Mono.error(new UserServiceException("USER NOT FOUND!")));
    }

    private UserDetails newValidUserBy(UserDTO userDTO) {
        Set<GrantedAuthority> authorities = listAuthorities(userDTO);
        return new User(userDTO.getEmail(), userDTO.getPassword(), authorities);
    }

    private Set<GrantedAuthority> listAuthorities(UserDTO userDTO) {
        return Arrays.stream(userDTO.getRoleType().get().split(","))
                .map(String::trim)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

}
