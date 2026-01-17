// v13 - Local library import for Safari compatibility
importScripts('/js/libs/idb.min.js');

const CACHE_NAME = 'pomodori-cache-v14';
const STORE_NAME = 'offline-scans';
const LOGOUT_URLS = ['/logout', '/custom-logout', '/dipendente/logout'];

// ⚙️ Lista delle risorse da precaricare (cache statica) - Usiamo solo URL locali per ora
const PRECACHE_URLS = [
  '/',
  '/home',
  '/scan',
  '/custom-login',
  '/dipendente/login',
  '/css/style.css',
  '/js/script.js',
  '/js/session-timeout.js',
  '/sounds/alert.mp3',
  '/manifest.json',
  '/icons/android-chrome-192x192.png',
  '/icons/android-chrome-512x512.png',
  '/icons/apple-touch-icon.png'
];

// Install: cache statica individuale
self.addEventListener('install', event => {
  self.skipWaiting();
  event.waitUntil(
    caches.open(CACHE_NAME).then(async cache => {
      for (const url of PRECACHE_URLS) {
        try {
          const response = await fetch(url);
          if (response.ok) await cache.put(url, response);
        } catch (e) {
          console.warn(`Precache fallito per ${url}:`, e);
        }
      }
    })
  );
});

// Activate: pulizia vecchie cache e tentativo sync
self.addEventListener('activate', event => {
  event.waitUntil(
    Promise.all([
      caches.keys().then(keys => Promise.all(
        keys.map(key => key !== CACHE_NAME ? caches.delete(key) : null)
      )),
      syncOfflineScans() // Prova sincronizzazione all'attivazione
    ])
  );
  self.clients.claim();
});

// Setup IndexedDB per richieste offline - con controllo per evitare il crash
let dbPromise = null;
if (typeof idb !== 'undefined') {
  dbPromise = idb.openDB('pomodori-db', 1, {
    upgrade(db) {
      if (!db.objectStoreNames.contains(STORE_NAME)) {
        db.createObjectStore(STORE_NAME, { keyPath: 'id', autoIncrement: true });
      }
    }
  });
}

// Intercetta fetch
self.addEventListener('fetch', event => {
  const { request } = event;
  const url = new URL(request.url);

  if (!url.protocol.startsWith('http')) return;

  // 🔄 Logout offline
  if (LOGOUT_URLS.some(p => url.pathname.endsWith(p))) {
    event.respondWith(
      fetch(request).catch(async () => {
        const loginPath = url.pathname.includes('dipendente') ? '/dipendente/login' : '/custom-login';
        const cached = await caches.match(loginPath) || await caches.match('/');
        return cached || new Response('Offline', { status: 200 });
      })
    );
    return;
  }

  // 🔄 POST /scan offline
  if (request.method === 'POST' && url.pathname.endsWith('/scan')) {
    event.respondWith(
      fetch(request.clone()).catch(async () => {
        const formData = await request.clone().formData();
        const json = {};
        for (const [key, value] of formData.entries()) json[key] = value;
        const db = await dbPromise;
        if (db) {
          await db.add(STORE_NAME, json);
          return new Response(null, { status: 302, headers: { 'Location': '/home?offlineSaved=true' } });
        }
        return new Response('Database Offline non disponibile', { status: 503 });
      })
    );
    return;
  }

  // 🛡️ Strategia Network-First per HTML, Cache-First per il resto
  const isHTML = request.mode === 'navigate' || request.headers.get('accept')?.includes('text/html');

  if (isHTML) {
    event.respondWith(
      fetch(request)
        .then(response => {
          if (response.ok) {
            const copy = response.clone();
            caches.open(CACHE_NAME).then(cache => cache.put(request, copy));
          }
          return response;
        })
        .catch(async () => {
          const matched = await caches.match(request) ||
            await caches.match(request, { ignoreSearch: true }) ||
            await caches.match('/home') ||
            await caches.match('/');
          return matched || new Response('Contenuto non disponibile offline', { status: 503 });
        })
    );
  } else {
    event.respondWith(
      caches.match(request).then(cached => {
        return cached || fetch(request).then(response => {
          if (response.ok || response.type === 'opaque') {
            const copy = response.clone();
            caches.open(CACHE_NAME).then(cache => cache.put(request, copy));
          }
          return response;
        }).catch(() => {
          // Se l'asset non è in cache e siamo offline
          if (url.pathname.includes('/css/') || url.pathname.includes('/js/')) {
            return new Response('', { status: 404 });
          }
          return null; // lascia che il browser gestisca l'errore se non è un asset critico
        });
      })
    );
  }
});

// 🎯 Sync
self.addEventListener('sync', event => {
  // Listener per messaggi manuali (per iOS/Safari)
  self.addEventListener('message', event => {
    if (event.data === 'sync-now') {
      event.waitUntil(syncOfflineScans());
    }
  });

  if (event.tag === 'sync-scans') event.waitUntil(syncOfflineScans());
});

async function syncOfflineScans() {
  const db = await dbPromise;
  if (!db) return;
  const scans = await db.getAll(STORE_NAME);
  for (const scan of scans) {
    try {
      const { id, ...scanData } = scan;
      const res = await fetch('/scan', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(scanData)
      });
      if (res.ok) await db.delete(STORE_NAME, scan.id);
    } catch (err) {
      console.error('Errore sincronizzazione:', err);
    }
  }
}

