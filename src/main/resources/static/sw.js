const CACHE = 'pomodori-cache-v1';

self.addEventListener('install', e => {
  e.waitUntil(
    caches.open(CACHE).then(c => c.addAll([
      '/home',
      '/scan',
      '/dipendente/login',
      '/css/style.css',
      '/js/script.js',
      '/manifest.json',
      '/icons/android-chrome-192x192.png',
      '/icons/android-chrome-512x512.png'
    ]))
  );
});

self.addEventListener('fetch', e => {
  e.respondWith(
    caches.match(e.request).then(cached => {
      if (cached) return cached;
      return fetch(e.request, { redirect: 'follow' }).then(response => {
        return response;
      });
    }).catch(() =>
      new Response("Offline o errore di rete", {
        status: 503,
        statusText: "Service Unavailable"
      })
    )
  );
});
