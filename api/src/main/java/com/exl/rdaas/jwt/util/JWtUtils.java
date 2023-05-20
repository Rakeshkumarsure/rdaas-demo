package com.exl.rdaas.jwt.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.exl.rdaas.jwt.repository.UserDetailsImpl;

import org.springframework.security.core.Authentication;

import java.security.Key;
import java.util.Date;
import java.util.Random;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JWtUtils {

	@Value("${rdaas.app.jwtExpirationMs}")
	private int jwtExpirationMs;

	private static final Logger logger = LoggerFactory.getLogger(JWtUtils.class);
	private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	public String generateJwtToken(Authentication authentication) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		return Jwts.builder().setSubject((userPrincipal.getUsername())).setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + 86400000)).signWith(key)
				.claim("tenant_id", userPrincipal.getTenantid()).compact();
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
			return true;
		} catch (SecurityException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}

	public static String generateTenantId() {
		long timestamp = System.currentTimeMillis();
		int random = new Random().nextInt(1000000);
		return "Tenant" + String.format("%013d", timestamp) + String.format("%06d", random);
	}
	
	public static String getTenantIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.get("tenant_id", String.class);
    }
}
