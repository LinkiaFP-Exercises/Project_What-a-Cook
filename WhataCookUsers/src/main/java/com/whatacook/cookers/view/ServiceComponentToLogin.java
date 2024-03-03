package com.whatacook.cookers.view;

import com.whatacook.cookers.model.constants.AccountStatus;
import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.users.UserDTO;
import com.whatacook.cookers.utilities.Util;
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

    Mono<UserDetails> validSpringUserToLogin(String userEmailOrId) {
        return Mono.just(userEmailOrId)
                .flatMap(info -> {
                    if (Util.isValidEmail(info))
                        return findUserByEmail(info);
                    else {
                        return findUserById(info);
                    }
                });
    }
    private Mono<UserDetails> findUserByEmail(String email) {
        return DAO.findByEmail(email)
                .switchIfEmpty(Mono.error(new UserServiceException("USER NOT FOUND!")))
                .flatMap(this::verifyAccountStatusByEmail)
                .map(this::newValidUserByEmail);
    }

    private Mono<UserDTO> verifyAccountStatusByEmail(UserDTO userDTO) {
        if (userDTO.getAccountStatus().equals(AccountStatus.OK)) {
            return Mono.just(userDTO);
        } else {
            return Mono.error(new UserServiceException(userDTO.getAccountStatus().getDetails()));
        }
    }

    private UserDetails newValidUserByEmail(UserDTO userDTO) {
        Set<GrantedAuthority> authorities = listAuthorities(userDTO);
        return new User(userDTO.getEmail(), userDTO.getPassword(), authorities);
    }

    private Set<GrantedAuthority> listAuthorities(UserDTO userDTO) {
        return Arrays.stream(userDTO.getRoleType().get().split(","))
                .map(String::trim)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    private Mono<UserDetails> findUserById(String id) {
        return DAO.findBy_id(id)
                .switchIfEmpty(Mono.error(new UserServiceException("USER NOT FOUND!")))
                .flatMap(this::verifyAccountStatusById)
                .map(this::newValidUserById)
                ;
    }

    private Mono<UserDTO> verifyAccountStatusById(UserDTO userDTO) {
        if (userDTO.getAccountStatus().equals(AccountStatus.PENDING)) {
            return Mono.just(userDTO);
        } else {
            return Mono.error(new UserServiceException(userDTO.getAccountStatus().getDetails()));
        }
    }

    private UserDetails newValidUserById(UserDTO userDTO) {
        Set<GrantedAuthority> authorities = listAuthorities(userDTO);
        return new User(userDTO.get_id(), userDTO.getPassword(), authorities);
    }

}
