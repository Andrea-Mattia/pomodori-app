# 🍅 Raccolta Pomodori

Applicazione web per la registrazione delle presenze tramite **scansione QR code** in ingresso/uscita campo.  
Ogni dipendente può autenticarsi e registrare la presenza.
È presente una parte di registrazione admin con consultazione delle scansioni effettuate, filtri di ricerca, export in CSV
e gestione dei dipendenti (registrazione, modifica, eliminazione).
È installabile come **Progressive Web App (PWA)**.

---

## 🧰 Stack Tecnologico

- **Backend**: Java 21 + Spring Boot 3.3.x
- **Frontend**: Thymeleaf + Bootstrap 5
- **Database**: PostgreSQL
- **Security**: Spring Security (Admin + Dipendente)
- **Hosting**: Render.com
- **Altro**: Service Worker, Web App Manifest, QR Code Scanner (via Html5Qrcode)

---

## 🚀 Avvio locale in HTTPS

Per testare le funzionalità PWA (come l’installazione) serve HTTPS anche in locale. Accedi all'app tramite **https://localhost:8080**

### 1. Genera un keystore locale:

```bash
keytool -genkeypair \
  -alias pomodori-local \
  -keyalg RSA \
  -keysize 2048 \
  -validity 365 \
  -storetype PKCS12 \
  -keystore keystore.p12 \
  -storepass password \
  -keypass password \
  -dname "CN=localhost, OU=Pomodori, O=Example, L=Città, ST=Italia, C=IT"
