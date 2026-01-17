// Chiude la navbar su mobile dopo il clic su un link
document.querySelectorAll('.navbar-nav .nav-link').forEach(function (link) {
  link.addEventListener('click', function () {
    const navbarToggler = document.querySelector('.navbar-toggler');
    if (navbarToggler && getComputedStyle(navbarToggler).display !== 'none') {
      navbarToggler.click();
    }
  });
});

// Mantiene in memoria username admin/dipendente
document.addEventListener('DOMContentLoaded', function () {
  const usernameInput = document.getElementById('usernameUser');
  const rememberCheckbox = document.getElementById('rememberUsername');
  const isAdminPage = window.location.pathname.includes('/custom-login');
  const storageKey = isAdminPage ? 'savedUsernameAdmin' : 'savedUsernameDipendente';

  if (localStorage.getItem(storageKey)) {
    if (usernameInput) usernameInput.value = localStorage.getItem(storageKey);
    if (rememberCheckbox) rememberCheckbox.checked = true;
  }

  if (rememberCheckbox) {
    rememberCheckbox.addEventListener('change', function () {
      if (this.checked) {
        localStorage.setItem(storageKey, usernameInput.value);
      } else {
        localStorage.removeItem(storageKey);
      }
    });
  }

  if (usernameInput) {
    usernameInput.addEventListener('input', function () {
      if (rememberCheckbox && rememberCheckbox.checked) {
        localStorage.setItem(storageKey, usernameInput.value);
      }
    });

    // Supporto tasto Enter su tutti i campi del form di login
    const loginForm = usernameInput.closest('form');
    if (loginForm) {
      const inputs = loginForm.querySelectorAll('input');
      inputs.forEach(input => {
        input.addEventListener('keydown', function (e) {
          if (e.key === 'Enter') {
            e.preventDefault(); // Previene comportamenti duplicati
            const submitBtn = loginForm.querySelector('button[type="submit"]');
            if (submitBtn) {
              submitBtn.click(); // Simula il click reale sul pulsante
            } else {
              loginForm.submit(); // Fallback se il pulsante non viene trovato
            }
          }
        });
      });
    }
  }
});

// Apertura fotocamera e lettura qr da home
document.addEventListener('DOMContentLoaded', () => {
  const scanButton = document.getElementById('startScannerButton');
  if (scanButton) {
    scanButton.addEventListener('click', startScanner);
  }
});

function startScanner() {
  const scanButton = document.getElementById('startScannerButton');
  const scanSpinner = document.getElementById('scanSpinner');
  const scanButtonText = document.getElementById('scanButtonText');

  scanButton.disabled = true;
  scanSpinner.classList.remove('d-none');
  scanButtonText.textContent = 'Apertura fotocamera...';

  const qrReader = new Html5Qrcode("qr-reader");

  qrReader.start(
    { facingMode: "environment" },
    { fps: 10, qrbox: 250 },
    qrCodeMessage => {
      let qrValue = qrCodeMessage;
      try {
        const url = new URL(qrCodeMessage);
        const params = new URLSearchParams(url.search);
        qrValue = params.get("qr") || qrCodeMessage;
      } catch (e) {
        // Non è un URL
      }

      qrReader.stop().then(() => {
        resetScannerButton();
        window.location.href = `/scan?qr=${encodeURIComponent(qrValue)}`;
      });
    },
    errorMessage => {
      // Ignora errori temporanei
    }
  ).catch(err => {
    alert("Impossibile accedere alla fotocamera: " + err);
    resetScannerButton();
  });

  function resetScannerButton() {
    scanButton.disabled = false;
    scanSpinner.classList.add('d-none');
    scanButtonText.textContent = 'Avvia fotocamera';
  }
}

// Modale elimina dipendente
window.openDeleteModal = function (button) {
  const dipendenteId = button.getAttribute('data-id');
  const confirmBtn = document.getElementById('confirmDeleteBtn');
  confirmBtn.href = `/admin/dipendenti/delete/${dipendenteId}`;
  confirmBtn.innerHTML = `<i class="bi bi-trash"></i> Elimina`;

  const modal = new bootstrap.Modal(document.getElementById('confirmDeleteModal'));
  modal.show();

  confirmBtn.onclick = function () {
    confirmBtn.innerHTML = `<span class="spinner-border spinner-border-sm"></span> Eliminazione...`;
  };
};

// Service Worker + Sync per invio dati offline
if ('serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    const swStatus = document.getElementById('sw-status');
    navigator.serviceWorker.register('/sw.js')
      .then(reg => {
        console.log('✅ Service Worker registrato');

        // Funzione per aggiornare il badge di stato
        const updateStatus = (worker) => {
          if (!swStatus) return;
          if (worker.state === 'activated') {
            swStatus.className = 'badge rounded-pill bg-success me-2';
            swStatus.textContent = 'App Offline Pronta';
          } else {
            swStatus.className = 'badge rounded-pill bg-warning text-dark me-2';
            swStatus.textContent = 'SW: ' + worker.state;
          }
        };

        if (reg.active) updateStatus(reg.active);

        reg.onupdatefound = () => {
          const installingWorker = reg.installing;
          installingWorker.onstatechange = () => {
            updateStatus(installingWorker);
            if (installingWorker.state === 'installed' && navigator.serviceWorker.controller) {
              console.log('Nuovo Service Worker disponibile, ricarica...');
              window.location.reload();
            }
          };
        };
      })
      .catch(err => {
        if (swStatus) {
          swStatus.className = 'badge rounded-pill bg-danger me-2';
          swStatus.textContent = 'Errore SW: ' + err.message.substring(0, 20);
        }
        console.error('Service Worker Error:', err);
      });
  });
}

// Gestione indicatore visuale offline
window.addEventListener('online', () => document.getElementById('offline-status-alert')?.classList.add('d-none'));
window.addEventListener('offline', () => document.getElementById('offline-status-alert')?.classList.remove('d-none'));
if (!navigator.onLine) document.getElementById('offline-status-alert')?.classList.remove('d-none');

// Gestione form scan offline (solo se necessario JS fallback)
document.addEventListener('DOMContentLoaded', () => {
  const form = document.querySelector('form[action="/scan"]');
  if (form && navigator.onLine === false) {
    form.addEventListener('submit', e => {
      e.preventDefault();

      const formData = new FormData(form);
      const scan = {};
      for (const [key, value] of formData.entries()) {
        scan[key] = value;
      }

      // Salva in IndexedDB
      if (window.indexedDB && 'serviceWorker' in navigator && 'SyncManager' in window) {
        navigator.serviceWorker.ready.then(reg => {
          return reg.sync.register('sync-scans');
        });

        form.reset();
        window.location.href = '/home?offlineSaved';
      } else {
        alert("Offline e salvataggio automatico non supportato.");
      }
    });
  }
});
