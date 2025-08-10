package com.yanuar.model;

import java.math.BigDecimal;
import com.yanuar.util.CurrencyUtil;

public class Contact {
    private String cifno;
    private String nama;
    private String email;
    private BigDecimal totalTabungan;

    public Contact() {
        this("", "", "", BigDecimal.ZERO);
    }

    public Contact(String cifno, String nama, String email, BigDecimal totalTabungan) {
        this.cifno = cifno == null ? "" : cifno;
        this.nama = nama == null ? "" : nama;
        this.email = email == null ? "" : email;
        this.totalTabungan = totalTabungan == null ? BigDecimal.ZERO : totalTabungan;
    }

    public String getCifno() { return cifno; }
    public void setCifno(String cifno) { this.cifno = cifno; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public BigDecimal getTotalTabungan() { return totalTabungan; }
    public void setTotalTabungan(BigDecimal totalTabungan) { this.totalTabungan = totalTabungan; }

    // formatting helper
    public String getTotalTabunganRupiah() {
        return CurrencyUtil.formatRupiah(totalTabungan);
    }

    public String getTotalTabunganTerbilang() {
        return CurrencyUtil.terbilang(totalTabungan);
    }

    @Override
    public String toString() {
        return "Contact{" + cifno + "," + nama + "," + email + "," + totalTabungan + "}";
    }
}
