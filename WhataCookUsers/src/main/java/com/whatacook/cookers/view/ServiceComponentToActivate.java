package com.whatacook.cookers.view;

import com.whatacook.cookers.config.jwt.JwtUtil;
import com.whatacook.cookers.model.constants.AccountStatus;
import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.users.UserDTO;
import com.whatacook.cookers.model.users.UserJson;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ServiceComponentToActivate {

    private final UserDAO DAO;
    private final JwtUtil jwtUtil;

    public ServiceComponentToActivate(UserDAO DAO, JwtUtil jwtUtil) {
        this.DAO = DAO;
        this.jwtUtil = jwtUtil;
    }

    public Mono<UserJson> userByTokenSentByEmail(String token) {
        return Mono.just(token)
                .map(jwtUtil::getIdFromToken)
                .flatMap(DAO::findById)
                    .flatMap(userDTO -> {
                        if (userDTO.getAccountStatus() == AccountStatus.PENDING) {
                            userDTO.setAccountStatus(AccountStatus.OK);
                            return DAO.save(userDTO);
                        } else {
                            return Mono.error(UserServiceException.pull("The Account Status is not correct to activate account"));
                        }
                    })
                    .map(UserDTO::toJson)
                .doOnError(throwable -> UserServiceException.throwUp(throwable.getMessage()));
    }
}
