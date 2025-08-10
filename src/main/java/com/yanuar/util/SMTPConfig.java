package com.yanuar.util;

public class SMTPConfig {
    private String host;
    private int port;
    private String username;
    private String password; // may be empty -> ENV fallback
    private boolean useTls;
    private String from;

    public SMTPConfig() {}

    public SMTPConfig(String host, int port, String username, String password, boolean useTls, String from) {
        this.host = host; this.port = port; this.username = username; this.password = password; this.useTls = useTls; this.from = from;
    }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isUseTls() { return useTls; }
    public void setUseTls(boolean useTls) { this.useTls = useTls; }

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }
}
