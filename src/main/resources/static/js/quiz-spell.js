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
            '<div class="alert alert-warning text-center">' +
            '<i class="ti ti-alert-circle me-2"></i>沒有可用的單字資料，請先選擇分類或年級</div>';
        return;
    }

    vocabularies = shuffleArray(vocabularies.slice());
    if (vocabularies.length > 20) {
        vocabularies = vocabularies.slice(0, 20);
    }

    document.getElementById('total').textContent = vocabularies.length;
    showQuestion();
}

function showQuestion() {
    if (currentIndex >= vocabularies.length) {
        showResult();
        return;
    }

    answered = false;
    var vocab = vocabularies[currentIndex];

    // Generate letter hints
    var word = vocab.english;
    var hintHtml = '';
    for (var i = 0; i < word.length; i++) {
        hintHtml += '<span class="letter-hint empty" id="hint_' + i + '" data-letter="' + word[i].toLowerCase() + '" data-index="' + i + '" onclick="fillLetter(\'' + i + '\')"></span>';
    }

    var html = '<div class="quiz-card card border-0 shadow-sm mb-4">' +
        '<div class="card-body p-4">' +
        '<div class="d-flex justify-content-between align-items-center mb-3">' +
        '<span class="badge bg-warning-soft text-warning">第 ' + (currentIndex + 1) + ' / ' + vocabularies.length + ' 題</span>' +
        '<button class="btn btn-outline-warning btn-sm rounded-pill" onclick="speak(\'' + escapeHtml(vocab.chinese) + '\', \'zh-TW\')">' +
        '<i class="ti ti-volume"></i> 播放中文' +
        '</button>' +
        '</div>' +
        '<div class="text-center mb-4">' +
        '<h4 class="fw-bold text-dark mb-2">請拼出這個單字：</h4>' +
        '<p class="text-muted fs-5">' + escapeHtml(vocab.chinese) + '</p>' +
        '<p class="text-muted">' + escapeHtml(vocab.phonetic || '') + '</p>' +
        '</div>' +
        '<div class="text-center mb-4" id="letterHints">' + hintHtml + '</div>' +
        '<div class="text-center mb-3">' +
        '<input type="text" id="spellInput" class="form-control spell-input text-center" ' +
        'placeholder="輸入英文單字..." maxlength="' + word.length + '" ' +
        'oninput="checkInput()" autocomplete="off">' +
        '</div>' +
        '<div class="text-center">' +
        '<button class="btn btn-warning fw-bold" onclick="submitAnswer()">確認答案</button>' +
        '</div>' +
        '<div class="mt-3 text-center" id="feedback" style="display:none;">' +
        '<div class="alert" role="alert"></div>' +
        '<button class="btn btn-primary" onclick="nextQuestion()">下一題 <i class="ti ti-arrow-right"></i></button>' +
        '</div>' +
        '</div></div>';

    document.getElementById('quizContainer').innerHTML = html;

    setTimeout(function() {
        speak(vocab.chinese, 'zh-TW');
    }, 500);

    updateProgress();
}

function checkInput() {
    var input = document.getElementById('spellInput').value.toLowerCase();
    var word = vocabularies[currentIndex].english.toLowerCase();
    var hints = document.querySelectorAll('.letter-hint');

    hints.forEach(function(hint, idx) {
        if (idx < input.length) {
            hint.textContent = input[idx].toUpperCase();
            if (input[idx] === word[idx]) {
                hint.classList.remove('empty');
                hint.classList.add('filled');
            } else {
                hint.classList.remove('filled');
                hint.classList.add('empty');
            }
        } else {
            hint.textContent = '';
            hint.classList.remove('filled');
            hint.classList.add('empty');
        }
    });
}

function submitAnswer() {
    if (answered) return;
    answered = true;

    var input = document.getElementById('spellInput').value.toLowerCase();
    var correctWord = vocabularies[currentIndex].english.toLowerCase();
    var isCorrect = (input === correctWord);

    var card = document.querySelector('.quiz-card');
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
        feedbackAlert.innerHTML = '<i class="ti ti-circle-x me-2"></i>答錯了！正確答案是：<strong>' + escapeHtml(correctWord.toUpperCase()) + '</strong>';
    }

    document.getElementById('spellInput').disabled = true;
    document.querySelector('.btn-warning').disabled = true;
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

function speak(text, lang) {
    var utterance = new SpeechSynthesisUtterance(text);
    utterance.lang = lang;
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
