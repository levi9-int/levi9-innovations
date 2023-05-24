package org.example.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;

import java.util.Base64;

public class JwtDecoder {

    public static String getUsername(String token) {
        String username;

        try {
            final Claims claims = getAllClaimsFromToken(token);
            username = claims.getSubject();
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    private static Claims getAllClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            claims = null;
        }

        // Preuzimanje proizvoljnih podataka je moguce pozivom funkcije claims.get(key)

        return claims;
    }
}
