package com.yanuar.model;

public class Template {
    private int id;
    private String subject;
    private String body;

    public Template() {}

    public Template(int id, String subject, String body) {
        this.id = id;
        this.subject = subject == null ? "" : subject;
        this.body = body == null ? "" : body;
    }

    public Template(String subject, String body) {
        this(0, subject, body);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    @Override
    public String toString() {
        String s = (subject == null || subject.trim().isEmpty()) ? "(no subject)" : subject;
        return "[" + id + "] " + s;
    }
}
