document.addEventListener('DOMContentLoaded', function() {
    // 點擊範例提示詞
    document.querySelectorAll('.example-prompt').forEach(function(el) {
        el.addEventListener('click', function(e) {
            e.preventDefault();
            var promptText = this.getAttribute('data-prompt');
            document.querySelector('textarea[name="prompt"]').value = promptText;
        });
    });

    // 提交表單時顯示 loading
    var form = document.getElementById('generateForm');
    if(form) {
        form.addEventListener('submit', function() {
            var btn = document.getElementById('generateBtn');
            var btnText = btn.querySelector('.btn-text');
            var btnLoading = btn.querySelector('.btn-loading');

            // 檢查 textarea 是否為空
            var promptTextarea = form.querySelector('textarea[name="prompt"]');
            if (promptTextarea.value.trim() === '') {
                // 如果為空，可以選擇性地顯示錯誤或阻止提交
                // 此處我們依賴 'required' 屬性，但也可以加上 JS 驗證
                return;
            }

            btn.disabled = true;
            btnText.classList.add('d-none');
            btnLoading.classList.remove('d-none');
        });
    }
});
