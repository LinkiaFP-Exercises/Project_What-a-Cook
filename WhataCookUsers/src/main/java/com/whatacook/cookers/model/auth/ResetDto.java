package com.whatacook.cookers.model.auth;

import com.whatacook.cookers.model.users.UserDTO;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
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
@Document(collection = "reset")
public class ResetDto {

    @Id
    private String id;
    private String code = generateCode();
    @CreatedDate
    private LocalDateTime expiration = LocalDateTime.now();

    public static ResetDto to(UserDTO user) {
        var resetDto = new ResetDto();
        resetDto.setId(user.get_id());
        return resetDto;
    }

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[64];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

}
