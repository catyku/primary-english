// 單字頁面自動朗讀按鈕綁定
document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.speech-btn').forEach(function(btn) {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            var text = this.getAttribute('data-text');
            if (text) {
                speak(text, 'en-US');
            }
        });
    });
});
