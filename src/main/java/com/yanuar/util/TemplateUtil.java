package com.yanuar.util;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.json.JSONObject;

public class TemplateUtil {

    private static final String TEMPLATE_FOLDER_NAME = "Template Message (from Email-Broadcaster App)";
    private static final Path TEMPLATE_FOLDER_PATH;

    static {
        String basePath = System.getenv("MESSAGE_TEMPLATE");
        if (basePath == null || basePath.isBlank()) {
            basePath = System.getProperty("user.dir"); // fallback ke current dir
        }
        TEMPLATE_FOLDER_PATH = Paths.get(basePath, TEMPLATE_FOLDER_NAME);
        createFolderIfNotExists();
    }

    private static void createFolderIfNotExists() {
        try {
            if (!Files.exists(TEMPLATE_FOLDER_PATH)) {
                Files.createDirectories(TEMPLATE_FOLDER_PATH);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getTemplateList() {
        List<String> templates = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(TEMPLATE_FOLDER_PATH, "*.json")) {
            for (Path file : stream) {
                templates.add(file.getFileName().toString().replace(".json", ""));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return templates;
    }

    public static void saveTemplate(String name, String subject, String body) throws IOException {
        JSONObject json = new JSONObject();
        json.put("subject", subject);
        json.put("body", body);

        Path filePath = TEMPLATE_FOLDER_PATH.resolve(name + ".json");
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write(json.toString(4)); // pretty print
        }
    }

    public static Map<String, String> loadTemplate(String name) {
        Map<String, String> template = new HashMap<>();
        Path filePath = TEMPLATE_FOLDER_PATH.resolve(name + ".json");
        if (!Files.exists(filePath)) return template;

        try {
            String content = Files.readString(filePath);
            JSONObject json = new JSONObject(content);
            template.put("subject", json.optString("subject", ""));
            template.put("body", json.optString("body", ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return template;
    }

    public static boolean deleteTemplate(String name) {
        Path filePath = TEMPLATE_FOLDER_PATH.resolve(name + ".json");
        try {
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void clearAllTemplates() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(TEMPLATE_FOLDER_PATH, "*.json")) {
            for (Path file : stream) {
                Files.deleteIfExists(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
