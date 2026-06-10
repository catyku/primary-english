/**
 * Web Speech API 語音引擎 (TTS)
 * 提供單字、例句、中文提示的全局朗讀功能
 * 支援：語速、音調、男聲/女聲/不限、localStorage 記憶設定
 */

(function() {
    'use strict';

    var SETTINGS_KEY = 'primary_english_tts_settings';

    // 預設設定
    var DEFAULT_SETTINGS = {
        rate: 0.85,
        pitch: 1.0,
        volume: 1.0,
        gender: 'any', // 'male', 'female', 'any'
        englishVoice: '',
        chineseVoice: ''
    };

    // 當前設定（會被 localStorage 覆蓋）
    window._ttsSettings = Object.assign({}, DEFAULT_SETTINGS);

    // 可用的語音列表
    window._ttsVoices = [];

    /**
     * 從 localStorage 載入設定
     */
    function loadSettings() {
        try {
            var saved = localStorage.getItem(SETTINGS_KEY);
            if (saved) {
                var parsed = JSON.parse(saved);
                window._ttsSettings = Object.assign({}, DEFAULT_SETTINGS, parsed);
                // 清理不可能的值
                if (window._ttsSettings.rate < 0.3) window._ttsSettings.rate = 0.3;
                if (window._ttsSettings.rate > 2.0) window._ttsSettings.rate = 2.0;
                if (window._ttsSettings.pitch < 0.5) window._ttsSettings.pitch = 0.5;
                if (window._ttsSettings.pitch > 2.0) window._ttsSettings.pitch = 2.0;
            }
        } catch (e) {
            console.warn('TTS 設定載入失敗:', e);
        }
    }

    /**
     * 儲存設定到 localStorage
     */
    function saveSettings() {
        try {
            localStorage.setItem(SETTINGS_KEY, JSON.stringify(window._ttsSettings));
        } catch (e) {
            console.warn('TTS 設定儲存失敗:', e);
        }
    }

    /**
     * 取得所有可用語音
     */
    function getVoices() {
        if (!window.speechSynthesis) return [];
        return window.speechSynthesis.getVoices() || [];
    }

    /**
     * 篩選符合性別偏好的語音
     */
    function filterVoicesByGender(voices, langPrefix, genderPref) {
        if (!voices || voices.length === 0) return null;
        if (genderPref === 'any') return null; // 使用瀏覽器預設

        var candidates = voices.filter(function(v) {
            return v.lang && v.lang.toLowerCase().startsWith(langPrefix.toLowerCase());
        });

        if (candidates.length === 0) return null;

        var maleNames = /male|boy|man|dave|daniel|google us english/i;
        var femaleNames = /female|girl|woman|zira|aria|jenny|neural.*female|samantha|google uk english female|victoria|moira|siri/i;

        if (genderPref === 'female') {
            // 優先找女性語音
            var femaleVoices = candidates.filter(function(v) {
                return femaleNames.test(v.name);
            });
            if (femaleVoices.length > 0) return femaleVoices[0];
            // fallback: 排除明顯男性語音
            return candidates.find(function(v) { return !maleNames.test(v.name); }) || candidates[0];
        }

        if (genderPref === 'male') {
            // 優先找男性語音
            var maleVoices = candidates.filter(function(v) {
                return maleNames.test(v.name);
            });
            if (maleVoices.length > 0) return maleVoices[0];
            // fallback
            return candidates.find(function(v) { return !femaleNames.test(v.name); }) || candidates[0];
        }

        return null;
    }

    /**
     * 根據語言和性別偏好選擇最佳語音
     */
    function pickVoice(lang) {
        var voices = getVoices();
        if (!voices || voices.length === 0) return null;

        var langPrefix = lang === 'zh-TW' || lang === 'zh-CN' || lang === 'zh-HK' ? 'zh' : 'en';
        var gender = window._ttsSettings.gender;

        // 如果有儲存的指定語音，先嘗試
        if (langPrefix === 'en' && window._ttsSettings.englishVoice) {
            var ev = voices.find(function(v) { return v.name === window._ttsSettings.englishVoice; });
            if (ev) return ev;
        }
        if (langPrefix === 'zh' && window._ttsSettings.chineseVoice) {
            var cv = voices.find(function(v) { return v.name === window._ttsSettings.chineseVoice; });
            if (cv) return cv;
        }

        var filtered = filterVoicesByGender(voices, langPrefix, gender);
        if (filtered) return filtered;

        // Fallback: 找語言匹配的任一語音
        var match = voices.find(function(v) {
            return v.lang && v.lang.toLowerCase().startsWith(langPrefix.toLowerCase());
        });
        return match || voices[0];
    }

    /**
     * 取得語音性別標籤
     */
    function getVoiceLabel(voice) {
        var name = (voice.name || '').toLowerCase();
        if (/male|boy|man|dave|daniel|google us english/.test(name)) return '(男)';
        if (/female|girl|woman|zira|aria|jenny|samantha|moira|victoria/.test(name)) return '(女)';
        return '';
    }

    /**
     * 全域朗讀函數
     * @param {string} text 要朗讀的文字
     * @param {string} lang 語言代碼 (en-US / zh-TW 等)
     */
    function speak(text, lang) {
        if (!window.speechSynthesis) {
            console.warn('瀏覽器不支援語音合成功能');
            return;
        }
        if (!text) return;

        window.speechSynthesis.cancel();

        var utterance = new SpeechSynthesisUtterance(text);
        utterance.lang = lang || 'en-US';
        utterance.rate = parseFloat(window._ttsSettings.rate) || 0.85;
        utterance.pitch = parseFloat(window._ttsSettings.pitch) || 1.0;
        utterance.volume = parseFloat(window._ttsSettings.volume) || 1.0;

        var voice = pickVoice(utterance.lang);
        if (voice) {
            utterance.voice = voice;
        }

        window.speechSynthesis.speak(utterance);
    }

    /**
     * 更新 UI 上的設定值顯示
     */
    function updateSettingDisplays() {
        var rateVal = document.getElementById('tts-rate-val');
        var pitchVal = document.getElementById('tts-pitch-val');
        if (rateVal) rateVal.textContent = window._ttsSettings.rate + 'x';
        if (pitchVal) pitchVal.textContent = window._ttsSettings.pitch;

        var genderBtn = document.querySelectorAll('input[name="tts-gender"]');
        genderBtn.forEach(function(el) {
            el.checked = (el.value === window._ttsSettings.gender);
        });

        var rateSlider = document.getElementById('tts-rate');
        var pitchSlider = document.getElementById('tts-pitch');
        if (rateSlider) rateSlider.value = window._ttsSettings.rate;
        if (pitchSlider) pitchSlider.value = window._ttsSettings.pitch;
    }

    /**
     * 初始化語音設定面板事件
     */
    function initSettingsPanel() {
        var rateSlider = document.getElementById('tts-rate');
        var pitchSlider = document.getElementById('tts-pitch');
        var testBtn = document.getElementById('tts-test');

        if (rateSlider) {
            rateSlider.addEventListener('input', function() {
                window._ttsSettings.rate = parseFloat(this.value);
                var disp = document.getElementById('tts-rate-val');
                if (disp) disp.textContent = this.value + 'x';
            });
            rateSlider.addEventListener('change', saveSettings);
        }

        if (pitchSlider) {
            pitchSlider.addEventListener('input', function() {
                window._ttsSettings.pitch = parseFloat(this.value);
                var disp = document.getElementById('tts-pitch-val');
                if (disp) disp.textContent = this.value;
            });
            pitchSlider.addEventListener('change', saveSettings);
        }

        var genderRadios = document.querySelectorAll('input[name="tts-gender"]');
        genderRadios.forEach(function(radio) {
            radio.addEventListener('change', function() {
                if (this.checked) {
                    window._ttsSettings.gender = this.value;
                    saveSettings();
                }
            });
        });

        if (testBtn) {
            testBtn.addEventListener('click', function() {
                speak('Hello, this is a test.', 'en-US');
            });
        }
    }

    // ============ 公開 API ============

    window.speak = speak;

    window.getTTSSettings = function() {
        return Object.assign({}, window._ttsSettings);
    };

    window.setTTSSetting = function(key, value) {
        window._ttsSettings[key] = value;
        saveSettings();
    };

    window.resetTTSSettings = function() {
        window._ttsSettings = Object.assign({}, DEFAULT_SETTINGS);
        saveSettings();
        updateSettingDisplays();
    };

    // ============ 初始化 ============

    loadSettings();

    // 預載語音列表
    if (window.speechSynthesis) {
        window.speechSynthesis.getVoices();
    }
    if (window.speechSynthesis && speechSynthesis.onvoiceschanged !== undefined) {
        speechSynthesis.onvoiceschanged = function() {
            window._ttsVoices = getVoices();
        };
    }

    // DOM 就緒後綁定設定面板
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initSettingsPanel);
    } else {
        initSettingsPanel();
    }

})();
