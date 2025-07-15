importScripts('https://unpkg.com/idb@7/build/iife/index-min.js');

const CACHE = 'pomodori-cache-v1';
const STORE_NAME = 'offline-scans';

// Cache static resources
self.addEventListener('install', e => {
  e.waitUntil(
    caches.open(CACHE).then(c => c.addAll([
      '/',
      '/home',
      '/scan',
      '/dipendente/login',
      '/custom-login',
      '/admin',
      '/admin/dipendenti',
      '/css/style.css',
      '/js/script.js',
      '/manifest.json',
      '/icons/android-chrome-192x192.png',
      '/icons/android-chrome-512x512.png'
    ]))
  );
});

// Setup IndexedDB
const dbPromise = idb.openDB('pomodori-db', 1, {
  upgrade(db) {
    if (!db.objectStoreNames.contains(STORE_NAME)) {
      db.createObjectStore(STORE_NAME, { keyPath: 'id', autoIncrement: true });
    }
  }
});

// Intercetta le richieste
self.addEventListener('fetch', event => {
  const { request } = event;

  // Se è una POST a /scan salva offline
  if (request.method === 'POST' && request.url.endsWith('/scan')) {
    event.respondWith(
      fetch(request).catch(async () => {
        const formData = await request.clone().formData();
        const json = {};
        for (const [key, value] of formData.entries()) {
          json[key] = value;
        }

        const db = await dbPromise;
        await db.add(STORE_NAME, json);

        // Registra sync per quando torna online
        await self.registration.sync.register('sync-scans');

        return new Response(JSON.stringify({ success: false, offline: true }), {
          headers: { 'Content-Type': 'application/json' }
        });
      })
    );
    return;
  }

  // Per tutto il resto: cache first
  event.respondWith(
    caches.match(request).then(cached => cached || fetch(request))
  );
});

// Sync handler per inviare dati salvati
self.addEventListener('sync', event => {
  if (event.tag === 'sync-scans') {
    event.waitUntil(syncOfflineScans());
  }
});

async function syncOfflineScans() {
  const db = await dbPromise;
  const scans = await db.getAll(STORE_NAME);

  for (const scan of scans) {
    try {
      const response = await fetch('/scan', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(scan)
      });

	  if (response.ok && scan.id !== undefined) {
	    await db.delete(STORE_NAME, scan.id);
	  }
    } catch (e) {
      // rimanda al prossimo sync
    }
  }
}
