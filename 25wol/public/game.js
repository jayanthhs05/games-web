// ========================================
// 25 WORDS OR LESS - CLIENT GAME LOGIC
// ========================================

const socket = io();

// Game state
let gameState = {
    gameId: null,
    role: null,
    words: [],
    wordCount: 0,
    currentWordIndex: 0,
    guessedWords: new Set(),
    passedWords: [],
    cluesRemaining: 25,
    timeRemaining: 180,
    started: false
};

// Game parameters
let params = {
    words: 10,
    clues: 25,
    time: 180
};

// DOM Elements
const screens = {
    landing: document.getElementById('landing-screen'),
    waiting: document.getElementById('waiting-screen'),
    game: document.getElementById('game-screen'),
    result: document.getElementById('result-screen')
};

const elements = {
    // Landing
    tabs: document.querySelectorAll('.tab'),
    createTab: document.getElementById('create-tab'),
    joinTab: document.getElementById('join-tab'),
    wordsValue: document.getElementById('words-value'),
    cluesValue: document.getElementById('clues-value'),
    timeValue: document.getElementById('time-value'),
    createBtn: document.getElementById('create-btn'),
    gameIdInput: document.getElementById('game-id-input'),
    joinBtn: document.getElementById('join-btn'),
    
    // Waiting
    displayGameId: document.getElementById('display-game-id'),
    copyIdBtn: document.getElementById('copy-id-btn'),
    startGameBtn: document.getElementById('start-game-btn'),
    
    // Game
    timerDisplay: document.getElementById('timer-display'),
    cluesDisplay: document.getElementById('clues-display'),
    roleBadge: document.getElementById('role-badge'),
    wordPanel: document.getElementById('word-panel'),
    wordList: document.getElementById('word-list'),
    passBtn: document.getElementById('pass-btn'),
    chatHistory: document.getElementById('chat-history'),
    speakerInput: document.getElementById('speaker-input'),
    guesserInput: document.getElementById('guesser-input'),
    clueInput: document.getElementById('clue-input'),
    guessInput: document.getElementById('guess-input'),
    sendClueBtn: document.getElementById('send-clue-btn'),
    sendGuessBtn: document.getElementById('send-guess-btn'),
    
    // Result
    resultIcon: document.getElementById('result-icon'),
    resultTitle: document.getElementById('result-title'),
    resultMessage: document.getElementById('result-message'),
    wordsGuessed: document.getElementById('words-guessed'),
    cluesUsed: document.getElementById('clues-used'),
    timeUsed: document.getElementById('time-used'),
    revealWords: document.getElementById('reveal-words'),
    playAgainBtn: document.getElementById('play-again-btn'),
    
    // Toast
    toast: document.getElementById('toast')
};

// ========================================
// SCREEN MANAGEMENT
// ========================================

function showScreen(screenName) {
    Object.values(screens).forEach(s => s.classList.remove('active'));
    screens[screenName].classList.add('active');
}

// ========================================
// TOAST NOTIFICATIONS
// ========================================

function showToast(message, type = 'info') {
    elements.toast.textContent = message;
    elements.toast.className = `toast ${type}`;
    elements.toast.classList.remove('hidden');
    
    setTimeout(() => {
        elements.toast.classList.add('hidden');
    }, 3000);
}

// ========================================
// TAB SWITCHING
// ========================================

elements.tabs.forEach(tab => {
    tab.addEventListener('click', () => {
        elements.tabs.forEach(t => t.classList.remove('active'));
        tab.classList.add('active');
        
        const tabName = tab.dataset.tab;
        elements.createTab.classList.toggle('active', tabName === 'create');
        elements.joinTab.classList.toggle('active', tabName === 'join');
    });
});

// ========================================
// PARAMETER CONTROLS
// ========================================

document.querySelectorAll('.param-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        const param = btn.dataset.param;
        const action = btn.dataset.action;
        
        const limits = {
            words: { min: 3, max: 20, step: 1 },
            clues: { min: 10, max: 50, step: 5 },
            time: { min: 60, max: 600, step: 30 }
        };
        
        const limit = limits[param];
        
        if (action === 'increase' && params[param] < limit.max) {
            params[param] += limit.step;
        } else if (action === 'decrease' && params[param] > limit.min) {
            params[param] -= limit.step;
        }
        
        const valueEl = document.getElementById(`${param}-value`);
        valueEl.textContent = params[param];
    });
});

// ========================================
// GAME CREATION & JOINING
// ========================================

elements.createBtn.addEventListener('click', () => {
    socket.emit('createGame', {
        wordCount: params.words,
        clueCount: params.clues,
        timeLimit: params.time
    });
});

elements.joinBtn.addEventListener('click', () => {
    const gameId = elements.gameIdInput.value.toUpperCase().trim();
    if (gameId.length !== 6) {
        showToast('Please enter a valid 6-character Game ID', 'error');
        return;
    }
    socket.emit('joinGame', gameId);
});

elements.gameIdInput.addEventListener('input', (e) => {
    e.target.value = e.target.value.toUpperCase().replace(/[^A-Z0-9]/g, '');
});

// ========================================
// WAITING ROOM
// ========================================

elements.copyIdBtn.addEventListener('click', () => {
    navigator.clipboard.writeText(gameState.gameId);
    showToast('Game ID copied to clipboard!', 'success');
});

elements.startGameBtn.addEventListener('click', () => {
    socket.emit('startGame');
});

// ========================================
// GAME CONTROLS
// ========================================

// Clue input
elements.sendClueBtn.addEventListener('click', sendClue);
elements.clueInput.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') sendClue();
});

function sendClue() {
    const clueText = elements.clueInput.value.trim();
    if (!clueText) return;
    
    socket.emit('sendClue', clueText);
    elements.clueInput.value = '';
}

// Guess input
elements.sendGuessBtn.addEventListener('click', sendGuess);
elements.guessInput.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') sendGuess();
});

function sendGuess() {
    const guessText = elements.guessInput.value.trim();
    if (!guessText) return;
    
    socket.emit('makeGuess', guessText);
    elements.guessInput.value = '';
}

// Pass button
elements.passBtn.addEventListener('click', () => {
    socket.emit('passWord');
});

// Play again
elements.playAgainBtn.addEventListener('click', () => {
    location.reload();
});

// ========================================
// UI RENDERING
// ========================================

function formatTime(seconds) {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
}

function updateTimer(seconds) {
    gameState.timeRemaining = seconds;
    elements.timerDisplay.textContent = formatTime(seconds);
    
    elements.timerDisplay.classList.remove('warning', 'danger');
    if (seconds <= 30) {
        elements.timerDisplay.classList.add('danger');
    } else if (seconds <= 60) {
        elements.timerDisplay.classList.add('warning');
    }
}

function updateClues(count) {
    gameState.cluesRemaining = count;
    elements.cluesDisplay.textContent = count;
}

function renderWordList() {
    elements.wordList.innerHTML = '';
    
    if (gameState.role === 'speaker') {
        gameState.words.forEach((word, index) => {
            const isActive = index === gameState.currentWordIndex && !gameState.guessedWords.has(index);
            const isGuessed = gameState.guessedWords.has(index);
            const isPassed = gameState.passedWords.includes(index);
            
            const div = document.createElement('div');
            div.className = `word-item${isActive ? ' active' : ''}${isGuessed ? ' guessed' : ''}${isPassed ? ' passed' : ''}`;
            div.innerHTML = `
                <span class="word-number">${index + 1}</span>
                <span class="word-text">${word}</span>
                <span class="word-status">${isGuessed ? '<svg class="icon"><use href="#icon-check"/></svg>' : isActive ? '<svg class="icon"><use href="#icon-arrow"/></svg>' : ''}</span>
            `;
            elements.wordList.appendChild(div);
        });
    } else {
        // Guesser view - masked words
        for (let i = 0; i < gameState.wordCount; i++) {
            const isGuessed = gameState.guessedWords.has(i);
            
            const div = document.createElement('div');
            div.className = `word-item${isGuessed ? ' guessed' : ''}`;
            div.innerHTML = `
                <span class="word-number">${i + 1}</span>
                <span class="word-text ${isGuessed ? '' : 'masked'}">${isGuessed ? gameState.words[i] || '???' : 'Word ' + (i + 1)}</span>
                <span class="word-status">${isGuessed ? '<svg class="icon"><use href="#icon-check"/></svg>' : ''}</span>
            `;
            elements.wordList.appendChild(div);
        }
    }
}

function addChatMessage(type, text) {
    // Remove placeholder if exists
    const placeholder = elements.chatHistory.querySelector('.chat-placeholder');
    if (placeholder) placeholder.remove();
    
    const div = document.createElement('div');
    div.className = `chat-message ${type}`;
    
    let label = 'Clue';
    if (type === 'guess-correct') label = 'Correct!';
    else if (type === 'guess-incorrect') label = 'Guess';
    
    div.innerHTML = `
        <div class="chat-label">${label}</div>
        <div class="chat-text">${text}</div>
    `;
    
    elements.chatHistory.appendChild(div);
    elements.chatHistory.scrollTop = elements.chatHistory.scrollHeight;
}

function showResult(result) {
    const isVictory = result.victory;
    
    // Update icon - trophy for victory, X for defeat
    elements.resultIcon.innerHTML = isVictory 
        ? '<svg class="icon icon-xl"><use href="#icon-trophy"/></svg>' 
        : '<svg class="icon icon-xl"><use href="#icon-x"/></svg>';
    elements.resultIcon.classList.toggle('defeat', !isVictory);
    elements.resultTitle.textContent = isVictory ? 'Victory!' : 'Game Over';
    
    const messages = {
        victory: 'Congratulations! You guessed all the words!',
        timeout: 'Time ran out!',
        noClues: 'You ran out of clues!',
        disconnect: 'The other player disconnected.'
    };
    elements.resultMessage.textContent = messages[result.reason] || 'Game ended.';
    
    document.querySelector('.result-card').classList.toggle('defeat', !isVictory);
    
    elements.wordsGuessed.textContent = `${result.wordsGuessed}/${result.totalWords}`;
    elements.cluesUsed.textContent = result.cluesUsed;
    elements.timeUsed.textContent = formatTime(result.timeUsed);
    
    // Reveal words
    elements.revealWords.innerHTML = '';
    result.words.forEach((word, index) => {
        const span = document.createElement('span');
        span.className = `reveal-word${gameState.guessedWords.has(index) ? ' guessed' : ''}`;
        span.textContent = word;
        elements.revealWords.appendChild(span);
    });
    
    showScreen('result');
}

// ========================================
// SOCKET EVENT HANDLERS
// ========================================

socket.on('gameCreated', (data) => {
    gameState.gameId = data.gameId;
    gameState.role = data.role;
    
    elements.displayGameId.textContent = data.gameId;
    showScreen('waiting');
});

socket.on('gameJoined', (data) => {
    gameState.gameId = data.gameId;
    gameState.role = data.role;
    
    showToast('Joined game! Waiting for host to start...', 'success');
    showScreen('waiting');
    elements.startGameBtn.style.display = 'none';
});

socket.on('playerJoined', () => {
    showToast('Player 2 has joined!', 'success');
    elements.startGameBtn.disabled = false;
});

socket.on('gameStarted', (state) => {
    gameState.role = state.role;
    gameState.cluesRemaining = state.cluesRemaining;
    gameState.timeRemaining = state.timeRemaining;
    gameState.guessedWords = new Set(state.guessedWords);
    
    if (state.role === 'speaker') {
        gameState.words = state.words;
        gameState.currentWordIndex = state.currentWordIndex;
        gameState.wordCount = state.words.length;
    } else {
        gameState.wordCount = state.wordCount;
        gameState.words = [];
    }
    
    // Setup UI based on role
    elements.roleBadge.textContent = state.role.toUpperCase();
    elements.roleBadge.classList.toggle('guesser', state.role === 'guesser');
    
    elements.speakerInput.classList.toggle('hidden', state.role !== 'speaker');
    elements.guesserInput.classList.toggle('hidden', state.role !== 'guesser');
    elements.passBtn.classList.toggle('hidden', state.role !== 'speaker');
    
    updateTimer(state.timeRemaining);
    updateClues(state.cluesRemaining);
    renderWordList();
    
    showScreen('game');
    
    // Focus appropriate input
    if (state.role === 'speaker') {
        elements.clueInput.focus();
    } else {
        elements.guessInput.focus();
    }
});

socket.on('timerUpdate', (seconds) => {
    updateTimer(seconds);
});

socket.on('clueReceived', (data) => {
    addChatMessage('clue', data.words.join(' '));
    updateClues(data.cluesRemaining);
});

socket.on('correctGuess', (data) => {
    addChatMessage('guess-correct', data.word);
    gameState.guessedWords = new Set(data.guessedWords);
    gameState.currentWordIndex = data.currentWordIndex;
    
    // Update guesser's word list with revealed word
    if (gameState.role === 'guesser') {
        gameState.words[data.wordIndex] = data.word;
    }
    
    renderWordList();
    showToast('Correct!', 'success');
});

socket.on('incorrectGuess', (data) => {
    addChatMessage('guess-incorrect', data.word);
});

socket.on('wordPassed', (data) => {
    gameState.currentWordIndex = data.currentWordIndex;
    gameState.passedWords = data.passedWords;
    renderWordList();
    showToast('Word passed', 'info');
});

socket.on('gameEnded', (result) => {
    showResult(result);
});

socket.on('error', (data) => {
    showToast(data.message, 'error');
});

// Initial state
showScreen('landing');
