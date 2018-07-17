package com.blogggr.utilities;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.blogggr.config.AppConfig;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Created by Daniel Sunnen on 03.06.18.
 */
public class JwtHelper {

  private static final String ISSUER = "blogggr";
  private static final long MAX_VALID_HOURS = 24L;
  private static final String HMAC_KEY = "7A7CFC99DB9802272D1987E287926AC52642417BF2D68455E180412C22";

  private JwtHelper(){
    //hide otherwise implicit public constructor
  }

  /**
   * Generate a JWT token with a subject claim and an expiration time The token expires 24h after
   * issuance
   *
   * @return a JWT
   */
  public static String generateJwt(String username) throws UnsupportedEncodingException {
    ZonedDateTime currentDateTime = LocalDateTime.now().atZone(AppConfig.LUXEMBOURG_ZONE_ID);
    ZonedDateTime validTillDateTime = currentDateTime.plus(Duration.ofHours(MAX_VALID_HOURS));
    Date expirationDate = Date.from(validTillDateTime.toInstant());
    Algorithm algorithm = Algorithm.HMAC512(HMAC_KEY);
    return JWT.create()
        .withSubject(username) //sub key
        .withIssuer(ISSUER) //iss key
        .withExpiresAt(expirationDate) //exp key
        .sign(algorithm);
  }

  private static DecodedJWT getDecodedJwt(String token, Algorithm algorithm)
      throws UnsupportedEncodingException {
    if (algorithm == null) {
      throw new UnsupportedEncodingException();
    }
    JWTVerifier verifier = JWT.require(algorithm)
        .withIssuer(ISSUER)
        .acceptExpiresAt(1L)
        .build(); //Reusable verifier instance
    return verifier.verify(token);
  }

  /**
   * Extract subject from valid JWT
   */
  public static String getSubjectFromValidJwt(String token)
      throws UnsupportedEncodingException {
    Algorithm algorithm = Algorithm.HMAC512(HMAC_KEY);
    DecodedJWT jwtObject = getDecodedJwt(token, algorithm);
    return jwtObject.getSubject();
  }

  /**
   * Extract expiration date from valid JWT
   */
  public static ZonedDateTime getExpirationFromValidJwt(String token)
      throws UnsupportedEncodingException {
    Algorithm algorithm = Algorithm.HMAC512(HMAC_KEY);
    DecodedJWT jwtObject = getDecodedJwt(token, algorithm);
    return ZonedDateTime.ofInstant(jwtObject.getExpiresAt().toInstant(), AppConfig.LUXEMBOURG_ZONE_ID);
  }
}
