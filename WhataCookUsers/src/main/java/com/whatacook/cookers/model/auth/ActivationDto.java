package com.whatacook.cookers.model.auth;

import com.whatacook.cookers.model.users.UserDTO;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

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

    public static ActivationDto to(UserDTO user) {
        ActivationDto activationDto = new ActivationDto();
        activationDto.setId(user.get_id());
        return activationDto;
    }

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
