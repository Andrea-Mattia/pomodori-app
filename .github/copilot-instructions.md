# Copilot Custom Instructions - Pomodori App

Seguire rigorosamente queste linee guida durante lo sviluppo di questo progetto:

## 1. Workflow Modifiche e Git
- **Analisi**: Prima di ogni modifica, analizzare i file coinvolti e spiegare l'approccio.
- **Esecuzione Locale**: Applicare le modifiche ai file nel workspace.
- **Riepilogo Obbligatorio**: Dopo le modifiche, fornire un elenco di COSA è stato cambiato e PERCHÉ è stato fatto.
- **Conferma Utente**: **ATTENDERE SEMPRE** la conferma esplicita dell'utente prima di procedere con `git commit` e `git push`.
- **Messaggi di Commit**: Usare messaggi descrittivi e professionali in inglese (es: "Fix: ...", "Feat: ...", "Chore: ...").

## 2. PWA e Compatibilità iOS/Safari
- **Service Worker**: Evitare librerie esterne tramite CDN che effettuano redirect (es. unpkg). Usare la libreria `idb.min.js` salvata localmente in `/js/libs/`.
- **Sincronizzazione Offline**: Safari/iOS NON supporta `SyncManager`. Usare il sistema basato su `postMessage('sync-now')` in `script.js` attivato dall'evento `online` e intercettato dal Service Worker.
- **Meta Tags**: Mantenere sempre i meta tag PWA aggiornati (sia `apple-mobile-web-app-capable` che `mobile-web-app-capable`).

## 3. Backend e Database (Render.com)
- **Database**: L'app usa H2 su file per persistenza su Render Free Tier.
- **Inizializzazione**: Gestire il seed dei dati (ruoli, ecc.) tramite `src/main/resources/data.sql`.
- **Sintassi SQL**: Usare la sintassi H2 `MERGE INTO ... (colonne) KEY (chiave) VALUES (...)` per evitare errori di avvio o duplicati.
- **Spring Profile**: Il profilo attivo su Render è `render-h2`.

## 4. Frontend e UI
- **Autocomplete**: Tutti i campi form devono avere l'attributo `autocomplete` appropriato (`username`, `current-password`, `new-password`, `email`) per usability e password manager.
- **Stato Offline**: Mantenere il badge visuale in navbar per indicare lo stato del Service Worker e della connessione.
