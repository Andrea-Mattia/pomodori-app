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