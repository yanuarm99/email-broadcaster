package com.yanuar.ui;

import com.yanuar.model.Contact;
import com.yanuar.model.Template;
import com.yanuar.service.EmailService;
import com.yanuar.service.ExcelReader;
import com.yanuar.store.TemplateStore;
import com.yanuar.util.CacheUtil;
import com.yanuar.util.ConfigUtil;
import com.yanuar.util.CurrencyUtil;
import com.yanuar.util.SMTPConfig;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class MainFrameController {
    private TableView<Contact> table;
    private List<Contact> contacts;
    private ProgressBar progressBar;

    public MainFrameController() {}

    public BorderPane getView() {
        BorderPane root = new BorderPane();

        table = new TableView<>();

        TableColumn<Contact, String> colCif = new TableColumn<>("CIFNO");
        colCif.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCifno()));

        TableColumn<Contact, String> colNama = new TableColumn<>("NAMA");
        colNama.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNama()));

        TableColumn<Contact, String> colEmail = new TableColumn<>("EMAIL");
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));

        TableColumn<Contact, String> colTotal = new TableColumn<>("TOTAL TABUNGAN");
        colTotal.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTotalTabunganRupiah()));

        TableColumn<Contact, String> colKet = new TableColumn<>("KETERANGAN");
        colKet.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTotalTabunganTerbilang()));

        // add exactly 5 columns
        table.getColumns().addAll(colCif, colNama, colEmail, colTotal, colKet);

        Button btnLoad = new Button("Load Excel");
        Button btnCfg = new Button("SMTP Config");
        Button btnDeleteCache = new Button("Delete Cache");
        Button btnTemplateMsg = new Button("Template Message");
        Button btnSend = new Button("Send Broadcast");

        btnLoad.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls"));
            File f = fc.showOpenDialog(null);
            if (f != null) {
                try {
                    List<Contact> list = new ExcelReader().read(f);
                    contacts = list;
                    table.getItems().setAll(list);
                    ConfigUtil.setLastExcelPath(f.getAbsolutePath());
                } catch (Exception ex) {
                    showAlert("Error load Excel: " + ex.getMessage());
                }
            }
        });

        btnCfg.setOnAction(e -> {
            SMTPConfigDialog dlg = new SMTPConfigDialog();
            dlg.showAndWait(null);
        });

        btnDeleteCache.setOnAction(e -> {
            if (table != null) table.getItems().clear();
            CacheUtil.clearLastExcelCache();
            TemplateStore.clear();
            showAlert("Cache cleared.");
        });

        btnTemplateMsg.setOnAction(e -> openTemplateManager());

        btnSend.setOnAction(e -> sendBroadcast());

        HBox header = new HBox(10, btnLoad, btnCfg, btnDeleteCache, btnTemplateMsg, btnSend);
        header.setPadding(new Insets(10));

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(Double.MAX_VALUE);

        VBox center = new VBox(table);

        root.setTop(header);
        root.setCenter(center);
        root.setBottom(progressBar);

        return root;
    }

    private void openTemplateManager() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Template Message Manager");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

        ListView<Template> lv = new ListView<>();
        lv.setPrefWidth(350);
        lv.getItems().setAll(TemplateStore.getAll());

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setPadding(new Insets(10));

        TextField subjectField = new TextField();
        subjectField.setPromptText("Subject");

        TextArea bodyArea = new TextArea();
        bodyArea.setPromptText("Body (HTML allowed). Use placeholders {NAMA}, {CIFNO}, {TOTAL_TABUNGAN}, {TERBILANG}");
        bodyArea.setPrefRowCount(12);
        bodyArea.setPrefColumnCount(40);

        Button btnAdd = new Button("Save / Add");
        Button btnDelete = new Button("Delete Selected");

        lv.getSelectionModel().selectedItemProperty().addListener((obs, oldT, newT) -> {
            if (newT != null) {
                subjectField.setText(newT.getSubject());
                bodyArea.setText(newT.getBody());
            } else {
                subjectField.clear();
                bodyArea.clear();
            }
        });

        btnAdd.setOnAction(e -> {
            try {
                String subj = subjectField.getText();
                String body = bodyArea.getText();
                if ((subj == null || subj.trim().isEmpty()) && (body == null || body.trim().isEmpty())) {
                    showAlert("Subject atau Body harus diisi.");
                    return;
                }
                Template t = TemplateStore.add(subj, body);
                lv.getItems().setAll(TemplateStore.getAll());
                lv.getSelectionModel().select(t);
                showAlert("Template tersimpan.");
            } catch (Exception ex) {
                showAlert("Gagal simpan template: " + ex.getMessage());
            }
        });

        btnDelete.setOnAction(e -> {
            Template sel = lv.getSelectionModel().getSelectedItem();
            if (sel == null) {
                showAlert("Pilih template dulu.");
                return;
            }
            boolean ok = TemplateStore.delete(sel.getId());
            if (ok) {
                lv.getItems().setAll(TemplateStore.getAll());
                subjectField.clear();
                bodyArea.clear();
                showAlert("Template dihapus.");
            } else {
                showAlert("Gagal menghapus template.");
            }
        });

        grid.add(new Label("Subject:"), 0, 0);
        grid.add(subjectField, 1, 0);
        grid.add(new Label("Body:"), 0, 1);
        grid.add(bodyArea, 1, 1);
        grid.add(btnAdd, 1, 2);
        grid.add(btnDelete, 1, 3);

        HBox content = new HBox(12, lv, grid);
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    private void sendBroadcast() {
        if (contacts == null || contacts.isEmpty()) {
            showAlert("Load Excel dulu.");
            return;
        }
        List<Template> templates = TemplateStore.getAll();
        if (templates.isEmpty()) {
            showAlert("Tidak ada template. Tambahkan dulu.");
            return;
        }
        ChoiceDialog<Template> choice = new ChoiceDialog<>(templates.get(0), templates);
        choice.setTitle("Pilih Template");
        choice.setHeaderText("Pilih template yang akan dipakai");
        Optional<Template> sel = choice.showAndWait();
        if (!sel.isPresent()) return;
        Template t = sel.get();

        // build SMTPConfig from ConfigUtil
        SMTPConfig cfg = new SMTPConfig();
        cfg.setHost(ConfigUtil.get("smtp.host", "smtp.gmail.com"));
        cfg.setPort(Integer.parseInt(ConfigUtil.get("smtp.port", "587")));
        cfg.setUsername(ConfigUtil.get("smtp.user", ""));
        cfg.setPassword(ConfigUtil.get("smtp.password", ""));
        cfg.setUseTls(!ConfigUtil.get("smtp.ssl", "false").equalsIgnoreCase("true"));
        cfg.setFrom(ConfigUtil.get("smtp.from", cfg.getUsername()));

        progressBar.setProgress(-1);
        new Thread(() -> {
            try {
                EmailService es = new EmailService(cfg);
                es.sendBulk(contacts, t.getSubject(), t.getBody(), (done, total) ->
                        Platform.runLater(() -> progressBar.setProgress((double) done / total))
                );
                Platform.runLater(() -> {
                    progressBar.setProgress(0);
                    showAlert("All sent!");
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    progressBar.setProgress(0);
                    showAlert("Send failed: " + ex.getMessage());
                });
            }
        }).start();
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.showAndWait();
    }
}
