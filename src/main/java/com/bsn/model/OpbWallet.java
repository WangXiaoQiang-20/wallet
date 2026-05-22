package com.bsn.model;

import com.bsn.utils.JsonFileUtil;
import com.bsn.utils.JsonUtils;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.crypto.params.KeyParameter;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Hash;
import org.web3j.crypto.WalletUtils;
import org.web3j.utils.Numeric;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.UUID;


/**
 * Desc：
 *
 * @Created by 2022-01-20 16:57
 */
public class OpbWallet {


    private static final int N_LIGHT = 1 << 12;
    private static final int P_LIGHT = 6;

    private static final int N_STANDARD = 1 << 18;
    private static final int P_STANDARD = 1;

    private static final int R = 8;
    private static final int DKLEN = 32;

    private static final int CURRENT_VERSION = 3;

    private static final String CIPHER = "aes-128-ctr";

    public static final String AES_128_CTR = "pbkdf2";

    public static final String SCRYPT = "scrypt";


    public static final String CHARSETS = "UTF-8";



    private static OpbWalletFile createWalletFile(
            byte[] cipherText,
            byte[] iv,
            byte[] salt,
            byte[] mac,
            int n,
            int p) {

        OpbWalletFile walletFile = new OpbWalletFile();

        OpbWalletFile.Crypto crypto = new OpbWalletFile.Crypto();
        crypto.setCipher(CIPHER);
        crypto.setCiphertext(Numeric.toHexStringNoPrefix(cipherText));

        OpbWalletFile.CipherParams cipherParams = new OpbWalletFile.CipherParams();
        cipherParams.setIv(Numeric.toHexStringNoPrefix(iv));
        crypto.setCipherparams(cipherParams);

        crypto.setKdf(SCRYPT);
        OpbWalletFile.ScryptKdfParams kdfParams = new OpbWalletFile.ScryptKdfParams();
        kdfParams.setDklen(DKLEN);
        kdfParams.setN(n);
        kdfParams.setP(p);
        kdfParams.setR(R);
        kdfParams.setSalt(Numeric.toHexStringNoPrefix(salt));
        crypto.setKdfparams(kdfParams);

        crypto.setMac(Numeric.toHexStringNoPrefix(mac));
        walletFile.setCrypto(crypto);
        walletFile.setId(UUID.randomUUID().toString());
        walletFile.setVersion(CURRENT_VERSION);

        return walletFile;
    }

    private static byte[] generateDerivedScryptKey(
            byte[] password, byte[] salt, int n, int r, int p, int dkLen) {
        return SCrypt.generate(password, salt, n, r, p, dkLen);
    }

    private static byte[] generateAes128CtrDerivedKey(
            byte[] password, byte[] salt, int c, String prf) throws CipherException {

        if (!prf.equals("hmac-sha256")) {
            throw new CipherException("Unsupported prf:" + prf);
        }

        PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA256Digest());
        gen.init(password, salt, c);
        return ((KeyParameter) gen.generateDerivedParameters(256)).getKey();
    }

    private static byte[] performCipherOperation(
            int mode, byte[] iv, byte[] encryptKey, byte[] text) throws CipherException {

        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");

            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptKey, "AES");
            cipher.init(mode, secretKeySpec, ivParameterSpec);
            return cipher.doFinal(text);
        } catch (Exception e) {
            throw new CipherException("Error performing cipher operation", e);
        }
    }

    private static byte[] generateMac(byte[] derivedKey, byte[] cipherText) {
        byte[] result = new byte[16 + cipherText.length];

        System.arraycopy(derivedKey, 16, result, 0, 16);
        System.arraycopy(cipherText, 0, result, 16, cipherText.length);

        return Hash.sha3(result);
    }

    public static String getPrivateKey(String password, OpbWalletFile walletFile) throws CipherException, UnsupportedEncodingException {
        return byteArrayToHexStr(getPrivateKeyByte(password, walletFile));
    }

    public static String getPriKey(String password, OpbWalletFile walletFile) throws CipherException, UnsupportedEncodingException {
        return new String(getPrivateKeyByte(password, walletFile), CHARSETS);
    }

    public static String getPriKeyTwo(String password, OpbWalletFile walletFile) throws CipherException, UnsupportedEncodingException {
        String privateKey = byteArrayToHexStr(getPrivateKeyByte(password, walletFile));
        if (!WalletUtils.isValidPrivateKey(privateKey)) {
            privateKey = new String(getPrivateKeyByte(password, walletFile), CHARSETS);
        }
        return privateKey;
    }


    public static byte[] getPrivateKeyByte(String password, OpbWalletFile walletFile) throws CipherException, UnsupportedEncodingException {

        validate(walletFile);

        OpbWalletFile.Crypto crypto = walletFile.getCrypto();

        byte[] mac = Numeric.hexStringToByteArray(crypto.getMac());
        byte[] iv = Numeric.hexStringToByteArray(crypto.getCipherparams().getIv());
        byte[] cipherText = Numeric.hexStringToByteArray(crypto.getCiphertext());

        byte[] derivedKey;

        OpbWalletFile.KdfParams kdfParams = crypto.getKdfparams();
        if (kdfParams instanceof OpbWalletFile.ScryptKdfParams) {
            OpbWalletFile.ScryptKdfParams scryptKdfParams =
                    (OpbWalletFile.ScryptKdfParams) crypto.getKdfparams();
            int dklen = scryptKdfParams.getDklen();
            int n = scryptKdfParams.getN();
            int p = scryptKdfParams.getP();
            int r = scryptKdfParams.getR();
            byte[] salt = Numeric.hexStringToByteArray(scryptKdfParams.getSalt());
            derivedKey = generateDerivedScryptKey(password.getBytes(CHARSETS), salt, n, r, p, dklen);
        } else if (kdfParams instanceof OpbWalletFile.Aes128CtrKdfParams) {
            OpbWalletFile.Aes128CtrKdfParams aes128CtrKdfParams =
                    (OpbWalletFile.Aes128CtrKdfParams) crypto.getKdfparams();
            int c = aes128CtrKdfParams.getC();
            String prf = aes128CtrKdfParams.getPrf();
            byte[] salt = Numeric.hexStringToByteArray(aes128CtrKdfParams.getSalt());

            derivedKey = generateAes128CtrDerivedKey(password.getBytes(CHARSETS), salt, c, prf);
        } else {
            throw new CipherException("Unable to deserialize params: " + crypto.getKdf());
        }

        byte[] derivedMac = generateMac(derivedKey, cipherText);

        if (!Arrays.equals(derivedMac, mac)) {
            throw new CipherException("Invalid password provided");
        }

        byte[] encryptKey = Arrays.copyOfRange(derivedKey, 0, 16);
        return performCipherOperation(Cipher.DECRYPT_MODE, iv, encryptKey, cipherText);
    }


    /*public static String byteToHex(byte[] bytes) {
        String strHex = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < bytes.length; n++) {
            strHex = Integer.toHexString(bytes[n] & 0xFF);
            sb.append((strHex.length() == 1) ? "0" + strHex : strHex);
        }
        return sb.toString().trim();
    }
*/
    public static String byteArrayToHexStr(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[byteArray.length * 2];
        for (int j = 0; j < byteArray.length; j++) {
            int v = byteArray[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


    public static byte[] hexToByte(String hex) {
        int m = 0, n = 0;
        int byteLen = hex.length() / 2;
        byte[] ret = new byte[byteLen];
        for (int i = 0; i < byteLen; i++) {
            m = i * 2 + 1;
            n = m + 1;
            int intVal = Integer.decode("0x" + hex.substring(i * 2, m) + hex.substring(m, n));
            ret[i] = Byte.valueOf((byte) intVal);
        }
        return ret;
    }

    public static String getPrivateKey(String password, String jsonPath) throws CipherException, IOException {
        return getPrivateKey(password, new File(jsonPath));
    }

    public static String getPrivateKey(String password, File jsonPath) throws CipherException, IOException {
        OpbWalletFile opbWalletFile = JsonFileUtil.readJsonFile(jsonPath);
        return getPrivateKey(password, opbWalletFile);
    }

    public static String getPrivateKeyByJson(String password, String input) throws CipherException, IOException {
        OpbWalletFile opbWalletFile = JsonUtils.jsonToPojo(input, OpbWalletFile.class);
        return getPrivateKey(password, opbWalletFile);
    }

    static void validate(OpbWalletFile walletFile) throws CipherException {
        OpbWalletFile.Crypto crypto = walletFile.getCrypto();

        if (walletFile.getVersion() != CURRENT_VERSION) {
            throw new CipherException("Wallet version is not supported");
        }

        if (!crypto.getCipher().equals(CIPHER)) {
            throw new CipherException("Wallet cipher is not supported");
        }

        if (!crypto.getKdf().equals(AES_128_CTR) && !crypto.getKdf().equals(SCRYPT)) {
            throw new CipherException("KDF type is not supported");
        }
    }

}
