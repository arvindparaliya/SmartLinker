console.log("Script loaded");

// theme manage, dark : light
function getTheme() {
  return localStorage.getItem("theme") || "light";
}

function setTheme(theme) {
  localStorage.setItem("theme", theme);
}

function applyTheme(theme) {
  const htmlEl = document.documentElement;
  htmlEl.classList.remove("light", "dark");
  htmlEl.classList.add(theme);
  
  // Update all theme labels
  document.querySelectorAll("[id^='theme_label']").forEach(label => {
    if (label) label.textContent = theme === "light" ? "Dark" : "Light";
  });
}

function toggleTheme() {
  const newTheme = currentTheme === "dark" ? "light" : "dark";
  currentTheme = newTheme;
  setTheme(newTheme);
  applyTheme(newTheme);
}

// Initialize theme
let currentTheme = getTheme();

// for mobile menu toggle button
function setupMobileMenu() {
  const mobileMenuToggle = document.getElementById("mobile-menu-toggle");
  const mobileMenu = document.getElementById("mobile-menu");

  if (mobileMenuToggle && mobileMenu) {
    mobileMenuToggle.addEventListener("click", () => {
      mobileMenu.classList.toggle("hidden");
    });
  }
}

//mobile search
function setupMobileSearch() {
  const searchToggle = document.getElementById("mobile-search-toggle");
  const searchBar = document.getElementById("mobile-search");

  if (searchToggle && searchBar) {
    searchToggle.addEventListener("click", () => {
      searchBar.classList.toggle("hidden");
      if (!searchBar.classList.contains("hidden")) {
        searchBar.querySelector("input")?.focus();
      }
    });
  }
}

//initalise
function init() {
  applyTheme(currentTheme);

  document.getElementById("theme_change_button")?.addEventListener("click", toggleTheme);
  document.getElementById("theme_change_button_mobile")?.addEventListener("click", toggleTheme);

  setupMobileMenu();
  setupMobileSearch();

  document.querySelectorAll("#mobile-menu a").forEach(link => {
    link.addEventListener("click", () => {
      document.getElementById("mobile-menu")?.classList.add("hidden");
    });
  });
}

// Start everything when DOM is ready
if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", init);
} else {
  init();
}

// sidebar is always visible on desktop
function checkSidebarVisibility() {
  const sidebar = document.getElementById('logo-sidebar');
  if (sidebar && window.innerWidth >= 768) {
    sidebar.classList.remove('-translate-x-full');
    // sidebar.classList.add('translate-x-0');
  }
}

// Run on load and resize
window.addEventListener('load', checkSidebarVisibility);
window.addEventListener('resize', checkSidebarVisibility);