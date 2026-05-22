package com.bsn.utils;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @Created by 2019-08-29 14:19
 */
public class AesUtil {

    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";//The default encryption algorithm
    private static final String key = "sde@5f98H*^hsff%dfs$r344&df8543*ereesde@5f98H*^hsff%dfs$r344&df8543*ereesde@5f98H*^hsff%dfs$r344&df8543*ereesde@5f98Heesde@5f98H";

    public static String encrypt(String sSrc) throws Exception {
        return encrypt(sSrc, key);
    }

    public static String decrypt(String encryptStr) throws Exception {
        return decrypt(encryptStr, key);
    }

    public static String decrypt(String encryptStr, String secretKey) throws Exception {

        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
        //Initialize with key, set to decryption mode
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(secretKey));
        //execute operation
        byte[] result = cipher.doFinal(Base64.getMimeDecoder().decode(encryptStr));
        return new String(result, "utf-8");
    }

    public static void main(String[] args) throws Exception {
        String sign = "LOhSeKO/9lWnT1qKOEoi8DCfJmxhWupaTrNw8eNJMsLigwPsS4vHYnE9Vvy+StkMmB1dPknrbqW7MUiQryWnOUyufncgmmGXZF5LCfTfKtVxz2EyoSUYE2nEDycuViuK7hOI8dPMSzTUrjRW7ZOws5QjzwR+cgiOFPaHC+q4HccWfLAjGVNY/5VupTRqkoVP";
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJkY0VtYWlsIjoic29uZ3hpd2VpQHJlZGRhdGV0ZWNoLmNvbSIsImRjQ29kZSI6IlNEQzAwMDgxMTkzNDQ3NyIsInZlcmlmaWNhdGlvbiI6IjE2Nzc2NTIyMDIyMTQifQ.-CryW2n8KEJHyl6197BaGG3KIN0n3rYVXVy94hRfvLQ";

        String decrypt = decrypt(sign, token);
        System.out.println(decrypt);

    }


    public static String encrypt(String sSrc, String secretKey) throws Exception {

        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
        byte[] byteContent = sSrc.getBytes("utf-8");
        // A cipher initialized to encryption mode
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(secretKey));
        // encrypt
        byte[] result = cipher.doFinal(byteContent);
        // Return via Base64 transcoding
//        return new BASE64Encoder().encode(result);
        return Base64.getMimeEncoder().encodeToString(result);
    }


    private static SecretKeySpec getSecretKey(final String key) throws NoSuchAlgorithmException {
        //Returns the KeyGenerator object that generated the specified algorithm key generator
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(key.getBytes());
        //AES The key length is required 128
        kg.init(128, secureRandom);
        //Generate a key
        SecretKey secretKey = kg.generateKey();
        // Convert to an AES private key
        return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
    }

}
