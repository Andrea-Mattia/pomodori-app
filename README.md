# 🍅 Raccolta Pomodori

Applicazione web per la registrazione delle presenze tramite **scansione QR code** in ingresso/uscita campo.  
Ogni dipendente può autenticarsi e registrare la presenza.
È presente una parte di registrazione admin con consultazione delle scansioni effettuate, filtri di ricerca, export in CSV
e gestione dei dipendenti (registrazione, modifica, eliminazione).
È installabile come **Progressive Web App (PWA)**.

---

## 🚀 Avvio Locale (Testing)

Per testare l'applicazione sul tuo computer senza dover configurare Oracle o PostgreSQL:

1. **Prerequisiti**: Java 21 installato.
2. **Esecuzione**: Apri il terminale nella root del progetto e lancia:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
   ```
3. **Database H2**: 
   - L'applicazione userà un database in memoria (H2).
   - Puoi accedere alla console H2 su: `http://localhost:8080/h2-console`
   - JDBC URL: `jdbc:h2:mem:pomodoridb` | User: `sa` | Pass: `password`
4. **Login iniziale**:
   - Vai su `http://localhost:8080/register` per creare il primo account Amministratore.
   - Per i dipendenti, una volta loggato come admin, potrai registrarli dalla sezione "Gestione Dipendenti".

## 📱 Test PWA e Offline

- **Localhost**: I Service Worker funzionano su `localhost` anche senza HTTPS.
- **Mobile**: Per installarla sul telefono o testare l'offline REALE servirebbe HTTPS. Ho predisposto la configurazione SSL in `application-local.yml` (attualmente commentata) che richiede un file `keystore.p12`.
- **Funzionamento Offline**: 
  - Effettua il login come dipendente mentre sei online.
  - Spegni la connessione.
  - Scansiona un QR (es. `https://esempio.it?qr=VALORE`).
  - L'app salverà i dati localmente e li invierà al server non appena riaccenderai la rete.

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
