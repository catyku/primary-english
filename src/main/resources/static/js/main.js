/**
 * 通用互動腳本
 */

document.addEventListener('DOMContentLoaded', function() {
    // 為所有 .speech-btn 綁定語音事件
    document.querySelectorAll('.speech-btn').forEach(function(btn) {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            var text = this.getAttribute('data-text');
            var lang = this.getAttribute('data-lang') || 'en-US';
            if (text) {
                speak(text, lang);
            }
        });
    });

    // 自動為 navbar 當前頁面加上 active
    var currentPath = window.location.pathname;
    document.querySelectorAll('.navbar-nav .nav-link').forEach(function(link) {
        if (link.getAttribute('href') === currentPath) {
            link.classList.add('active');
        }
    });
});
