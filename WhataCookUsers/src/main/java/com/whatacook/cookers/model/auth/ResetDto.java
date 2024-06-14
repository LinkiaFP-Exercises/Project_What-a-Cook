package com.whatacook.cookers.model.auth;

import com.whatacook.cookers.model.users.UserDto;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * Data Transfer Object (DTO) for password reset.
 * Contains the reset code and expiration time.
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
 * - id: The unique identifier for the reset entity.
 * - code: The reset code.
 * - expiration: The expiration time of the reset code.
 * <p>
 * Methods:
 * - to(UserDto user): Converts a UserDto to a ResetDto.
 * - generateCode(): Generates a secure random reset code.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Document(collection = "reset")
public class ResetDto {

    @Id
    private String id;
    private String code = generateCode();
    private LocalDateTime expiration = LocalDateTime.now();

    /**
     * Converts a UserDto to a ResetDto.
     *
     * @param user The user to convert.
     * @return The corresponding ResetDto.
     */
    public static ResetDto to(UserDto user) {
        var resetDto = new ResetDto();
        resetDto.setId(user.get_id());
        return resetDto;
    }

    /**
     * Generates a secure random reset code.
     *
     * @return The generated reset code.
     */
    private String generateCode() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[64];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
