package com.whatacook.cookers.service.components;

import static com.whatacook.cookers.model.constants.AccountStatus.*;

import com.whatacook.cookers.model.constants.AccountStatus;
import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.users.UserDto;
import com.whatacook.cookers.model.users.UserJson;
import com.whatacook.cookers.utilities.Util;
import com.whatacook.cookers.service.contracts.UserDao;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
public class UpdateComponent {

    private final UserDao DAO;

    public UpdateComponent(UserDao DAO) {
        this.DAO = DAO;
    }

    public Mono<UserJson> updateUser(UserJson userJson) {
        return DAO.findBy_id(userJson.get_id())
                .switchIfEmpty(UserServiceException.mono(
                        "User not found with this ID", Map.of("_id", userJson.get_id())))
                .flatMap(oldUser -> updatePlayerIfNecessary(oldUser, userJson))
                .flatMap(this::updateUserByDtoReturnJson);
    }

    private static Mono<UserDto> updatePlayerIfNecessary(UserDto oldUser, UserJson updateInfo) {
        AtomicBoolean updated = new AtomicBoolean(false);
        return Mono.just(oldUser)
                .flatMap(user -> {
                    updateFirstName(user, updateInfo, updated);
                    updateSurnames(user, updateInfo, updated);
                    updateEmail(user, updateInfo, updated);
                    updateBirthdate(user, updateInfo, updated);
                    updatePassword(user, updateInfo, updated);
                    updateAccountStatus(user, updateInfo, updated);

                    if (!updated.get()) {
                        return UserServiceException.mono("No update required or data is invalid.");
                    }
                    return Mono.just(user);
                });
    }

    private static void updateFirstName(UserDto user, UserJson updateInfo, AtomicBoolean updated) {
        boolean isFirstNameUpdated = updateAttribute(user::getFirstName, () -> verifyNames(updateInfo.getFirstName()), user::setFirstName);
        updated.set(isFirstNameUpdated || updated.get());
    }

    private static void updateSurnames(UserDto user, UserJson updateInfo, AtomicBoolean updated) {
        boolean isSurnamesUpdated = updateAttribute(user::getSurNames, () -> verifyNames(updateInfo.getSurNames()), user::setSurNames);
        updated.set(isSurnamesUpdated || updated.get());
    }

    private static void updateEmail(UserDto user, UserJson updateInfo, AtomicBoolean updated) {
        boolean isEmailUpdated = updateAttribute(user::getEmail, () -> verifyEmail(updateInfo.getEmail()), user::setEmail);
        updated.set(isEmailUpdated || updated.get());
    }

    private static void updateBirthdate(UserDto user, UserJson updateInfo, AtomicBoolean updated) {
        boolean isBirthdateUpdated = updateAttribute(user::getBirthdate, () -> verifyBirthdate(updateInfo.getBirthdate()), user::setBirthdate);
        updated.set(isBirthdateUpdated || updated.get());
    }

    private static void updatePassword(UserDto user, UserJson updateInfo, AtomicBoolean updated) {
        if (updateInfo.getNewPassword() != null) {
            Optional.ofNullable(updateInfo.getPassword())
                    .filter(pwd -> Util.encryptMatches(pwd, user.getPassword()))
                    .orElseThrow(UserServiceException::passNotMatch);
            Optional.ofNullable(updateInfo.getNewPassword())
                    .filter(Util::isValidPassword)
                    .ifPresent(newPwd -> {
                        user.setPassword(Util.encryptPassword(newPwd));
                        updated.set(true);
                    });
        }
    }

    private static void updateAccountStatus(UserDto user, UserJson updateInfo, AtomicBoolean updated) {
        if (updateInfo.getAccountStatus() != null) {
            boolean isCurrentStatusEligibleForUpdate =
                    EnumSet.of(OK, OFF, OUTDATED, REQUEST_DELETE).contains(user.getAccountStatus());
            AccountStatus toUpdate = valueOf(updateInfo.getAccountStatus());
            boolean isNewStatusValid = !MARKED_DELETE.equals(toUpdate);

            if (isCurrentStatusEligibleForUpdate && isNewStatusValid) {
                boolean isAccountStatusUpdated =
                        updateAttribute(user::getAccountStatus, () -> toUpdate, user::setAccountStatus);
                updated.set(isAccountStatusUpdated || updated.get());
            }
        }
    }

    private Mono<UserJson> updateUserByDtoReturnJson(UserDto userToSave) {
        return Mono.just(userToSave).flatMap(DAO::save).map(UserDto::toJson);
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

    private static String verifyNames(String nameOrSurname) {
        return Optional.ofNullable(nameOrSurname).map(Util::TitleCase).orElse(null);
    }

    private static String verifyEmail(String email) {
        return Optional.ofNullable(email).filter(Util::isValidEmail).orElse(null);
    }

    private static LocalDate verifyBirthdate(LocalDate localDate) {
        return Optional.ofNullable(localDate).filter(Util::isValidBirthdate).orElse(null);
    }

}
