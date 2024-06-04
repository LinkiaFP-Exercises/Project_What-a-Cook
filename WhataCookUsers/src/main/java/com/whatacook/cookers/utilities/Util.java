package com.whatacook.cookers.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

public class Util {

    public static boolean isValidBirthdate(LocalDate birthdate) {
        return birthdate != null && !birthdate.isAfter(LocalDate.now().minusYears(7));
    }

    public static boolean notValidBirthdate(LocalDate birthdate) {
        return birthdate == null || birthdate.isAfter(LocalDate.now().minusYears(7));
    }

    public static boolean isNullOrEmpty(String something) {
        return !StringUtils.hasText(something);
    }

    public static boolean isNullOrEmptyOrLiteralNull(String something) {
        return isNullOrEmpty(something) || "null".equalsIgnoreCase(something.trim());
    }

    public static boolean notNullOrEmpty(String something) {
        return StringUtils.hasText(something) && !"null".equalsIgnoreCase(something.trim());
    }

    public static String TitleCase(String toConvert) {
        return isNullOrEmptyOrLiteralNull(toConvert) ? null : TitleCase.all(toConvert);
    }

    public static String encryptPassword(String toEncrypt) {
        return BCrypt.encode(toEncrypt);
    }

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
//        String characters = "!¡|'´`¨\\^*+@·#$%&/{}()=\\-_:.;,<>?¿";
        String charactersUnicode = "\\u0021\\u00A1\\u007C\\u0027\\u00B4\\u0060\\u00A8\\u005E\\u002A\\u002B\\u0040\\u00B7\\u0023\\u0024\\u0025\\u0026\\u002F\\u007B\\u007D\\u0028\\u0029\\u003D\\u005C\\u002D\\u005F\\u003A\\u002E\\u003B\\u002C\\u003C\\u003E\\u003F\\u00BF";
        String regex = "^(?=.*[\\p{Ll}])(?=.*[\\p{Lu}])(?=.*\\p{N})(?=.*[%s])[\\p{L}\\p{N}%s]{8,}$";
        return String.format(regex, charactersUnicode, charactersUnicode);
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
