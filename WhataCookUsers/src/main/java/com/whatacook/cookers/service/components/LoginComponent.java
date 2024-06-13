package com.whatacook.cookers.service.components;

import com.whatacook.cookers.config.jwt.CustomUserDetails;
import com.whatacook.cookers.model.constants.AccountStatus;
import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.users.UserDTO;
import com.whatacook.cookers.service.contracts.UserDao;
import com.whatacook.cookers.utilities.Util;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.whatacook.cookers.model.constants.AccountStatus.*;

@AllArgsConstructor
@Component
@Validated
public class LoginComponent {

    private final UserDao DAO;
    private final DeleteComponent deleteComponent;

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
        final AccountStatus accountStatus = userDTO.getAccountStatus();
        if (EnumSet.of(OK, OFF, REQUEST_DELETE).contains(accountStatus))
            return Mono.just(userDTO);
        else if (MARKED_DELETE.equals(accountStatus))
            return deleteComponent.proceedIfApplicable(userDTO.toJson())
                    .flatMap(response -> UserServiceException.mono(response.getMessage()));
        else
            return UserServiceException.mono(accountStatus.getDetails());
    }

    private UserDetails newValidUserByEmail(UserDTO userDTO) {
        Set<GrantedAuthority> authorities = listAuthorities(userDTO);
        authorities.add(new SimpleGrantedAuthority("ROLE_SELF"));
        return new CustomUserDetails(userDTO.getEmail() + userDTO.get_id(), userDTO.getPassword(), authorities, userDTO.getEmail(), userDTO.get_id());
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
        String errorMsg = "Account Status Incorrect for this request: " + userDTO.getAccountStatus().getDetails();
        if (EnumSet.of(AccountStatus.OK, AccountStatus.PENDING, AccountStatus.OUTDATED)
                .contains(userDTO.getAccountStatus())) {
            return Mono.just(userDTO);
        } else {
            return UserServiceException.mono(errorMsg);
        }
    }

    private UserDetails newValidUserById(UserDTO userDTO) {
        Set<GrantedAuthority> authorities = listAuthorities(userDTO);
        authorities.add(new SimpleGrantedAuthority("ROLE_SELF"));
        return new CustomUserDetails(userDTO.get_id(), userDTO.getPassword(), authorities, userDTO.getEmail(), userDTO.get_id());
    }

}
