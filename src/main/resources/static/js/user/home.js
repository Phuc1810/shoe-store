document.addEventListener("DOMContentLoaded", () => {
    const desktopQuery = window.matchMedia("(min-width: 992px)");
    const dropdowns = document.querySelectorAll(".home-nav-dropdown");
    const closeTimers = new WeakMap();
    const closeDelayMs = 250;

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
});
