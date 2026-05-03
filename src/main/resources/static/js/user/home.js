document.addEventListener("DOMContentLoaded", () => {
    const desktopQuery = window.matchMedia("(min-width: 992px)");
    const dropdowns = document.querySelectorAll(".home-nav-dropdown");
    const closeTimers = new WeakMap();
    const closeDelayMs = 250;
    const anonymousHeaderState = document.querySelector(".anonymous-header-state");
    const authRequiredModalElement = document.getElementById("authRequiredModal");
    const authRequiredModal = anonymousHeaderState && authRequiredModalElement && window.bootstrap
        ? new window.bootstrap.Modal(authRequiredModalElement)
        : null;

    function openDropdown(dropdown) {
        window.clearTimeout(closeTimers.get(dropdown));
        dropdown.classList.add("is-open");
    }

    function scheduleClose(dropdown) {
        window.clearTimeout(closeTimers.get(dropdown));
        const timer = window.setTimeout(() => {
            dropdown.classList.remove("is-open");
        }, closeDelayMs);
        closeTimers.set(dropdown, timer);
    }

    dropdowns.forEach((dropdown) => {
        dropdown.addEventListener("mouseenter", () => {
            if (desktopQuery.matches) {
                openDropdown(dropdown);
            }
        });

        dropdown.addEventListener("mouseleave", () => {
            if (desktopQuery.matches) {
                scheduleClose(dropdown);
            }
        });

        dropdown.addEventListener("focusin", () => openDropdown(dropdown));
        dropdown.addEventListener("focusout", () => scheduleClose(dropdown));
    });

    desktopQuery.addEventListener("change", () => {
        dropdowns.forEach((dropdown) => dropdown.classList.remove("is-open"));
    });

    if (anonymousHeaderState && authRequiredModal) {
        document.addEventListener("click", (event) => {
            const profileLink = event.target.closest("a[href='/profile'], a[href$='/profile']");
            if (!profileLink) {
                return;
            }

            event.preventDefault();
            authRequiredModal.show();
        });
    }
});
