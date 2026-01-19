package com.adera.aderapos.security.encryption;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Security;
import java.util.Base64;

/**
 * Service for encryption and decryption using AES algorithm.
 */
@Service
public class EncryptionService {

    /**
     * Static block to add Bouncy Castle as a security provider.
     * This ensures that Bouncy Castle is available for cryptographic operations.
     *
     */
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Encrypts the given data using AES encryption.
     *
     * @param data the data to encrypt
     * @param key  the secret key for encryption
     * @return the encrypted data in Base64 format
     * @throws Exception if an error occurs during encryption
     */
    public String encryptAES(String data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * Decrypts the given encrypted data using AES decryption.
     *
     * @param encryptedData the encrypted data in Base64 format
     * @param key           the secret key for decryption
     * @return the decrypted data as a string
     * @throws Exception if an error occurs during decryption
     */
    public String decryptAES(String encryptedData, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decoded = Base64.getDecoder().decode(encryptedData);
        return new String(cipher.doFinal(decoded));
    }

    /**
     * Generates a new AES secret key.
     *
     * @return the generated SecretKey
     * @throws Exception if an error occurs during key generation
     */
    public SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        return keyGen.generateKey();
    }
}
