package com.whatacook.cookers.config.jwt;

import com.whatacook.cookers.model.auth.AuthRequestDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for handling JWT operations such as token creation, parsing, and validation.
 * <p>
 * Annotations:
 * - @Getter: Generates getter methods for all fields.
 * - @Setter: Generates setter methods for all fields.
 * - @ConfigurationProperties: Binds the properties prefixed with "security.jwt" to this class.
 * <p>
 * Fields:
 * - Various configuration properties for JWT handling.
 * <p>
 * Methods:
 * - getSecretKey(): Returns the secret key for signing the JWT.
 * - getUsernameFromToken(String token): Extracts the username from the JWT.
 * - getExpirationDateFromToken(String token): Extracts the expiration date from the JWT.
 * - getClaimFromToken(String token, Function<Claims, T> claimsResolver): Extracts a claim from the JWT.
 * - getAllClaimsFromToken(String token): Extracts all claims from the JWT.
 * - isExpired(String token): Checks if the JWT is expired.
 * - generateToken(AuthRequestDto authRequestDto): Generates a JWT for the given user.
 * - doGenerateToken(Map<String, Object> claims, String subject): Generates a JWT with the specified claims and subject.
 * - generateExpiredTokenForTest(Map<String, Object> claims, String subject): Generates an expired JWT for testing purposes.
 * - hasToken(String token): Checks if the token is not empty or null.
 * - extractPrefix(String token): Extracts the prefix from the token.
 * - isValidToken(String token): Validates the JWT.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "security.jwt")
public final class JwtUtil {

    private String authRoot;
    private String loginUrl;
    private String signInUrl;
    private String forgotPass;
    private String header;
    private String activation;
    private String resend;
    private String resetCode;
    private String codeToSet;
    private String prefix;
    private String issuer;
    private String audience;
    @Getter(AccessLevel.NONE)
    private String secret;
    private long expiration;

    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(this.secret.getBytes());
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().verifyWith(getSecretKey()).build()
                .parseSignedClaims(token.replace(prefix, "")).getPayload();
    }

    private Boolean isExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(AuthRequestDto authRequestDto) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, authRequestDto.getUsername());
    }

    public String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .audience().add(audience).and()
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey()).compact();
    }

    public String generateExpiredTokenForTest(Map<String, Object> claims, String subject) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis - expiration * 2);
        Date expiredDate = new Date(nowMillis - expiration);
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .audience().add(audience).and()
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiredDate)
                .signWith(getSecretKey()).compact();
    }

    public boolean hasToken(String token) {
        return StringUtils.hasText(token);
    }

    public String extractPrefix(String token) {
        return token.substring(7);
    }

    public Boolean isValidToken(String token) {
        if (!token.startsWith(prefix)) throw new JwtException("This Token is not Bearer");
        else if (token.split("\\.").length != 3) throw new JwtException("This Token is not ours");
        else if (isExpired(token)) throw new JwtException("This Token has Expired");
        else return true;
    }
}
