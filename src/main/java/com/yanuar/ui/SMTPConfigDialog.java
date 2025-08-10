package com.yanuar.ui;

import com.yanuar.util.SMTPConfig;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class SMTPConfigDialog {
    private final SMTPConfig initial;

    public SMTPConfigDialog(SMTPConfig initial) { this.initial = initial; }

    public SMTPConfig showAndWait(Window owner) {
        Stage s = new Stage();
        s.initOwner(owner);
        s.initModality(Modality.APPLICATION_MODAL);
        s.setTitle("SMTP Config");

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
        g.addRow(2, new Label("User:"), user);
        g.addRow(3, new Label("Password:"), pass);
        g.addRow(4, new Label("From:"), from);
        g.add(tls, 1, 5);

        Button ok = new Button("Save");
        Button cancel = new Button("Cancel");
        ok.setDefaultButton(true);
        cancel.setCancelButton(true);
        g.addRow(6, ok, cancel);

        Scene scene = new Scene(g);
        s.setScene(scene);

        final SMTPConfig[] result = new SMTPConfig[1];
        ok.setOnAction(ev -> {
            SMTPConfig cfg = new SMTPConfig();
            cfg.setHost(host.getText().trim());
            try { cfg.setPort(Integer.parseInt(port.getText().trim())); } catch (Exception ex) { cfg.setPort(587); }
            cfg.setUsername(user.getText().trim());
            cfg.setPassword(pass.getText());
            cfg.setUseTls(tls.isSelected());
            cfg.setFrom(from.getText().trim());
            result[0] = cfg;
            s.close();
        });
        cancel.setOnAction(ev -> {
            result[0] = null;
            s.close();
        });

        s.showAndWait();
        return result[0];
    }
}
