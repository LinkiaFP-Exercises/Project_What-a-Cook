package com.whatacook.cookers.service.components;

import com.whatacook.cookers.config.jwt.CustomUserDetails;
import com.whatacook.cookers.model.constants.AccountStatus;
import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.users.UserDto;
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

/**
 * Component for handling user login operations.
 * <p>
 * Annotations:
 * - @AllArgsConstructor: Generates a constructor with 1 parameter for each field.
 * - @Component: Indicates that this class is a Spring component.
 * - @Validated: Indicates that this class is eligible for Spring's method-level validation.
 * <p>
 * Fields:
 * - DAO: Data Access Object for UserDto.
 * - deleteComponent: Component for handling account deletions.
 * <p>
 * Methods:
 * - validSpringUserToLogin(String userEmailOrId): Validates and retrieves a Spring Security user.
 * - findUserByEmail(String email): Finds a user by email.
 * - verifyAccountStatusByEmail(UserDto userDTO): Verifies the account status by email.
 * - newValidUserByEmail(UserDto userDTO): Creates a valid UserDetails object by email.
 * - listAuthorities(UserDto userDTO): Lists the authorities for a user.
 * - findUserById(String id): Finds a user by ID.
 * - verifyAccountStatusById(UserDto userDTO): Verifies the account status by ID.
 * - newValidUserById(UserDto userDTO): Creates a valid UserDetails object by ID.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see UserDao
 * @see UserDto
 * @see UserDetails
 * @see CustomUserDetails
 * @see DeleteComponent
 * @see Util
 * @see Mono
 * @see AllArgsConstructor
 * @see Component
 * @see Validated
 * @see AccountStatus
 */
@AllArgsConstructor
@Component
@Validated
public class LoginComponent {

    private final UserDao DAO;
    private final DeleteComponent deleteComponent;

    /**
     * Validates and retrieves a Spring Security user.
     *
     * @param userEmailOrId the user email or ID
     * @return a Mono of UserDetails
     */
    public Mono<UserDetails> validSpringUserToLogin(String userEmailOrId) {
        return Mono.just(userEmailOrId)
                .flatMap(info -> {
                    if (Util.isValidEmail(info))
                        return findUserByEmail(info);
                    else
                        return findUserById(info);
                });
    }

    /**
     * Finds a user by email.
     *
     * @param email the user email
     * @return a Mono of UserDetails
     */
    private Mono<UserDetails> findUserByEmail(String email) {
        return DAO.findByEmail(email)
                .switchIfEmpty(UserServiceException.mono("USER NOT FOUND!"))
                .flatMap(this::verifyAccountStatusByEmail)
                .map(this::newValidUserByEmail);
    }

    /**
     * Verifies the account status by email.
     *
     * @param userDTO the user details
     * @return a Mono of UserDto
     */
    private Mono<UserDto> verifyAccountStatusByEmail(UserDto userDTO) {
        final AccountStatus accountStatus = userDTO.getAccountStatus();
        if (EnumSet.of(OK, OFF, REQUEST_DELETE).contains(accountStatus))
            return Mono.just(userDTO);
        else if (MARKED_DELETE.equals(accountStatus))
            return deleteComponent.proceedIfApplicable(userDTO.toJson())
                    .flatMap(response -> UserServiceException.mono(response.getMessage()));
        else
            return UserServiceException.mono(accountStatus.getDetails());
    }

    /**
     * Creates a valid UserDetails object by email.
     *
     * @param userDTO the user details
     * @return the UserDetails object
     */
    private UserDetails newValidUserByEmail(UserDto userDTO) {
        Set<GrantedAuthority> authorities = listAuthorities(userDTO);
        authorities.add(new SimpleGrantedAuthority("ROLE_SELF"));
        return new CustomUserDetails(userDTO.getEmail() + userDTO.get_id(), userDTO.getPassword(), authorities, userDTO.getEmail(), userDTO.get_id());
    }

    /**
     * Lists the authorities for a user.
     *
     * @param userDTO the user details
     * @return a set of GrantedAuthority
     */
    private Set<GrantedAuthority> listAuthorities(UserDto userDTO) {
        return Arrays.stream(userDTO.getRoleType().get().split(","))
                .map(String::trim)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    /**
     * Finds a user by ID.
     *
     * @param id the user ID
     * @return a Mono of UserDetails
     */
    private Mono<UserDetails> findUserById(String id) {
        return DAO.findBy_id(id)
                .switchIfEmpty(UserServiceException.mono("USER NOT FOUND!"))
                .flatMap(this::verifyAccountStatusById)
                .map(this::newValidUserById);
    }

    /**
     * Verifies the account status by ID.
     *
     * @param userDTO the user details
     * @return a Mono of UserDto
     */
    private Mono<UserDto> verifyAccountStatusById(UserDto userDTO) {
        String errorMsg = "Account Status Incorrect for this request: " + userDTO.getAccountStatus().getDetails();
        if (EnumSet.of(AccountStatus.OK, AccountStatus.PENDING, AccountStatus.OUTDATED)
                .contains(userDTO.getAccountStatus())) {
            return Mono.just(userDTO);
        } else {
            return UserServiceException.mono(errorMsg);
        }
    }

    /**
     * Creates a valid UserDetails object by ID.
     *
     * @param userDTO the user details
     * @return the UserDetails object
     */
    private UserDetails newValidUserById(UserDto userDTO) {
        Set<GrantedAuthority> authorities = listAuthorities(userDTO);
        authorities.add(new SimpleGrantedAuthority("ROLE_SELF"));
        return new CustomUserDetails(userDTO.get_id(), userDTO.getPassword(), authorities, userDTO.getEmail(), userDTO.get_id());
    }

}
