package com.yanuar.util;

import java.io.File;

/**
 * Small helper for clearing cached files/paths.
 */
public class CacheUtil {
    // last excel path is stored in config.properties via ConfigUtil
    public static boolean clearLastExcelCache() {
        try {
            ConfigUtil.clearLastExcelPath();
            TemplateUtil.clearAllTemplates(); // 🔹 tambah hapus semua template juga
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteFileIfExists(String path) {
        if (path == null || path.isEmpty()) return false;
        File f = new File(path);
        if (!f.exists()) return false;
        return f.delete();
    }
}
