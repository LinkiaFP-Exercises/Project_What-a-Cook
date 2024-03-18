package com.whatacook.cookers.service.components;

import com.whatacook.cookers.model.constants.AccountStatus;
import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.users.UserDTO;
import com.whatacook.cookers.utilities.Util;
import com.whatacook.cookers.service.contracts.UserDao;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
@Validated
public class LoginComponent {

    private final UserDao DAO;

    public Mono<UserDetails> validSpringUserToLogin(String userEmailOrId) {
        return Mono.just(userEmailOrId)
                .flatMap(info -> {
                    if (Util.isValidEmail(info))
                        return findUserByEmail(info);
                    else
                        return findUserById(info);
                });
    }
    private Mono<UserDetails> findUserByEmail(String email) {
        return DAO.findByEmail(email)
                .switchIfEmpty(UserServiceException.mono("USER NOT FOUND!"))
                .flatMap(this::verifyAccountStatusByEmail)
                .map(this::newValidUserByEmail);
    }

    private Mono<UserDTO> verifyAccountStatusByEmail(UserDTO userDTO) {
        if (EnumSet.of(AccountStatus.OK, AccountStatus.OFF)
                .contains(userDTO.getAccountStatus()))  {
            return Mono.just(userDTO);
        } else {
            return UserServiceException.mono(userDTO.getAccountStatus().getDetails());
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
                .switchIfEmpty(UserServiceException.mono("USER NOT FOUND!"))
                .flatMap(this::verifyAccountStatusById)
                .map(this::newValidUserById);
    }

    private Mono<UserDTO> verifyAccountStatusById(UserDTO userDTO) {
        if (EnumSet.of(AccountStatus.PENDING, AccountStatus.OUTDATED)
                .contains(userDTO.getAccountStatus())) {
            return Mono.just(userDTO);
        } else {
            return UserServiceException.mono(userDTO.getAccountStatus().getDetails());
        }
    }

    private UserDetails newValidUserById(UserDTO userDTO) {
        Set<GrantedAuthority> authorities = listAuthorities(userDTO);
        return new User(userDTO.get_id(), userDTO.getPassword(), authorities);
    }

}
