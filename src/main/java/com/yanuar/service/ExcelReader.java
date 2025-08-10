package com.yanuar.service;

import com.yanuar.model.Contact;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelReader {

    /**
     * Read first sheet. Expect header in first row. Columns:
     * 0 -> CIFNO
     * 1 -> NAMA
     * 2 -> EMAIL
     * 3 -> TOTAL TABUNGAN
     */
    public List<Contact> read(File file) throws Exception {
        List<Contact> out = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(file);
             Workbook wb = new XSSFWorkbook(fis)) {

            Sheet sheet = wb.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            if (!rows.hasNext()) return out;
            rows.next(); // skip header

            while (rows.hasNext()) {
                Row r = rows.next();
                String cif = getStringCell(r.getCell(0));
                String nama = getStringCell(r.getCell(1));
                String email = getStringCell(r.getCell(2));
                BigDecimal total = getNumericCell(r.getCell(3));
                out.add(new Contact(cif, nama, email, total));
            }
        }
        return out;
    }

    private String getStringCell(Cell c) {
        if (c == null) return "";
        if (c.getCellType() == CellType.STRING) return c.getStringCellValue().trim();
        if (c.getCellType() == CellType.NUMERIC) {
            double v = c.getNumericCellValue();
            long lv = (long) v;
            return String.valueOf(lv);
        }
        return c.toString().trim();
    }

    private BigDecimal getNumericCell(Cell c) {
        if (c == null) return BigDecimal.ZERO;
        if (c.getCellType() == CellType.NUMERIC) return BigDecimal.valueOf(c.getNumericCellValue());
        String s = c.toString().replaceAll("[^0-9.,-]", "").replace(",", "");
        if (s.isEmpty()) return BigDecimal.ZERO;
        try {
            return new BigDecimal(s);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
