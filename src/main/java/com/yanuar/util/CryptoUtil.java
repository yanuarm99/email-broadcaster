package com.yanuar.util;

import java.util.Base64;

public class CryptoUtil {
    public static String encode(String plain) {
        if (plain == null) return "";
        return Base64.getEncoder().encodeToString(plain.getBytes());
    }

    public static String decode(String encoded) {
        if (encoded == null) return "";
        try {
            byte[] b = Base64.getDecoder().decode(encoded);
            return new String(b);
        } catch (Exception e) {
            return "";
        }
    }
}
