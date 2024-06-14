package com.whatacook.cookers.model.auth;

import com.whatacook.cookers.model.users.UserDto;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * Data Transfer Object (DTO) for account activation.
 * Contains the activation code and expiration time.
 * <p>
 * Annotations:
 * - @NoArgsConstructor: Generates a no-arguments constructor.
 * - @AllArgsConstructor: Generates an all-arguments constructor.
 * - @Getter: Generates getter methods for all fields.
 * - @Setter: Generates setter methods for all fields.
 * - @ToString: Generates a toString method for the class.
 * - @EqualsAndHashCode: Generates equals and hashCode methods for the class.
 * - @Document: Marks this class as a MongoDB document.
 * <p>
 * Fields:
 * - id: The unique identifier for the activation entity.
 * - code: The activation code.
 * - expiration: The expiration time of the activation code.
 * <p>
 * Methods:
 * - to(UserDto user): Converts a UserDto to an ActivationDto.
 * - generateCode(): Generates a secure random activation code.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Document(collection = "activation")
public class ActivationDto {

    @Id
    private String id;
    private String code = generateCode();
    private LocalDateTime expiration = LocalDateTime.now();

    /**
     * Converts a UserDto to an ActivationDto.
     *
     * @param user The user to convert.
     * @return The corresponding ActivationDto.
     */
    public static ActivationDto to(UserDto user) {
        ActivationDto activationDto = new ActivationDto();
        activationDto.setId(user.get_id());
        return activationDto;
    }

    /**
     * Generates a secure random activation code.
     *
     * @return The generated activation code.
     */
    private String generateCode() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
