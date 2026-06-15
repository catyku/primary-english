function speak(text, lang) {
    var utterance = new SpeechSynthesisUtterance(text);
    utterance.lang = lang;
    speechSynthesis.speak(utterance);
}

function speakArticle() {
    var text = document.querySelector('.article-content').innerText;
    if (text) speak(text, 'en-US');
}

function toggleTranslation() {
    var box = document.getElementById('translationBox');
    box.style.display = (box.style.display === 'none') ? 'block' : 'none';
}

document.getElementById('quizForm').addEventListener('submit', function(e) {
    e.preventDefault();
    document.getElementById('submitOverlay').style.display = 'block';
    document.getElementById('quizForm').style.display = 'none';

    var formData = new FormData(this);
    fetch(this.action, {
        method: 'POST',
        body: new URLSearchParams(formData)
    })
    .then(function(r) { return r.json(); })
    .then(function(data) {
        // The redirect is handled by the controller, but as a fallback:
        window.location.href = '/reading/' + data.articleId + '/result';
    })
    .catch(function(err) {
        console.error(err);
        alert('提交答案時發生錯誤，請重試。');
        document.getElementById('submitOverlay').style.display = 'none';
        document.getElementById('quizForm').style.display = 'block';
    });
});

// Reading progress bar
window.addEventListener('scroll', function() {
    var scrollTop = window.pageYOffset || document.documentElement.scrollTop;
    var scrollHeight = document.documentElement.scrollHeight - document.documentElement.clientHeight;
    var progress = (scrollTop / scrollHeight) * 100;
    document.getElementById('progressBar').style.width = progress + '%';
});
