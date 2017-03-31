package com.hsbc.greenpacket.util;


import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AlgorithmUtil {
    public static byte[] aesEncrypt(String key, byte[] data) throws Exception {
        byte[] encrypted = null;
        byte[] raw = key.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        encrypted = cipher.doFinal(data);
        return encrypted;
  }
  public static byte[] aesDecrypt(String key, byte[] data) throws Exception {
        byte[] original = null;
        byte[] deData = data;
        byte[] raw = key.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        original = cipher.doFinal(deData);
        return original;
  }
  public static String gen256Key(String key) {
      int length = key.length();
      if (length > 32) {
          return key.substring(length - 32, length);
      } else if (length < 32) {
          char appendex[] = new char[32 - length];
          for (char c : appendex) {
              c = '\0';
          }
          return key.concat(new String(appendex));
      }
      return key;
    }
    
}
