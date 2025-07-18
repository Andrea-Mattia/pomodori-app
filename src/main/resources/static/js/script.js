// Chiude la navbar su mobile dopo il clic su un link
document
	.querySelectorAll('.navbar-nav .nav-link')
	.forEach(
		function(link) {
			link
				.addEventListener(
					'click',
					function() {
						const navbarToggler = document
							.querySelector('.navbar-toggler');
						const navbarCollapse = document
							.querySelector('.navbar-collapse');

						if (navbarToggler
							&& getComputedStyle(navbarToggler).display !== 'none') {
							navbarToggler.click();
						}
					});
		});

// mantiene in memoria username admin/dipendente
document.addEventListener('DOMContentLoaded', function() {
  const usernameInput = document.getElementById('usernameUser');
  const rememberCheckbox = document.getElementById('rememberUsername');

  // Capisci da URL se sei nella login Admin o Dipendente
  const isAdminPage = window.location.pathname.includes('/custom-login');
  const storageKey = isAdminPage ? 'savedUsernameAdmin' : 'savedUsernameDipendente';

  // Se c'è, pre-compila
  if (localStorage.getItem(storageKey)) {
    if (usernameInput) usernameInput.value = localStorage.getItem(storageKey);
    if (rememberCheckbox) rememberCheckbox.checked = true;
  }

  // Se spunti o togli la spunta
  if (rememberCheckbox) {
    rememberCheckbox.addEventListener('change', function() {
      if (this.checked) {
        localStorage.setItem(storageKey, usernameInput.value);
      } else {
        localStorage.removeItem(storageKey);
      }
    });
  }

  // Aggiorna mentre scrivi
  if (usernameInput) {
	usernameInput.addEventListener('input', function() {
	    if (rememberCheckbox && rememberCheckbox.checked) {
	      localStorage.setItem(storageKey, usernameInput.value);
	    }
	});
  }
});


//apertura fotocamera e lettura qr da home
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

  // Disabilita bottone e mostra spinner
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
        // Non è un URL, va bene comunque
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



//modale elimina dipendente
window.openDeleteModal = function(button) {
  const dipendenteId = button.getAttribute('data-id');
  const confirmBtn = document.getElementById('confirmDeleteBtn');
  confirmBtn.href = `/admin/dipendenti/delete/${dipendenteId}`;

  // Reset icona
  confirmBtn.innerHTML = `<i class="bi bi-trash"></i> Elimina`;

  const modal = new bootstrap.Modal(document.getElementById('confirmDeleteModal'));
  modal.show();

  // Quando clicca su conferma: mostra spinner
  confirmBtn.onclick = function() {
    confirmBtn.innerHTML = `<span class="spinner-border spinner-border-sm"></span> Eliminazione...`;
  };
};

//service worker
if ('serviceWorker' in navigator) {
  navigator.serviceWorker.register('/sw.js')
    .then(() => console.log('Service Worker registrato'))
    .catch(console.error);
}
