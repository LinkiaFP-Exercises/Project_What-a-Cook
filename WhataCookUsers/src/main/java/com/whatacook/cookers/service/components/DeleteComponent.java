package com.whatacook.cookers.service.components;

import com.whatacook.cookers.model.constants.AccountStatus;
import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.responses.Response;
import com.whatacook.cookers.model.users.UserDTO;
import com.whatacook.cookers.model.users.UserJson;
import com.whatacook.cookers.service.contracts.UserDao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@AllArgsConstructor
@Component
public class DeleteComponent {

    private final UserDao DAO;

    public Mono<Response> proceedIfApplicable(UserJson userJson) {
        return DAO.findBy_id(userJson.get_id())
                .switchIfEmpty(UserServiceException.mono("User not found"))
                .flatMap(this::handleStatusChange);
    }

    private Mono<Response> handleStatusChange(UserDTO userDTO) {
        return switch (userDTO.getAccountStatus()) {
            case OK -> handleOkStatus(userDTO);
            case REQUEST_DELETE -> handleRequestDeleteStatus(userDTO);
            case MARKED_DELETE -> handleMarkedDeleteStatus(userDTO);
            case DELETE -> handleDeleteStatus(userDTO);
            default -> UserServiceException.mono("Invalid account status to request deletion");
        };
    }

    private Mono<Response> handleOkStatus(UserDTO userDTO) {
        if (userDTO.getRequestDeleteDate() == null) {
            userDTO.setAccountStatus(AccountStatus.REQUEST_DELETE);
            userDTO.setRequestDeleteDate(LocalDateTime.now());
            return DAO.save(userDTO)
                    .map(savedUserDTO -> Response.success("REQUEST_DELETE set, you have one year to revoke the deletion", savedUserDTO))
                    .onErrorResume(UserServiceException::mono);
        }
        return Mono.just(Response.success("Already in OK status without deletion request date", userDTO));
    }

    private Mono<Response> handleRequestDeleteStatus(UserDTO userDTO) {
        LocalDateTime requestDeleteDate = userDTO.getRequestDeleteDate();
        if (requestDeleteDate != null && ChronoUnit.YEARS.between(requestDeleteDate, LocalDateTime.now()) >= 1) {
            userDTO.setAccountStatus(AccountStatus.MARKED_DELETE);
            return DAO.save(userDTO)
                    .map(savedUserDTO -> Response.success("MARKED_DELETE set, your account has been invalidated you have one year to request your data", savedUserDTO))
                    .onErrorResume(UserServiceException::mono);
        }
        return Mono.just(Response.success("REQUEST_DELETE request is not yet a year old", userDTO));
    }

    private Mono<Response> handleMarkedDeleteStatus(UserDTO userDTO) {
        LocalDateTime requestDeleteDate = userDTO.getRequestDeleteDate();
        if (requestDeleteDate != null && ChronoUnit.YEARS.between(requestDeleteDate, LocalDateTime.now()) >= 2) {
            userDTO.setAccountStatus(AccountStatus.DELETE);
            return DAO.save(userDTO)
                    .map(savedUserDTO -> Response.success("DELETE set, after two years your account has been set to be terminated", savedUserDTO))
                    .onErrorResume(UserServiceException::mono);
        }
        return Mono.just(Response.success("MARKED_DELETE request is not yet a year old", userDTO));
    }

    private Mono<Response> handleDeleteStatus(UserDTO userDTO) {
        LocalDateTime requestDeleteDate = userDTO.getRequestDeleteDate();
        if (requestDeleteDate != null && ChronoUnit.DAYS.between(requestDeleteDate, LocalDateTime.now()) >= 3) {
            return DAO.delete(userDTO)
                    .thenReturn(Response.success("Your account has been terminated", true))
                    .onErrorResume(UserServiceException::mono);
        }
        return Mono.just(Response.success("DELETE request is not yet a year old", userDTO));
    }

}
