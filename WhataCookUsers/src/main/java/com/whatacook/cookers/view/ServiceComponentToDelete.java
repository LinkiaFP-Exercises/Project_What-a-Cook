package com.whatacook.cookers.view;

import com.whatacook.cookers.model.constants.AccountStatus;
import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.users.UserJson;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class ServiceComponentToDelete {

    private final UserDAO DAO;

    public ServiceComponentToDelete(UserDAO DAO) { this.DAO = DAO; }
    Mono<Boolean> proceedIfApplicable(UserJson userJson) {
        return DAO.findBy_id(userJson.get_id())
                .switchIfEmpty(Mono.defer(() -> Mono.error(
                            UserServiceException.pull("User not found"))))
                .filter(user -> user.getAccountStatus() == AccountStatus.DELETE)
                .switchIfEmpty(Mono.defer(() -> Mono.error(
                            UserServiceException.pull("AccountStatus does not allow"))))
                .flatMap(user -> DAO.delete(user).thenReturn(true))
                .onErrorResume(e -> Mono.error(
                            UserServiceException.pull("Look in content for errors",
                                                                Map.of("Error", e.getMessage()))));
    }

}
