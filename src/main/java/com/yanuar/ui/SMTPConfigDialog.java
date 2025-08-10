package com.yanuar.ui;

import com.yanuar.util.ConfigUtil;
import com.yanuar.util.SMTPConfig;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;

public class SMTPConfigDialog {

    /** Show dialog, return saved SMTPConfig (or null if cancel). */
    public SMTPConfig showAndWait(Window owner) {
        SMTPConfig initial = loadFromConfig();

        Dialog<SMTPConfig> dlg = new Dialog<>();
        dlg.setTitle("SMTP Config");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane g = new GridPane();
        g.setHgap(8);
        g.setVgap(8);
        g.setPadding(new Insets(10));

        TextField host = new TextField(initial == null ? "smtp.gmail.com" : initial.getHost());
        TextField port = new TextField(initial == null ? "587" : String.valueOf(initial.getPort()));
        TextField user = new TextField(initial == null ? "" : initial.getUsername());
        PasswordField pass = new PasswordField();
        pass.setText(initial == null ? "" : initial.getPassword());
        CheckBox tls = new CheckBox("Use TLS (STARTTLS)");
        tls.setSelected(initial == null ? true : initial.isUseTls());
        TextField from = new TextField(initial == null ? "" : initial.getFrom());

        g.addRow(0, new Label("Host:"), host);
        g.addRow(1, new Label("Port:"), port);
        g.addRow(2, new Label("User (email):"), user);
        g.addRow(3, new Label("Password (leave blank to use env SMTP_PASSWORD):"), pass);
        g.addRow(4, new Label("TLS:"), tls);
        g.addRow(5, new Label("From (override):"), from);

        dlg.getDialogPane().setContent(g);

        dlg.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                SMTPConfig cfg = new SMTPConfig();
                cfg.setHost(host.getText().trim());
                try { cfg.setPort(Integer.parseInt(port.getText().trim())); } catch (Exception e) { cfg.setPort(587); }
                cfg.setUsername(user.getText().trim());
                cfg.setPassword(pass.getText()); // may be empty -> handled later
                cfg.setUseTls(tls.isSelected());
                cfg.setFrom(from.getText().trim());
                // persist to config.properties
                try {
                    ConfigUtil.set("smtp.host", cfg.getHost());
                    ConfigUtil.set("smtp.port", String.valueOf(cfg.getPort()));
                    ConfigUtil.set("smtp.user", cfg.getUsername());
                    // store password literally only if not blank (otherwise we keep blank to use ENV)
                    if (cfg.getPassword() != null && !cfg.getPassword().isEmpty()) {
                        ConfigUtil.set("smtp.password", cfg.getPassword());
                    } else {
                        // store placeholder empty (so get() can fallback to env)
                        ConfigUtil.set("smtp.password", "");
                    }
                    ConfigUtil.set("smtp.ssl", cfg.isUseTls() ? "false" : "true"); // follow older convention
                    ConfigUtil.set("smtp.from", cfg.getFrom());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return cfg;
            }
            return null;
        });

        dlg.initOwner(owner);
        return dlg.showAndWait().orElse(null);
    }

    private SMTPConfig loadFromConfig() {
        SMTPConfig c = new SMTPConfig();
        c.setHost(ConfigUtil.get("smtp.host", "smtp.gmail.com"));
        c.setPort(Integer.parseInt(ConfigUtil.get("smtp.port", "587")));
        c.setUsername(ConfigUtil.get("smtp.user", ""));
        // If config value is a ${ENV}, ConfigUtil.get will resolve it. If blank, password is empty -> ENV fallback.
        c.setPassword(ConfigUtil.get("smtp.password", ""));
        c.setUseTls(!ConfigUtil.get("smtp.ssl", "false").equalsIgnoreCase("true"));
        c.setFrom(ConfigUtil.get("smtp.from", c.getUsername()));
        return c;
    }
}
