package com.yanuar.store;

import com.yanuar.model.Template;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * File-backed template store (templates.properties in project root).
 * Static API for simplicity.
 */
public class TemplateStore {
    private static final String FILE = "templates.properties";
    private static final Properties props = new Properties();

    static {
        load();
    }

    private static synchronized void load() {
        File f = new File(FILE);
        if (!f.exists()) return;
        try (FileInputStream fis = new FileInputStream(f)) {
            props.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static synchronized void save() throws IOException {
        try (FileOutputStream fos = new FileOutputStream(FILE)) {
            props.store(fos, "Email templates");
        }
    }

    public static synchronized Template add(String subject, String body) throws IOException {
        int next = Integer.parseInt(props.getProperty("nextId", "1"));
        props.setProperty("template." + next + ".subject", subject == null ? "" : subject);
        props.setProperty("template." + next + ".body", body == null ? "" : body);
        props.setProperty("nextId", String.valueOf(next + 1));
        save();
        return new Template(next, subject, body);
    }

    public static synchronized boolean delete(int id) {
        String sKey = "template." + id + ".subject";
        String bKey = "template." + id + ".body";
        boolean existed = props.containsKey(sKey) || props.containsKey(bKey);
        props.remove(sKey);
        props.remove(bKey);
        if (existed) {
            try {
                save();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return existed;
    }

    public static synchronized List<Template> getAll() {
        Set<Integer> ids = props.stringPropertyNames().stream()
                .filter(k -> k.startsWith("template.") && k.endsWith(".subject"))
                .map(k -> {
                    String mid = k.substring("template.".length(), k.length() - ".subject".length());
                    return Integer.parseInt(mid);
                }).collect(Collectors.toSet());
        List<Template> out = new ArrayList<>();
        for (Integer id : ids) {
            String subj = props.getProperty("template." + id + ".subject", "");
            String body = props.getProperty("template." + id + ".body", "");
            out.add(new Template(id, subj, body));
        }
        out.sort(Comparator.comparingInt(Template::getId));
        return out;
    }

    public static synchronized Template get(int id) {
        String subj = props.getProperty("template." + id + ".subject");
        String body = props.getProperty("template." + id + ".body");
        if (subj == null && body == null) return null;
        return new Template(id, subj == null ? "" : subj, body == null ? "" : body);
    }

    public static synchronized void clear() {
        props.clear();
        File f = new File(FILE);
        if (f.exists()) f.delete();
    }
}
