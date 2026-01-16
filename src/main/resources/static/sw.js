importScripts('https://unpkg.com/idb@7/build/iife/index-min.js');

const CACHE_NAME = 'pomodori-cache-v3';
const STORE_NAME = 'offline-scans';

// ⚙️ Lista delle risorse da precaricare (cache statica)
const PRECACHE_URLS = [
  '/', '/home', '/scan', '/custom-login',
  '/admin', '/admin/dipendenti',
  '/css/style.css', '/js/script.js',
  '/manifest.json',
  '/icons/android-chrome-192x192.png',
  '/icons/android-chrome-512x512.png'
];

// Install: cache statica
self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME).then(cache => cache.addAll(PRECACHE_URLS))
  );
});

// Setup IndexedDB per richieste offline
const dbPromise = idb.openDB('pomodori-db', 1, {
  upgrade(db) {
    if (!db.objectStoreNames.contains(STORE_NAME)) {
		db.createObjectStore(STORE_NAME, { keyPath: 'id', autoIncrement: true });
    }
  }
});

// Intercetta fetch
self.addEventListener('fetch', event => {
  const { request } = event;

  // ❌ Escludi completamente GET e POST su /dipendente/login
  if (request.url.includes('/dipendente/login')) {
    return; // non intercettare né cache né sync
  }

  // 🔄 Salva POST /scan se offline
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

        await self.registration.sync.register('sync-scans');

        return new Response(JSON.stringify({ success: false, offline: true }), {
          headers: { 'Content-Type': 'application/json' }
        });
      })
    );
    return;
  }

  // ⚡️ Cache first, poi rete, poi fallback
  event.respondWith(
    caches.match(request).then(cached => {
      if (cached) return cached;

      return fetch(request)
        .then(response => {
          if (
            request.method === 'GET' &&
            response.status === 200 &&
            response.type === 'basic'
          ) {
            const responseClone = response.clone();
            caches.open(CACHE_NAME).then(cache => cache.put(request, responseClone));
          }
          return response;
        })
        .catch(() =>
          new Response('Offline o errore di rete', {
            status: 503,
            statusText: 'Service Unavailable'
          })
        );
    })
  );
});


// 🎯 Sync: invia le scansioni salvate quando torna la connessione
self.addEventListener('sync', event => {
  if (event.tag === 'sync-scans') {
    event.waitUntil(syncOfflineScans());
  }
});

async function syncOfflineScans() {
  const db = await dbPromise;
  const scans = await db.getAllFromIndex(STORE_NAME, ''); // ottieni tutte
  for (const [key, scan] of scans.entries()) {
    try {
      const response = await fetch('/scan', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(scan)
      });
      if (response.ok) {
        await db.delete(STORE_NAME, key);
      }
    } catch (err) {
      // fallisce → riprova al prossimo sync
    }
  }
}
