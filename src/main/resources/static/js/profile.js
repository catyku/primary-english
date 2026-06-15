const DEFAULT_MODELS = {
    'ollama': '預設: llamma3',
    'openrouter': '預設: meta-llama/llama-3-8b-instruct',
    'gemini': '預設: gemini/gemini-1.5-flash-latest',
    'openai': '預設: gpt-4o',
    'github': '預設: gpt-4'
};

function toggleKey() {
    const keyInput = document.getElementById('apiKeyInput');
    const eyeIcon = document.getElementById('eyeIcon');
    if (keyInput.type === 'password') {
        keyInput.type = 'text';
        eyeIcon.classList.remove('ti-eye');
        eyeIcon.classList.add('ti-eye-off');
    } else {
        keyInput.type = 'password';
        eyeIcon.classList.remove('ti-eye-off');
        eyeIcon.classList.add('ti-eye');
    }
}

document.addEventListener('DOMContentLoaded', function() {
    const providerSelect = document.querySelector('select[name="apiProvider"]');
    if (providerSelect) {
        providerSelect.addEventListener('change', function() {
            document.getElementById('defaultModelHint').textContent = DEFAULT_MODELS[this.value] || '';
        });
        // Trigger change on load to set initial hint
        providerSelect.dispatchEvent(new Event('change'));
    }
});
