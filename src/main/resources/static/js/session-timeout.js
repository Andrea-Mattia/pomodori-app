let warningTimer, logoutTimer;
let warningShown = false;

const warningTime = timeoutMinutes * 60 * 1000 - 1 * 60 * 1000;
const logoutTime = timeoutMinutes * 60 * 1000;

function startTimers() {
	clearTimeout(warningTimer);
	clearTimeout(logoutTimer);

	warningShown = false; // reset stato

	if (warningTime <= 0) {
		console.warn("timeoutMinutes troppo basso: modale disattivata.");
		logoutTimer = setTimeout(() => document.getElementById("logoutFormButton").click(), logoutTime);
		return;
	}

	warningTimer = setTimeout(showWarning, warningTime);
	logoutTimer = setTimeout(() => document.getElementById("logoutFormButton").click(), logoutTime);
}

function resetTimers() {
	if (!warningShown) return;

	console.log("Attività utente rilevata: reset dei timer.");
	startTimers();
	hideWarning();
}

function showWarning() {
	warningShown = true;

	const modal = document.getElementById("sessionModal");
	document.getElementById("timeoutSound")?.play().catch(() => {});
	if (modal) new bootstrap.Modal(modal).show();
}

function hideWarning() {
	const modalEl = document.getElementById("sessionModal");
	const modal = bootstrap.Modal.getInstance(modalEl);
	if (modal) modal.hide();
	warningShown = false;
}

window.onload = startTimers;
document.onmousemove = resetTimers;
document.onkeydown = resetTimers;
document.onclick = resetTimers;
document.onscroll = resetTimers;
