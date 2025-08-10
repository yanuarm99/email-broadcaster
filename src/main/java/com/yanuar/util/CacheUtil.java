package com.yanuar.util;

import java.io.*;
import java.util.Properties;

public class ConfigUtil {
    private static final String FILE = "config.properties";

    public static SMTPConfig load() {
        Properties p = new Properties();
        File f = new File(FILE);
        if (!f.exists()) return null;
        try (FileInputStream fis = new FileInputStream(f)) {
            p.load(fis);
            SMTPConfig cfg = new SMTPConfig();
            cfg.setHost(p.getProperty("smtp.host", ""));
            cfg.setPort(Integer.parseInt(p.getProperty("smtp.port", "587")));
            cfg.setUsername(p.getProperty("smtp.user", ""));
            cfg.setPassword(p.getProperty("smtp.password", ""));
            cfg.setUseTls(Boolean.parseBoolean(p.getProperty("smtp.ssl", "false")));
            cfg.setFrom(p.getProperty("smtp.from", cfg.getUsername()));
            return cfg;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void save(SMTPConfig cfg) throws IOException {
        Properties p = new Properties();
        p.setProperty("smtp.host", cfg.getHost() == null ? "" : cfg.getHost());
        p.setProperty("smtp.port", String.valueOf(cfg.getPort()));
        p.setProperty("smtp.user", cfg.getUsername() == null ? "" : cfg.getUsername());
        p.setProperty("smtp.password", cfg.getPassword() == null ? "" : cfg.getPassword());
        p.setProperty("smtp.ssl", String.valueOf(cfg.isUseTls()));
        p.setProperty("smtp.from", cfg.getFrom() == null ? "" : cfg.getFrom());
        // preserve last.excel.path if exists
        File f = new File(FILE);
        if (f.exists()) {
            Properties old = new Properties();
            try (FileInputStream fis = new FileInputStream(f)) {
                old.load(fis);
            } catch (IOException ignore) {}
            String last = old.getProperty("last.excel.path");
            if (last != null) p.setProperty("last.excel.path", last);
        }
        try (FileOutputStream fos = new FileOutputStream(f)) {
            p.store(fos, "SMTP Configuration");
        }
    }

    public static void setLastExcelPath(String path) throws IOException {
        File f = new File(FILE);
        Properties p = new Properties();
        if (f.exists()) {
            try (FileInputStream fis = new FileInputStream(f)) { p.load(fis); }
        }
        if (path == null) p.remove("last.excel.path"); else p.setProperty("last.excel.path", path);
        try (FileOutputStream fos = new FileOutputStream(f)) { p.store(fos, "config"); }
    }

    public static String getLastExcelPath() {
        File f = new File(FILE);
        if (!f.exists()) return null;
        try (FileInputStream fis = new FileInputStream(f)) {
            Properties p = new Properties();
            p.load(fis);
            return p.getProperty("last.excel.path");
        } catch (IOException e) { return null; }
    }

    public static void clearLastExcelPath() throws IOException {
        setLastExcelPath(null);
    }
}
