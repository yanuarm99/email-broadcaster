package com.yanuar.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtil {

    public static String formatRupiah(BigDecimal value) {
        if (value == null) return "";
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return nf.format(value);
    }

    public static String terbilang(BigDecimal value) {
        if (value == null) return "";
        long v = value.longValue();
        String words = toWords(v).trim();
        if (words.isEmpty()) return "";
        // capitalize first letter
        words = words.substring(0,1).toUpperCase() + words.substring(1);
        return words + " Rupiah";
    }

    // internal
    private static String toWords(long n) {
        if (n == 0) return "nol";
        return convert(n).trim();
    }

    private static String convert(long n) {
        final String[] small = {"", "satu", "dua", "tiga", "empat", "lima", "enam", "tujuh", "delapan", "sembilan", "sepuluh", "sebelas"};
        if (n < 12) return small[(int) n];
        if (n < 20) return convert(n - 10) + " belas";
        if (n < 100) return convert(n / 10) + " puluh" + (n % 10 != 0 ? " " + convert(n % 10) : "");
        if (n < 200) return "seratus" + (n - 100 != 0 ? " " + convert(n - 100) : "");
        if (n < 1000) return convert(n / 100) + " ratus" + (n % 100 != 0 ? " " + convert(n % 100) : "");
        if (n < 2000) return "seribu" + (n - 1000 != 0 ? " " + convert(n - 1000) : "");
        if (n < 1000000) return convert(n / 1000) + " ribu" + (n % 1000 != 0 ? " " + convert(n % 1000) : "");
        if (n < 1000000000) return convert(n / 1000000) + " juta" + (n % 1000000 != 0 ? " " + convert(n % 1000000) : "");
        if (n < 1000000000000L) return convert(n / 1000000000) + " miliar" + (n % 1000000000 != 0 ? " " + convert(n % 1000000000) : "");
        if (n < 1000000000000000L) return convert(n / 1000000000000L) + " triliun" + (n % 1000000000000L != 0 ? " " + convert(n % 1000000000000L) : "");
        return "";
    }
}
