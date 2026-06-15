var questionCount = 0;
var existingQuestions = []; // This will be populated by Thymeleaf

function addQuestion(data) {
    questionCount++;
    var idx = questionCount;
    var q = data || {};
    var html = '<div class="question-box p-3 mb-3" id="q_box_' + idx + '">' +
        '<div class="d-flex justify-content-between align-items-center mb-2">' +
            '<span class="fw-bold text-primary">Q' + idx + '</span>' +
            '<button type="button" class="btn btn-outline-danger btn-xs" onclick="removeQuestion(' + idx + ')">' +
                '<i class="ti ti-trash"></i> 刪除</button>' +
        '</div>' +
        '<input type="text" name="q_question_' + idx + '" class="form-control mb-2" required' +
            ' placeholder="請輸入問題" value="' + (q.question || '') + '" maxlength="500">' +
        '<div class="row g-2 mb-2">' +
            '<div class="col-md-6"><input type="text" name="q_optA_' + idx + '" class="form-control" required' +
                ' placeholder="選項 A" value="' + (q.optionA || '') + '" maxlength="200"></div>' +
            '<div class="col-md-6"><input type="text" name="q_optB_' + idx + '" class="form-control" required' +
                ' placeholder="選項 B" value="' + (q.optionB || '') + '" maxlength="200"></div>' +
            '<div class="col-md-6"><input type="text" name="q_optC_' + idx + '" class="form-control" required' +
                ' placeholder="選項 C" value="' + (q.optionC || '') + '" maxlength="200"></div>' +
            '<div class="col-md-6"><input type="text" name="q_optD_' + idx + '" class="form-control" required' +
                ' placeholder="選項 D" value="' + (q.optionD || '') + '" maxlength="200"></div>' +
        '</div>' +
        '<select name="q_answer_' + idx + '" class="form-select form-select-sm" style="max-width:120px;">' +
            '<option value="A" ' + ((q.correctAnswer||'A')=='A'?'selected':'') + '>正確答案：A</option>' +
            '<option value="B" ' + ((q.correctAnswer||'')=='B'?'selected':'') + '>正確答案：B</option>' +
            '<option value="C" ' + ((q.correctAnswer||'')=='C'?'selected':'') + '>正確答案：C</option>' +
            '<option value="D" ' + ((q.correctAnswer||'')=='D'?'selected':'') + '>正確答案：D</option>' +
        '</select>' +
    '</div>';
    var container = document.getElementById('questionsContainer');
    var div = document.createElement('div');
    div.innerHTML = html;
    container.appendChild(div.firstElementChild);
}

function removeQuestion(idx) {
    var box = document.getElementById('q_box_' + idx);
    if (box) box.remove();
}

function initializeForm(questions) {
    existingQuestions = questions;
    if (existingQuestions && existingQuestions.length > 0) {
        existingQuestions.forEach(function(q) { addQuestion(q); });
    } else {
        for (var i = 0; i < 5; i++) addQuestion();
    }
}
