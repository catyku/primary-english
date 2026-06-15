document.addEventListener('DOMContentLoaded', function() {
    // 點擊範例提示詞
    document.querySelectorAll('.example-prompt').forEach(function(el) {
        el.addEventListener('click', function(e) {
            e.preventDefault();
            var promptText = this.getAttribute('data-prompt');
            document.querySelector('textarea[name="prompt"]').value = promptText;
        });
    });

    // 提交表單時使用 AJAX 背景生成
    var form = document.getElementById('generateForm');
    var modalEl = document.getElementById('generationModal');
    var modal = modalEl ? new bootstrap.Modal(modalEl, {backdrop: 'static', keyboard: false}) : null;

    if(form) {
        form.addEventListener('submit', function(e) {
            e.preventDefault();

            var promptTextarea = form.querySelector('textarea[name="prompt"]');
            if (promptTextarea.value.trim() === '') {
                return;
            }

            var btn = document.getElementById('generateBtn');
            var btnText = btn.querySelector('.btn-text');
            var btnLoading = btn.querySelector('.btn-loading');

            btn.disabled = true;
            btnText.classList.add('d-none');
            btnLoading.classList.remove('d-none');

            var formData = new FormData(form);

            fetch('/admin/articles/generate-async', {
                method: 'POST',
                body: formData
            })
            .then(function(response) { return response.json(); })
            .then(function(data) {
                if (data.error) {
                    alert(data.error);
                    btn.disabled = false;
                    btnText.classList.remove('d-none');
                    btnLoading.classList.add('d-none');
                    return;
                }

                // Show modal
                if (modal) {
                    document.getElementById('generationPending').classList.remove('d-none');
                    document.getElementById('generationSuccess').classList.add('d-none');
                    document.getElementById('generationFailed').classList.add('d-none');
                    modal.show();
                }

                // Start polling
                pollGenerationStatus(data.jobId);

                // Reset button
                btn.disabled = false;
                btnText.classList.remove('d-none');
                btnLoading.classList.add('d-none');
            })
            .catch(function(err) {
                alert('提交失敗：' + err.message);
                btn.disabled = false;
                btnText.classList.remove('d-none');
                btnLoading.classList.add('d-none');
            });
        });
    }

    function pollGenerationStatus(jobId) {
        var pollInterval = setInterval(function() {
            fetch('/admin/articles/generate/status/' + jobId)
                .then(function(response) { return response.json(); })
                .then(function(data) {
                    if (data.status === 'completed') {
                        clearInterval(pollInterval);
                        document.getElementById('generationPending').classList.add('d-none');
                        document.getElementById('generationSuccess').classList.remove('d-none');
                        document.getElementById('generationSuccessMessage').textContent = data.message || '文章生成完成！';
                        document.getElementById('generationEditLink').href = '/admin/articles/' + data.articleId + '/edit';
                        // Allow closing
                        if (modalEl) {
                            modalEl.setAttribute('data-bs-backdrop', 'true');
                            modalEl.setAttribute('data-bs-keyboard', 'true');
                        }
                    } else if (data.status === 'failed') {
                        clearInterval(pollInterval);
                        document.getElementById('generationPending').classList.add('d-none');
                        document.getElementById('generationFailed').classList.remove('d-none');
                        document.getElementById('generationFailedMessage').textContent = data.error || '生成失敗';
                        if (modalEl) {
                            modalEl.setAttribute('data-bs-backdrop', 'true');
                            modalEl.setAttribute('data-bs-keyboard', 'true');
                        }
                    }
                    // else pending/running: keep polling
                })
                .catch(function(err) {
                    console.error('Poll error:', err);
                });
        }, 3000); // Poll every 3 seconds
    }
});
