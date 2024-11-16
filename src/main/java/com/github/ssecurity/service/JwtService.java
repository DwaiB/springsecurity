package com.github.ssecurity.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	@Value("${security.jwt.secret-key}")
	private String secretKey;
	
	@Value("${security.jwt.expiration-time}")
	private Long jwtExpTime;
	
	public String extractUserName(String token) {
		System.out.println("Extract UserName ");
		return extractClaim(token,Claims::getSubject);
	}
	
	public <T> T extractClaim(String token,Function<Claims,T> claimsResolver) {
		Claims claims  = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	public Claims extractAllClaims(String token) {
		return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
		
	}
	private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
	public Long getExpirationTime() {
		return jwtExpTime;
	}
	
	public String generateToken(UserDetails user) {
		return generateToken(new HashMap<>(),user);
	}
	public String generateToken(Map<String,Object> extraClaims,UserDetails userdetails) {
		return buildToken(extraClaims,userdetails,jwtExpTime);
	}
	public String buildToken(Map<String,Object> claims,UserDetails userDetails, Long expTime ) {
		return Jwts
				.builder()
				.claims()
				.add(claims)
				.subject(userDetails.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis()+expTime))
				.and()
				.signWith(getSignInKey())
				.compact();
	}
	public boolean isTokenValid(String token,UserDetails userDetails) {
		String username = extractUserName(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
		
	}
	public boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}
	public Date extractExpiration(String token) {
		return extractClaim(token,Claims::getExpiration);
	}
}
