package com.blogggr.utilities;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.time.Duration;
import java.time.Instant;

/**
 * Created by Daniel Sunnen on 04.11.16.
 */
@Component
public class Cryptography {

    final private static char[] lookupHexChars = "0123456789abcdef".toCharArray();

    private static String byteToString(byte[] data){
        char[] hexData = new char[data.length*2];
        for(int i=0;i<data.length;i++){
            int value = data[i] & 0xFF;
            hexData[i*2] = lookupHexChars[value >> 4];
            hexData[i*2+1] = lookupHexChars[value & 0x0F];
        }
        return new String(hexData);
    }

    public static String computeSHA256Hash(String input){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(input.getBytes());
            byte[] bytesHash = md.digest();
            return byteToString(bytesHash);
        }
        catch(NoSuchAlgorithmException e){
            return "";
        }
    }

    @Value("${hmackey}")
    private String HMACKey;

    private final String issuer = "blogggr";

    /**
     * Generate a JWT token with a subject claim and an expiration time
     * The token expires 24h after issuance
     * @param username
     * @return a JWT
     * @throws UnsupportedEncodingException
     */
    public String generateJWT(String username) throws UnsupportedEncodingException {
        Algorithm algorithm = Algorithm.HMAC512(HMACKey);
        Instant validTillDate = Instant.now().plus(Duration.ofHours(24));
        Date expirationDate = Date.from(validTillDate);
        return JWT.create()
                .withSubject(username)
                .withIssuer(issuer)
                .withExpiresAt(expirationDate)
                .sign(algorithm);
    }

    public DecodedJWT verifyJWT(String token) throws UnsupportedEncodingException{
        Algorithm algorithm = Algorithm.HMAC512(HMACKey);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build(); //Reusable verifier instance
        return verifier.verify(token);
    }
}
