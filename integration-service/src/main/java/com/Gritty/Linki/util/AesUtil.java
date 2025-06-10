package com.Gritty.Linki.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


public class AesUtil {

    private static final String ALGORITHM = "AES";
    private static String key;

    public static void setKey(String aesKey) {
        key = aesKey;
    }


    // 🔐 암호화
    /**
     * Encrypts a plain text string using AES algorithm and a static secret key.
     *
     * @param plainText The plain text to encrypt.
     * @return The AES-encrypted and Base64-encoded string.
     * @throws RuntimeException If encryption fails.
     */
    public static String encrypt(String plainText){
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 🔓 복호화
    /**
     * Decrypts an AES-encrypted and Base64-encoded string back to plain text using the static secret key.
     *
     * @param encryptedText The Base64-encoded AES encrypted text.
     * @return The decrypted plain text string.
     * @throws RuntimeException If decryption fails.
     */
    public static String decrypt(String encryptedText) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, "UTF-8");
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
