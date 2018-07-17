package com.blogggr.utilities;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Daniel Sunnen on 04.11.16.
 */
@Component
public class Cryptography {

  private Cryptography(){}

  private static final char[] lookupHexChars = "0123456789abcdef".toCharArray();

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
}
