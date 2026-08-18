package com.onuryigitkocaturk.query_monitor.connector;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

// izlenen veritabanı şifresini AES ile şifreler ve çözer.
// key env variable'da tutulur.
@Component
public class ConnectionCredentialEncryptor {

    private static final String ALGORITHM = "AES/GCM/NoPadding"; // AES simetrik şifreleme algoritması, standart.
                                                                 // GCM, AES'in hem şifreleyen hem de
                                                                 // veri değiştirilmiş mi diye doğrulayan çalışma modu.
    private static final int GCM_IV_LENGTH_BYTES = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;

    private final SecretKeySpec key; // Ham AES anahtarını, Java'nın şifreleme API'sinin (Cipher) kullanabileceği bir nesneye sarmalıyor.

    public ConnectionCredentialEncryptor(@Value("${connection.encryption.key}") String base64Key) {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        this.key = new SecretKeySpec(keyBytes, "AES");
    }

    public String encrypt(String plainText) {
        try {
            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            byte[] ivAndCipherText = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, ivAndCipherText, 0, iv.length);
            System.arraycopy(cipherText, 0, ivAndCipherText, iv.length, cipherText.length);

            return Base64.getEncoder().encodeToString(ivAndCipherText);
        } catch (Exception e) {
            throw new IllegalStateException("Bağlantı şifresi şifrelenemedi", e);
        }
    }

    public String decrypt(String encryptedText) {
        try {
            byte[] ivAndCipherText = Base64.getDecoder().decode(encryptedText);
            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            byte[] cipherText = new byte[ivAndCipherText.length - GCM_IV_LENGTH_BYTES];
            System.arraycopy(ivAndCipherText, 0, iv, 0, iv.length);
            System.arraycopy(ivAndCipherText, iv.length, cipherText, 0, cipherText.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            byte[] plainText = cipher.doFinal(cipherText);

            return new String(plainText, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Bağlantı şifresi çözülemedi", e);
        }
    }
}
