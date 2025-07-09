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
