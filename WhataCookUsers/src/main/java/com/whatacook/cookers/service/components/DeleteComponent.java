package com.whatacook.cookers.service.components;

import com.whatacook.cookers.model.constants.AccountStatus;
import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.responses.Response;
import com.whatacook.cookers.model.users.UserDto;
import com.whatacook.cookers.model.users.UserJson;
import com.whatacook.cookers.service.contracts.UserDao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Component for handling user account deletion processes.
 * <p>
 * Annotations:
 * - @AllArgsConstructor: Generates a constructor with 1 parameter for each field.
 * - @Component: Indicates that this class is a Spring component.
 * <p>
 * Fields:
 * - DAO: Data Access Object for UserDto.
 * <p>
 * Methods:
 * - proceedIfApplicable(UserJson userJson): Checks and proceeds with the deletion if applicable.
 * - handleStatusChange(UserDto userDTO): Handles the account status change during deletion.
 * - handleOkStatus(UserDto userDTO): Handles the OK status during deletion.
 * - handleRequestDeleteStatus(UserDto userDTO): Handles the REQUEST_DELETE status during deletion.
 * - handleMarkedDeleteStatus(UserDto userDTO): Handles the MARKED_DELETE status during deletion.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see UserDao
 * @see UserDto
 * @see UserJson
 * @see Response
 * @see Mono
 * @see AllArgsConstructor
 * @see Component
 */
@AllArgsConstructor
@Component
public class DeleteComponent {

    private final UserDao DAO;

    /**
     * Checks and proceeds with the deletion if applicable.
     *
     * @param userJson the user details
     * @return a response indicating the result of the deletion process
     */
    public Mono<Response> proceedIfApplicable(UserJson userJson) {
        return DAO.findBy_id(userJson.get_id())
                .switchIfEmpty(UserServiceException.mono("User not found"))
                .flatMap(this::handleStatusChange);
    }

    /**
     * Handles the account status change during deletion.
     *
     * @param userDTO the user details
     * @return a response indicating the result of the status change
     */
    private Mono<Response> handleStatusChange(UserDto userDTO) {
        return switch (userDTO.getAccountStatus()) {
            case OK -> handleOkStatus(userDTO);
            case REQUEST_DELETE -> handleRequestDeleteStatus(userDTO);
            case MARKED_DELETE -> handleMarkedDeleteStatus(userDTO);
            default -> UserServiceException.mono("Invalid account status to request deletion");
        };
    }

    /**
     * Handles the OK status during deletion.
     *
     * @param userDTO the user details
     * @return a response indicating the result of setting the REQUEST_DELETE status
     */
    private Mono<Response> handleOkStatus(UserDto userDTO) {
        userDTO.setAccountStatus(AccountStatus.REQUEST_DELETE);
        userDTO.setRequestDeleteDate(LocalDateTime.now());
        return DAO.save(userDTO)
                .map(savedUserDTO -> Response.success("REQUEST_DELETE set, you have one year to revoke the deletion", savedUserDTO.toJson()))
                .onErrorResume(UserServiceException::mono);
    }

    /**
     * Handles the REQUEST_DELETE status during deletion.
     *
     * @param userDTO the user details
     * @return a response indicating the result of handling the REQUEST_DELETE status
     */
    private Mono<Response> handleRequestDeleteStatus(UserDto userDTO) {
        LocalDateTime requestDeleteDate = userDTO.getRequestDeleteDate();
        if (requestDeleteDate != null && ChronoUnit.YEARS.between(requestDeleteDate, LocalDateTime.now()) >= 1) {
            userDTO.setAccountStatus(AccountStatus.MARKED_DELETE);
            return DAO.save(userDTO)
                    .map(savedUserDTO -> Response.success("MARKED_DELETE set, your account has been invalidated you have one year to request your data", savedUserDTO.toJson()))
                    .onErrorResume(UserServiceException::mono);
        }
        return Mono.just(Response.success("REQUEST_DELETE request is not yet a year old", userDTO.toJson()));
    }

    /**
     * Handles the MARKED_DELETE status during deletion.
     *
     * @param userDTO the user details
     * @return a response indicating the result of handling the MARKED_DELETE status
     */
    private Mono<Response> handleMarkedDeleteStatus(UserDto userDTO) {
        LocalDateTime requestDeleteDate = userDTO.getRequestDeleteDate();
        if (requestDeleteDate != null && ChronoUnit.YEARS.between(requestDeleteDate, LocalDateTime.now()) >= 2) {
            return DAO.delete(userDTO)
                    .thenReturn(Response.success("Your account has been terminated", true))
                    .onErrorResume(UserServiceException::mono);
        }
        String message = "Your account is set to be deleted, but you still have time to request your data";
        return Mono.just(Response.success(message, userDTO.toJson()));
    }

}
