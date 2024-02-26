package com.whatacook.cookers.view;

import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.users.UserDTO;
import com.whatacook.cookers.model.users.UserJson;
import com.whatacook.cookers.utilities.Util;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
public class ServiceComponentToUpdate {

    private final UserDAO DAO;

    public ServiceComponentToUpdate(UserDAO DAO)  {
        this.DAO = DAO;
    }

    Mono<UserJson> updateUser(UserJson userJson) {
        return DAO.findBy_id(userJson.get_id())
                .switchIfEmpty(Mono.error(
                        new UserServiceException("User not found with this ID", Map.of("_id", userJson.get_id()))))
                .flatMap(oldUser -> updatePlayerIfNecessary(oldUser, userJson))
                .flatMap(this::updateUserByDTOReturnJson);
    }

    private static Mono<UserDTO> updatePlayerIfNecessary(UserDTO oldUser, UserJson updateInfo) {
        AtomicBoolean updated = new AtomicBoolean(false);
        return Mono.just(oldUser)
                .flatMap(user -> {
                    updateFirstName(user, updateInfo, updated);
                    updateSurnames(user, updateInfo, updated);
                    updateEmail(user, updateInfo, updated);
                    updateBirthdate(user, updateInfo, updated);
                    updatePassword(user, updateInfo, updated);

                    if (!updated.get()) {
                        return Mono.error(new UserServiceException("No update required."));
                    }
                    return Mono.just(user);
                });
    }

    private static void updateFirstName(UserDTO user, UserJson updateInfo, AtomicBoolean updated) {
        updated.set(updateAttribute(user::getFirstName, () -> verifyNames(updateInfo.getFirstName()), user::setFirstName)
                || updated.get());
    }
    private static void updateSurnames(UserDTO user, UserJson updateInfo, AtomicBoolean updated) {
        updated.set(updateAttribute(user::getSurNames, () -> verifyNames(updateInfo.getSurNames()), user::setSurNames)
                || updated.get());
    }

    private static String verifyNames(String nameOrSurname) {
        return Optional.ofNullable(nameOrSurname).map(Util::TitleCase).orElse(null);
    }

    private static void updateEmail(UserDTO user, UserJson updateInfo, AtomicBoolean updated) {
        updated.set(updateAttribute(user::getEmail, updateInfo::getEmail, user::setEmail) || updated.get());
    }

    private static void updateBirthdate(UserDTO user, UserJson updateInfo, AtomicBoolean updated) {
        updated.set(updateAttribute(user::getBirthdate, updateInfo::getBirthdate, user::setBirthdate) || updated.get());
    }

    private static <T> boolean updateAttribute(Supplier<T> original, Supplier<T> updated, Consumer<T> setter) {
        T originalValue = original.get();
        T updatedValue = updated.get();
        if (updatedValue != null && !Objects.equals(originalValue, updatedValue)) {
            setter.accept(updatedValue);
            return true;
        }
        return false;
    }

    private static void updatePassword(UserDTO user, UserJson updateInfo, AtomicBoolean updated) {
        Optional.ofNullable(updateInfo.getPassword())
                .filter(pwd -> Util.encryptNotMatches(pwd, user.getPassword()))
                .ifPresent(pwd -> {
                    user.setPassword(Util.encryptPassword(pwd));
                    updated.set(true);
                });
    }

    private Mono<UserJson> updateUserByDTOReturnJson(UserDTO userToSave) {
        return Mono.just(userToSave).flatMap(DAO::save).map(UserDTO::toJson);
    }

}
