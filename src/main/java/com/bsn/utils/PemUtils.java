package com.bsn.utils;

import java.util.regex.Pattern;

/**
 * @Author wxq
 * @Description
 * @Date 2025/1/24 11:06
 * @Version 1.0
 */
public class PemUtils {
    // 定义 PEM 格式的私钥正则表达式
    private static final Pattern PEM_PRIVATE_KEY_PATTERN = Pattern.compile(
            "-----BEGIN PRIVATE KEY-----\\s*([A-Za-z0-9+/=\\s]+)\\s*-----END PRIVATE KEY-----"
    );

    /**
     * 判断输入的字符串是否为 PEM 格式的私钥
     *
     * @param pemString 输入的字符串
     * @return 如果是 PEM 格式的私钥，返回 true；否则返回 false
     */
    public static boolean isPemPrivateKey(String pemString) {
        if (pemString == null || pemString.isEmpty()) {
            return false;
        }
        return PEM_PRIVATE_KEY_PATTERN.matcher(pemString).matches();
    }
}
