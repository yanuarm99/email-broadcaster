package com.yanuar.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;
import com.yanuar.model.Contact;
import com.yanuar.service.ExcelReader;
import com.yanuar.service.EmailService;
import com.formdev.flatlaf.FlatLightLaf;

public class MainFrame extends JFrame {
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"CIFNO","NAMA","EMAIL","TOTAL TABUNGAN"},0);
    private List<Contact> contacts;

    public MainFrame() {
        FlatLightLaf.setup();
        setTitle("Broadcast Email");
        setSize(900,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        init();
    }

    private void init(){
        JTable table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        JButton load = new JButton("Load Excel");
        JButton cfg = new JButton("SMTP Config");
        JButton send = new JButton("Send Broadcast");
        JButton preview = new JButton("Preview Selected");
        JProgressBar progress = new JProgressBar();

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(load); top.add(cfg); top.add(preview); top.add(send);

        add(top, BorderLayout.NORTH);
        add(sp, BorderLayout.CENTER);
        add(progress, BorderLayout.SOUTH);

        load.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files", "xlsx"));
            int r = fc.showOpenDialog(this);
            if (r == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                try {
                    contacts = ExcelReader.read(f);
                    model.setRowCount(0);
                    for (Contact c : contacts) model.addRow(new Object[]{c.cif,c.nama,c.email,c.totalTabungan});
                    JOptionPane.showMessageDialog(this, "Loaded: "+contacts.size());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage());
                }
            }
        });

        cfg.addActionListener(e -> {
            SMTPConfigDialog d = new SMTPConfigDialog(this);
            d.setVisible(true);
        });

        preview.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0) {
                String msg = String.format("To: %s\nName: %s\nTotal: %s", model.getValueAt(r,2), model.getValueAt(r,1), model.getValueAt(r,3));
                JOptionPane.showMessageDialog(this, msg);
            }
        });

        send.addActionListener(e -> {
            if (contacts == null || contacts.isEmpty()) { JOptionPane.showMessageDialog(this, "Load Excel dulu."); return; }
            String subj = JOptionPane.showInputDialog(this, "Subject (use {NAMA} {CIF} placeholders):", "Info Tabungan");
            String body = JOptionPane.showInputDialog(this, "Body (HTML OK, use {NAMA} and {TOTAL}):", "Halo {NAMA}, saldo Anda {TOTAL}");
            new Thread(() -> {
                try {
                    EmailService es = new EmailService();
                    progress.setIndeterminate(true);
                    es.sendPersonalized(contacts, subj, body);
                    progress.setIndeterminate(false);
                    JOptionPane.showMessageDialog(this, "All sent!");
                } catch (Exception ex) {
                    progress.setIndeterminate(false);
                    JOptionPane.showMessageDialog(this, "Send failed: "+ex.getMessage());
                }
            }).start();
        });
    }
}