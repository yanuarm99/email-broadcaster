package com.yanuar.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Simple config util that reads/writes config.properties in project root.
 * Supports values like ${ENV_VAR} which will be resolved via System.getenv.
 */
public class ConfigUtil {
    private static final String CONFIG_FILE = "config.properties";
    private static final Path CONFIG_PATH = Paths.get(CONFIG_FILE);
    private static final Properties props = new Properties();

    static {
        load();
    }

    private static synchronized void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (InputStream in = new FileInputStream(CONFIG_PATH.toFile())) {
                props.load(in);
            } catch (IOException e) {
                System.err.println("ConfigUtil: failed to load config.properties: " + e.getMessage());
            }
        } else {
            // create empty file so set() works later
            try {
                Files.createFile(CONFIG_PATH);
            } catch (IOException ignored) {}
        }
    }

    private static synchronized void save() throws IOException {
        try (OutputStream out = new FileOutputStream(CONFIG_PATH.toFile())) {
            props.store(out, "config.properties");
        }
    }

    /** Get property with default. Resolves ${ENV_VAR} if present. */
    public static synchronized String get(String key, String defaultValue) {
        String v = props.getProperty(key, defaultValue);
        if (v == null) return defaultValue;
        v = v.trim();
        if (v.startsWith("${") && v.endsWith("}")) {
            String envKey = v.substring(2, v.length() - 1);
            String env = System.getenv(envKey);
            return env != null ? env : defaultValue;
        }
        return v;
    }

    /** Set property and persist to file. */
    public static synchronized void set(String key, String value) throws IOException {
        props.setProperty(key, value == null ? "" : value);
        save();
    }

    public static synchronized void clearLastExcelPath() throws IOException {
        props.remove("last.excel.path");
        save();
    }

    public static synchronized String getLastExcelPath() {
        return props.getProperty("last.excel.path", "");
    }

    public static synchronized void setLastExcelPath(String path) throws IOException {
        set("last.excel.path", path == null ? "" : path);
    }
}
