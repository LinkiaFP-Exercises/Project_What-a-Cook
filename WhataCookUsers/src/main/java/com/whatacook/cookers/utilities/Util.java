package com.whatacook.cookers.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

public class Util {

    public static String msgError(String input) {
        return String.format("Something went wrong trying to %s 8==> ", input);
    }

    public static boolean isValidBirthdate(LocalDate birthdate) {
        return birthdate != null && !birthdate.isAfter(LocalDate.now().minusYears(7));
    }
    public static boolean notValidBirthdate(LocalDate birthdate) {
        return birthdate == null || birthdate.isAfter(LocalDate.now().minusYears(7));
    }

    public static boolean isNullOrEmpty(String something) { return !StringUtils.hasText(something); }

    public static boolean notNullOrEmpty(String something) { return StringUtils.hasText(something); }

    public static String TitleCase(String toConvert) { return TitleCase.all(toConvert); }

    public static String encryptPassword(String toEncrypt) { return BCrypt.encode(toEncrypt); }

    public static boolean encryptMatches(String rawPassword, String encodedPassword) {
        return notNullOrEmpty(rawPassword) && BCrypt.matches(rawPassword, encodedPassword);
    }

    public static boolean encryptNotMatches(String rawPassword, String encodedPassword) {
        return !encryptMatches(rawPassword, encodedPassword);
    }

    public static boolean isValidEmail(String email) {
        return notNullOrEmpty(email) && email.matches(buildEmailRegex());
    }

    public static boolean notValidEmail(String email) {
        return isNullOrEmpty(email) || !email.matches(buildEmailRegex());
    }

    public static String buildEmailRegex() {
        String front = "[\\p{L}\\p{N}!#$%&'*+/=?^_`{|}~-]+";
        String back = "[\\p{L}\\p{N}](?:[a-z0-9-]*[\\p{L}\\p{N}]";
        String domain = String.format("(?:%s)?.)+%s)?", back, back);
        return String.format("%s(?:.%s)*@%s", front, front, domain);
    }

    public static boolean isValidPassword(String password) {
        return notNullOrEmpty(password) && password.matches(buildPassRegex());
    }

    public static boolean notValidPassword(String password) {
        return isNullOrEmpty(password) || !password.matches(buildPassRegex());
    }

    public static String buildPassRegex() {
        String characters = "!¡|'´`¨^*+@·#$%&/{}()=-_:.;,<>?¿";
        String regex = "^(?=.*[\\p{Ll}])(?=.*[\\p{Lu}])(?=.*\\p{N})(?=.*[%s])[\\p{L}\\p{N}%s]{8,}$";
        return String.format(regex, characters, characters);
    }

    public static String convertToJsonAsString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "{\"error\": \"Error converting to JSON.\"}";
        }
    }
    public static byte[] convertToJsonAsBytes(Object obj) {
        try {
            return new ObjectMapper().writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            return "{\"error\": \"Error converting to JSON.\"}".getBytes();
        }
    }

    private static final BCryptPasswordEncoder BCrypt = new BCryptPasswordEncoder();

}
