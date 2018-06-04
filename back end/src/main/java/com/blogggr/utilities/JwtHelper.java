package com.blogggr.utilities;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Created by Daniel Sunnen on 03.06.18.
 */
public class JwtHelper {

  private static final String issuer = "blogggr";
  private static final long maxValidHours = 24L;
  private static final String hmacKey = "7A7CFC99DB9802272D1987E287926AC52642417BF2D68455E180412C22";

  /**
   * Generate a JWT token with a subject claim and an expiration time The token expires 24h after
   * issuance
   *
   * @return a JWT
   */
  public static String generateJwt(String username) throws UnsupportedEncodingException {
    ZoneId luxembourgZoneId = ZoneId.of("Europe/Luxembourg");
    ZonedDateTime currentDateTime = LocalDateTime.now().atZone(luxembourgZoneId);
    ZonedDateTime validTillDateTime = currentDateTime.plus(Duration.ofHours(maxValidHours));
    Date expirationDate = Date.from(validTillDateTime.toInstant());
    Algorithm algorithm = Algorithm.HMAC512(hmacKey);
    return JWT.create()
        .withSubject(username) //sub key
        .withIssuer(issuer) //iss key
        .withExpiresAt(expirationDate) //exp key
        .sign(algorithm);
  }

  private static DecodedJWT getDecodedJwt(String token, Algorithm algorithm)
      throws Exception {
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
  public static String getSubjectFromValidJwt(String token)
      throws Exception {
    Algorithm algorithm = Algorithm.HMAC512(hmacKey);
    DecodedJWT jwtObject = getDecodedJwt(token, algorithm);
    return jwtObject.getSubject();
  }

  /**
   * Extract expiration date from valid JWT
   */
  public static java.util.Date getExpirationFromValidJwt(String token)
      throws Exception {
    Algorithm algorithm = Algorithm.HMAC512(hmacKey);
    DecodedJWT jwtObject = getDecodedJwt(token, algorithm);
    return jwtObject.getExpiresAt();
  }
}
