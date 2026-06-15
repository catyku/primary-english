var vocabularies = (typeof quizVocabularies !== 'undefined') ? quizVocabularies : [];
try {
    if (typeof vocabularies === 'string') {
        vocabularies = JSON.parse(vocabularies);
    }
} catch (e) {
    console.error('JSON parse failed:', e);
    vocabularies = [];
}

var currentIndex = 0;
var score = 0;
var answered = false;

function initQuiz() {
    if (vocabularies.length === 0) {
        document.getElementById('quizContainer').innerHTML =
            '<div class="alert alert-warning text-center"><i class="ti ti-alert-circle me-2"></i>沒有可用的單字資料，請先選擇分類或年級</div>';
        return;
    }

    vocabularies = shuffleArray(vocabularies.slice());
    if (vocabularies.length > 20) {
        vocabularies = vocabularies.slice(0, 20);
    }

    document.getElementById('total').textContent = vocabularies.length;
    var vocabCountEl = document.getElementById('vocabCount');
    if (vocabCountEl) {
        vocabCountEl.textContent = vocabularies.length;
    }
    showQuestion();
}

function showQuestion() {
    if (currentIndex >= vocabularies.length) {
        showResult();
        return;
    }

    answered = false;
    var vocab = vocabularies[currentIndex];
    var options = generateOptions(vocab);

    var html = '<div class="quiz-card card border-0 shadow-sm mb-4">' +
        '<div class="card-body p-4">' +
        '<div class="d-flex justify-content-between align-items-center mb-3">' +
        '<span class="badge bg-info-soft text-info">第 ' + (currentIndex + 1) + ' / ' + vocabularies.length + ' 題</span>' +
        '<button class="btn btn-outline-info btn-sm rounded-pill" onclick="speakWord()">' +
        '<i class="ti ti-volume"></i> 播放發音' +
        '</button>' +
        '</div>' +
        '<div class="text-center mb-4">' +
        '<h3 class="fw-bold text-primary display-6" id="currentWord">' + escapeHtml(vocab.english) + '</h3>' +
        '<p class="text-muted" id="currentPhonetic">' + escapeHtml(vocab.phonetic || '') + '</p>' +
        '</div>' +
        '<div class="row g-3">';

    options.forEach(function(opt) {
        html += '<div class="col-md-6">' +
            '<button class="option-btn btn btn-outline-primary w-100 py-3 fw-bold" ' +
            'onclick="selectOption(this, ' + opt.correct + ')" ' +
            'data-correct="' + opt.correct + '">' +
            escapeHtml(opt.text) +
            '</button></div>';
    });

    html += '</div>' +
        '<div class="mt-3 text-center" id="feedback" style="display:none;">' +
        '<div class="alert" role="alert"></div>' +
        '<button class="btn btn-primary" onclick="nextQuestion()">下一題 <i class="ti ti-arrow-right"></i></button>' +
        '</div>' +
        '</div></div>';

    document.getElementById('quizContainer').innerHTML = html;

    setTimeout(function() {
        speakWord();
    }, 500);

    updateProgress();
}

function generateOptions(correctVocab) {
    var correct = { text: correctVocab.chinese, correct: true };
    var wrongOptions = vocabularies
        .filter(function(v) { return v.english !== correctVocab.english; })
        .map(function(v) { return { text: v.chinese, correct: false }; })
        .slice(0, 20);

    // Randomly pick 3 wrong options
    wrongOptions = shuffleArray(wrongOptions).slice(0, 3);

    // Fill with generic if not enough
    while (wrongOptions.length < 3) {
        wrongOptions.push({ text: '其他選項', correct: false });
    }

    var allOptions = [correct].concat(wrongOptions);
    return shuffleArray(allOptions);
}

function selectOption(btn, isCorrect) {
    if (answered) return;
    answered = true;

    var card = btn.closest('.quiz-card');
    var feedback = document.getElementById('feedback');
    var feedbackAlert = feedback.querySelector('.alert');

    if (isCorrect) {
        score++;
        card.classList.add('correct');
        feedbackAlert.className = 'alert alert-success';
        feedbackAlert.innerHTML = '<i class="ti ti-circle-check me-2"></i>答對了！';
    } else {
        card.classList.add('wrong');
        feedbackAlert.className = 'alert alert-danger';
        var correctButton = card.querySelector('button[data-correct="true"]');
        feedbackAlert.innerHTML = '<i class="ti ti-circle-x me-2"></i>答錯了！正確答案是：<strong>' + escapeHtml(correctButton.textContent) + '</strong>';
        correctButton.classList.add('correct-answer');
    }

    var buttons = card.querySelectorAll('.option-btn');
    buttons.forEach(function(b) {
        b.disabled = true;
    });

    feedback.style.display = 'block';
    document.getElementById('score').textContent = score;
}

function nextQuestion() {
    currentIndex++;
    showQuestion();
}

function showResult() {
    var modal = new bootstrap.Modal(document.getElementById('resultModal'));
    document.getElementById('finalScore').textContent = score;
    document.getElementById('finalTotal').textContent = vocabularies.length;

    var percentage = (score / vocabularies.length) * 100;
    var resultProgress = document.getElementById('resultProgress');
    resultProgress.style.width = percentage + '%';
    resultProgress.textContent = Math.round(percentage) + '%';

    var emoji = '🎉';
    var message = '太棒了！';
    if (percentage < 60) {
        emoji = '🤔';
        message = '再接再厲！';
        resultProgress.classList.add('bg-danger');
    } else if (percentage < 80) {
        emoji = '👍';
        message = '不錯喔！';
        resultProgress.classList.add('bg-warning');
    } else {
        resultProgress.classList.add('bg-success');
    }

    document.getElementById('resultEmoji').textContent = emoji;
    document.getElementById('resultMessage').textContent = message;

    modal.show();
}

function speakWord() {
    var word = document.getElementById('currentWord').textContent;
    var utterance = new SpeechSynthesisUtterance(word);
    utterance.lang = 'en-US';
    speechSynthesis.speak(utterance);
}

function updateProgress() {
    var progress = (currentIndex / vocabularies.length) * 100;
    document.getElementById('progressBar').style.width = progress + '%';
}

function shuffleArray(array) {
    for (var i = array.length - 1; i > 0; i--) {
        var j = Math.floor(Math.random() * (i + 1));
        [array[i], array[j]] = [array[j], array[i]];
    }
    return array;
}

function escapeHtml(unsafe) {
    return unsafe
         .replace(/&/g, "&amp;")
         .replace(/</g, "&lt;")
         .replace(/>/g, "&gt;")
         .replace(/"/g, "&quot;")
         .replace(/'/g, "&#039;");
}

document.addEventListener('DOMContentLoaded', initQuiz);
