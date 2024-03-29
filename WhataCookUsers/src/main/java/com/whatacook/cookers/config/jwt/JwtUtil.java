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

@Getter @Setter
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

    public String getUsernameFromToken(String token) { return getClaimFromToken(token, Claims::getSubject); }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private  <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
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

    public String generateToken(AuthRequestDto AuthRequestDto) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, AuthRequestDto.getUsername());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .audience().add(audience).and()
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey()).compact();
    }

    public boolean hasToken(String token) { return StringUtils.hasText(token); }

    public String extractPrefix(String token) { return token.substring(7); }

    public Boolean isValidToken(String token) {
        if (!token.startsWith(prefix)) throw new JwtException("This Token is not Bearer");
        else if (token.split("\\.").length != 3) throw new JwtException("This Token is not ours");
        else if (isExpired(token)) throw new JwtException("This Token has Expired");
        else return true;
    }

}
