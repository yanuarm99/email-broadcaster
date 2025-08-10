# Java Email Broadcaster App

Aplikasi Java sederhana untuk mengirim broadcast email secara massal dengan template yang bisa disesuaikan. Cocok untuk kebutuhan email marketing, pengumuman, atau komunikasi massal lainnya.

---

## Fitur

- Mengirim email ke banyak kontak sekaligus.
- Support template email dengan placeholder yang dapat diisi data dinamis.
- Membaca data kontak dari file Excel atau sumber lain.
- Pengaturan SMTP yang mudah untuk berbagai layanan email.
- Logging hasil pengiriman email.
- Simple dan mudah dikembangkan.

---

## Struktur Project

- `model` — Berisi class model seperti `Contact` dan `Template`.
- `service` — Service pengirim email dan pembaca file Excel.
- `store` — Tempat penyimpanan template dan data sementara.
- `ui` — User Interface (jika ada) untuk mengelola template dan kontak.
- `util` — Utility helper seperti konfigurasi dan cache.

---

## Cara Pakai

1. **Konfigurasi SMTP**  
   Atur setting SMTP di file konfigurasi (`config.properties` atau environment variables), seperti host, port, username, password.

2. **Siapkan Data Kontak**  
   Siapkan file Excel yang berisi daftar email dan data terkait kontak.

3. **Buat Template Email**  
   Buat template email dengan placeholder, misal:  
   
   Halo {nama}, 
   Ini adalah broadcast email dari kami.