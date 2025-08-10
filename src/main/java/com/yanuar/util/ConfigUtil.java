package com.yanuar.util;

import java.io.FileInputStream;
import java.util.Properties;
public class Config {
    private static final Properties p = new Properties();
    static {
        try {
            FileInputStream f = new FileInputStream("config.properties");
            p.load(f);
            f.close();
        } catch (Exception e) {}
    }
    public static String get(String k, String def){
        String v = System.getenv(k.toUpperCase());
        if(v != null && !v.isEmpty()) return v;
        v = p.getProperty(k);
        return v != null ? v : def;
    }
}