/* ============ State Variables ============ */
let isActive = false;
let isLoading = false;
let isRecording = false;
let currentTopic = '';
let history = [];
let recognition = null;

/* ============ SpeechRecognition Initialization ============ */
function initSpeechRecognition() {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (!SpeechRecognition) {
        document.getElementById('micBtn').style.display = 'none';
        document.getElementById('speechTipText').textContent = '你的瀏覽器不支援語音輸入，請用鍵盤輸入英文回答。';
        return;
    }
    recognition = new SpeechRecognition();
    recognition.lang = 'en-US';
    recognition.interimResults = false;
    recognition.maxAlternatives = 1;

    recognition.onresult = function(event) {
        const transcript = event.results[0][0].transcript;
        document.getElementById('textInput').value = transcript;
        sendMessage();
    };
    recognition.onerror = function(event) {
        console.warn('語音辨識錯誤:', event.error);
        stopRecording();
        if (event.error !== 'aborted') {
            showStatus('語音辨識失敗，請改用打字輸入', 'error');
        }
    };
    recognition.onend = function() {
        stopRecording();
    };
}

/* ============ UI Update Functions ============ */
function showStatus(text, type = 'info') {
    const statusText = document.getElementById('statusText');
    const statusDot = document.getElementById('statusDot');
    statusText.textContent = text;
    statusDot.className = 'status-dot';
    if (type === 'loading') {
        statusDot.classList.add('loading');
    } else if (type === 'speaking') {
        statusDot.classList.add('speaking');
    }
}

function addMessageToChat(sender, text, translation = '', hint = '') {
    const chatContainer = document.getElementById('chatContainer');
    const isUser = sender === 'user';
    const bubble = document.createElement('div');
    bubble.className = `chat-bubble ${sender}`;

    const meta = `<div class="bubble-meta"><i class="ti ti-${isUser ? 'user' : 'robot'}"></i> ${isUser ? '你' : 'AI 老師'}</div>`;
    const textContent = `<div class="bubble-text">${text}</div>`;
    const translationContent = translation ? `<div class="bubble-translation"><strong>翻譯：</strong> ${translation}</div>` : '';
    const hintContent = hint ? `<div class="bubble-hint"><strong>提示：</strong> ${hint}</div>` : '';
    const speakBtn = `<button class="speak-bubble-btn" onclick="speakText(this, '${text.replace(/'/g, "\\'")}')"><i class="ti ti-volume"></i></button>`;

    bubble.innerHTML = meta + textContent + translationContent + hintContent + speakBtn;
    chatContainer.appendChild(bubble);
    chatContainer.scrollTop = chatContainer.scrollHeight;
}

function showTypingIndicator() {
    const chatContainer = document.getElementById('chatContainer');
    const typingIndicator = document.createElement('div');
    typingIndicator.id = 'typingIndicator';
    typingIndicator.className = 'chat-bubble ai';
    typingIndicator.innerHTML = `<div class="bubble-meta"><i class="ti ti-robot"></i> AI 老師</div><div class="typing-dots"><span></span><span></span><span></span></div>`;
    chatContainer.appendChild(typingIndicator);
    chatContainer.scrollTop = chatContainer.scrollHeight;
}

function removeTypingIndicator() {
    const indicator = document.getElementById('typingIndicator');
    if (indicator) {
        indicator.remove();
    }
}

/* ============ Speech Synthesis ============ */
function speakText(button, text) {
    if (isLoading) return;
    const utterance = new SpeechSynthesisUtterance(text);
    utterance.lang = 'en-US';
    const originalIcon = button.innerHTML;
    const statusDot = document.getElementById('statusDot');

    utterance.onstart = () => {
        showStatus('AI 正在朗讀...', 'speaking');
        button.innerHTML = '<i class="ti ti-player-pause"></i>';
        statusDot.classList.add('speaking');
    };
    utterance.onend = () => {
        showStatus(isActive ? '輪到你說了' : '等待開始', 'info');
        button.innerHTML = originalIcon;
        statusDot.classList.remove('speaking');
    };
    utterance.onerror = (e) => {
        console.error('朗讀失敗:', e);
        showStatus('朗讀失敗', 'error');
        button.innerHTML = originalIcon;
        statusDot.classList.remove('speaking');
    };
    speechSynthesis.speak(utterance);
}

/* ============ Recording Functions ============ */
function startRecording() {
    if (!recognition || isRecording) return;
    try {
        recognition.start();
        isRecording = true;
        document.getElementById('micBtn').classList.add('recording');
        showStatus('正在聆聽...', 'speaking');
    } catch (e) {
        console.error("無法開始錄音:", e);
        showStatus('無法啟動麥克風', 'error');
    }
}

function stopRecording() {
    if (!recognition || !isRecording) return;
    recognition.stop();
    isRecording = false;
    document.getElementById('micBtn').classList.remove('recording');
    showStatus(isActive ? '輪到你說了' : '等待開始', 'info');
}

/* ============ Conversation Flow ============ */
async function startConversation() {
    if (isLoading) return;
    isLoading = true;
    document.getElementById('startBtn').disabled = true;
    showStatus('正在準備對話...', 'loading');

    const grade = document.getElementById('gradeSelect').value;
    const difficulty = document.getElementById('difficultySelect').value;

    try {
        const response = await fetch('/api/ai-conversation/start', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ grade, difficulty })
        });

        if (!response.ok) throw new Error(`伺服器錯誤: ${response.status}`);

        const data = await response.json();
        
        if (data.error) {
            throw new Error(data.error);
        }
        
        currentTopic = data.topic;
        history = [{ role: 'system', content: data.systemPrompt || '' }];

        document.getElementById('chatContainer').innerHTML = '';
        addMessageToChat('ai', data.english || data.initialMessage, data.chinese || data.translation, data.hint);
        document.getElementById('topicText').textContent = currentTopic;
        document.getElementById('topicDisplay').classList.remove('d-none');
        document.getElementById('speechTip').classList.remove('d-none');
        document.getElementById('inputArea').style.opacity = '1';
        document.getElementById('inputArea').style.pointerEvents = 'auto';
        document.getElementById('startBtn').textContent = '🚀 重新開始';

        isActive = true;
        showStatus('輪到你說了', 'info');

    } catch (error) {
        console.error('開始對話失敗:', error);
        showStatus('開始對話失敗，請重試', 'error');
        isActive = false;
    } finally {
        isLoading = false;
        document.getElementById('startBtn').disabled = false;
    }
}

async function sendMessage() {
    const textInput = document.getElementById('textInput');
    const message = textInput.value.trim();
    if (!message || !isActive || isLoading) return;

    isLoading = true;
    textInput.value = '';
    textInput.disabled = true;
    document.getElementById('sendBtn').disabled = true;
    document.getElementById('micBtn').disabled = true;

    addMessageToChat('user', message);
    history.push({ role: 'user', content: message });
    showStatus('AI 正在思考...', 'loading');
    showTypingIndicator();

    try {
        const response = await fetch('/api/ai-conversation/message', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ 
                topic: currentTopic, 
                history: history,
                grade: document.getElementById('gradeSelect').value,
                difficulty: document.getElementById('difficultySelect').value
            })
        });

        if (!response.ok) throw new Error(`伺服器錯誤: ${response.status}`);

        const data = await response.json();
        
        if (data.error) {
            throw new Error(data.error);
        }
        
        const aiResponse = (data.english || data.response || '').replace(/<br>/g, '\n');
        const chineseResponse = (data.chinese || data.chineseResponse || '').replace(/<br>/g, '\n');
        
        removeTypingIndicator();
        addMessageToChat('ai', aiResponse, chineseResponse, data.hint);
        history.push({ role: 'assistant', content: aiResponse });
        showStatus('輪到你說了', 'info');

    } catch (error) {
        console.error('傳送訊息失敗:', error);
        removeTypingIndicator();
        addMessageToChat('ai', '抱歉，我好像有點問題，請稍後再試。', 'Sorry, I seem to be having some trouble. Please try again later.');
        showStatus('發生錯誤，請重試', 'error');
    } finally {
        isLoading = false;
        textInput.disabled = false;
        document.getElementById('sendBtn').disabled = false;
        document.getElementById('micBtn').disabled = false;
        textInput.focus();
    }
}

function endConversation() {
    isActive = false;
    isLoading = false;
    currentTopic = '';
    history = [];
    
    document.getElementById('topicDisplay').classList.add('d-none');
    document.getElementById('speechTip').classList.add('d-none');
    document.getElementById('inputArea').style.opacity = '0.5';
    document.getElementById('inputArea').style.pointerEvents = 'none';
    document.getElementById('startBtn').textContent = '🚀 開始新對話';
    
    const chatContainer = document.getElementById('chatContainer');
    chatContainer.innerHTML = `
        <div class="empty-chat">
            <div class="empty-emoji">🎙️</div>
            <h4 class="fw-bold mt-3">對話已結束</h4>
            <p>準備好再次挑戰了嗎？點擊上方按鈕開始新的對話！</p>
        </div>`;
    showStatus('等待開始', 'info');
}

/* ============ Event Listeners ============ */
document.addEventListener('DOMContentLoaded', () => {
    initSpeechRecognition();

    const micBtn = document.getElementById('micBtn');
    micBtn.addEventListener('mousedown', startRecording);
    micBtn.addEventListener('mouseup', stopRecording);
    micBtn.addEventListener('touchstart', startRecording);
    micBtn.addEventListener('touchend', stopRecording);
});
