document.addEventListener('DOMContentLoaded', function() {
    // 自動為 navbar 當前頁面加上 active
    var currentPath = window.location.pathname;
    document.querySelectorAll('.navbar-nav .nav-link').forEach(function(link) {
        if (link.getAttribute('href') === currentPath) {
            link.classList.add('active');
        }
    });
});
