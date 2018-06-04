package com.blogggr.utilities;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
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

  private static String byteToString(byte[] data) {
    char[] hexData = new char[data.length * 2];
    for (int i = 0; i < data.length; i++) {
      int value = data[i] & 0xFF;
      hexData[i * 2] = lookupHexChars[value >> 4];
      hexData[i * 2 + 1] = lookupHexChars[value & 0x0F];
    }
    return new String(hexData);
  }

  public static String computeSHA256Hash(String input) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      md.update(input.getBytes());
      byte[] bytesHash = md.digest();
      return byteToString(bytesHash);
    } catch (NoSuchAlgorithmException e) {
      return "";
    }
  }

  public Cryptography(@Value("${hmackey}") String HMACKey) {
    try {
      this.algorithm = Algorithm.HMAC512(HMACKey);
    } catch (UnsupportedEncodingException e) {
      this.algorithm = null;
    }
  }

  private static final String issuer = "blogggr";
  private static final long maxValidHours = 24L;
  private static Algorithm algorithm;

  /**
   * Generate a JWT token with a subject claim and an expiration time The token expires 24h after
   * issuance
   *
   * @return a JWT
   */
  public static String generateJWT(String username) throws UnsupportedEncodingException {
    Instant validTillDate = Instant.now().plus(Duration.ofHours(maxValidHours));
    Date expirationDate = Date.from(validTillDate);
    if (algorithm == null) {
      throw new UnsupportedEncodingException();
    }
    return JWT.create()
        .withSubject(username) //sub key
        .withIssuer(issuer) //iss key
        .withExpiresAt(expirationDate) //exp key
        .sign(algorithm);
  }

  private DecodedJWT getDecodedJWT(String token)
      throws UnsupportedEncodingException, JWTVerificationException {
    if (algorithm == null) {
      throw new UnsupportedEncodingException();
    }
    JWTVerifier verifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .acceptExpiresAt(1L)
        .build(); //Reusable verifier instance
    return verifier.verify(token);
  }

  /**
   * Extract subject from valid JWT
   */
  public String getSubjectFromValidJWT(String token)
      throws UnsupportedEncodingException, JWTVerificationException {
    if (algorithm == null) {
      throw new UnsupportedEncodingException();
    }
    DecodedJWT jwtObject = getDecodedJWT(token);
    return jwtObject.getSubject();
  }

  /**
   * Extract expiration date from valid JWT
   */
  public java.util.Date getExpirationFromValidJWT(String token)
      throws UnsupportedEncodingException, JWTVerificationException {
    if (algorithm == null) {
      throw new UnsupportedEncodingException();
    }
    DecodedJWT jwtObject = getDecodedJWT(token);
    return jwtObject.getExpiresAt();
  }
}
