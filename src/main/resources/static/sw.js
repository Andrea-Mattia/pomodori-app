importScripts('https://unpkg.com/idb@7/build/iife/index-min.js');

const CACHE_NAME = 'pomodori-cache-v4';
const STORE_NAME = 'offline-scans';

// ⚙️ Lista delle risorse da precaricare (cache statica) - Include bootstrap e risorse scan
const PRECACHE_URLS = [
  '/',
  '/home',
  '/scan',
  '/custom-login',
  '/dipendente/login',
  '/admin',
  '/admin/dipendenti',
  '/admin/settings',
  '/css/style.css',
  '/js/script.js',
  '/js/session-timeout.js',
  '/sounds/alert.mp3',
  '/manifest.json',
  '/icons/android-chrome-192x192.png',
  '/icons/android-chrome-512x512.png',
  'https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css',
  'https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js',
  'https://cdn.jsdelivr.net/npm/bootstrap-icons@1.13.1/font/bootstrap-icons.min.css'
];

// Install: cache statica
self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME).then(cache => cache.addAll(PRECACHE_URLS))
  );
  self.skipWaiting();
});

// Activate: pulizia vecchie cache
self.addEventListener('activate', event => {
  event.waitUntil(
    caches.keys().then(cacheNames => {
      return Promise.all(
        cacheNames.filter(name => name !== CACHE_NAME)
          .map(name => caches.delete(name))
      );
    })
  );
  self.clients.claim();
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
  const url = new URL(request.url);

  // 🔄 Salva POST /scan se offline
  if (request.method === 'POST' && url.pathname.endsWith('/scan')) {
    event.respondWith(
      fetch(request).catch(async () => {
        const formData = await request.clone().formData();
        const json = {};
        for (const [key, value] of formData.entries()) {
          json[key] = value;
        }

        const db = await dbPromise;
        await db.add(STORE_NAME, json);

        if (self.registration.sync) {
          await self.registration.sync.register('sync-scans');
        }

        return new Response(JSON.stringify({ success: false, offline: true }), {
          headers: { 'Content-Type': 'application/json' }
        });
      })
    );
    return;
  }

  // Strategia differenziata:
  // 1. Navigation/HTML -> Network falling back to cache
  // 2. Static Assets -> Cache first falling back to network

  const isNavigation = request.mode === 'navigate' ||
    (request.method === 'GET' && request.headers.get('accept') && request.headers.get('accept').includes('text/html'));

  if (isNavigation) {
    event.respondWith(
      fetch(request)
        .then(response => {
          if (response.status === 200) {
            const responseClone = response.clone();
            caches.open(CACHE_NAME).then(cache => cache.put(request, responseClone));
          }
          return response;
        })
        .catch(() => {
          // Fallback alla cache se offline - gestisce params per la pagina scan
          return caches.match(request, { ignoreSearch: url.pathname.includes('/scan') })
            .then(cached => cached || new Response('Contenuto non disponibile offline', {
              status: 503,
              statusText: 'Offline'
            }));
        })
    );
  } else {
    // Assets statici: Cache-First
    event.respondWith(
      caches.match(request).then(cached => {
        return cached || fetch(request).then(response => {
          if (response.status === 200) {
            const responseClone = response.clone();
            caches.open(CACHE_NAME).then(cache => cache.put(request, responseClone));
          }
          return response;
        });
      })
    );
  }
});


// 🎯 Sync: invia le scansioni salvate quando torna la connessione
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
      // Rimuoviamo l'id per evitare conflitti lato server se presente
      const { id, ...scanData } = scan;
      const response = await fetch('/scan', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(scanData)
      });
      if (response.ok) {
        await db.delete(STORE_NAME, scan.id);
      }
    } catch (err) {
      console.error('Errore durante la sincronizzazione della scansione:', err);
    }
  }
}
