const express = require('express');
const http = require('http');
const { Server } = require('socket.io');
const fs = require('fs');
const path = require('path');

const app = express();
const server = http.createServer(app);
const io = new Server(server);

// Serve static files from public directory
app.use(express.static(path.join(__dirname, 'public')));

// Load words from words.txt
const words = fs.readFileSync(path.join(__dirname, 'words.txt'), 'utf-8')
    .split('\n')
    .map(w => w.trim().toLowerCase())
    .filter(w => w.length > 0);

// Game sessions storage
const games = new Map();

// Generate unique 6-character Game ID
function generateGameId() {
    const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789';
    let id;
    do {
        id = '';
        for (let i = 0; i < 6; i++) {
            id += chars[Math.floor(Math.random() * chars.length)];
        }
    } while (games.has(id));
    return id;
}

// Get random words for a game
function getRandomWords(count) {
    const shuffled = [...words].sort(() => Math.random() - 0.5);
    return shuffled.slice(0, count);
}

// Socket.IO connection handling
io.on('connection', (socket) => {
    console.log('Player connected:', socket.id);
    
    let currentGameId = null;
    let currentRole = null;

    // Create new game
    socket.on('createGame', (params) => {
        const gameId = generateGameId();
        const wordCount = params.wordCount || 10;
        const clueCount = params.clueCount || 25;
        const timeLimit = params.timeLimit || 180;
        
        const game = {
            id: gameId,
            params: { wordCount, clueCount, timeLimit },
            words: getRandomWords(wordCount),
            currentWordIndex: 0,
            guessedWords: new Set(),
            passedWords: [],
            cluesRemaining: clueCount,
            timeRemaining: timeLimit,
            speaker: socket.id,
            guesser: null,
            started: false,
            ended: false,
            history: [],
            timerInterval: null
        };
        
        games.set(gameId, game);
        socket.join(gameId);
        currentGameId = gameId;
        currentRole = 'speaker';
        
        socket.emit('gameCreated', { gameId, role: 'speaker' });
        console.log(`Game ${gameId} created by ${socket.id}`);
    });

    // Join existing game
    socket.on('joinGame', (gameId) => {
        const game = games.get(gameId);
        
        if (!game) {
            socket.emit('error', { message: 'Game not found' });
            return;
        }
        
        if (game.guesser) {
            socket.emit('error', { message: 'Game is full' });
            return;
        }
        
        game.guesser = socket.id;
        socket.join(gameId);
        currentGameId = gameId;
        currentRole = 'guesser';
        
        socket.emit('gameJoined', { gameId, role: 'guesser' });
        io.to(game.speaker).emit('playerJoined');
        
        console.log(`Player ${socket.id} joined game ${gameId} as guesser`);
    });

    // Start game
    socket.on('startGame', () => {
        const game = games.get(currentGameId);
        if (!game || game.started) return;
        if (socket.id !== game.speaker) return;
        if (!game.guesser) {
            socket.emit('error', { message: 'Waiting for another player' });
            return;
        }
        
        game.started = true;
        
        // Send initial state to both players
        const speakerState = {
            words: game.words,
            currentWordIndex: game.currentWordIndex,
            guessedWords: Array.from(game.guessedWords),
            cluesRemaining: game.cluesRemaining,
            timeRemaining: game.timeRemaining,
            history: game.history,
            role: 'speaker'
        };
        
        const guesserState = {
            wordCount: game.words.length,
            guessedWords: Array.from(game.guessedWords),
            cluesRemaining: game.cluesRemaining,
            timeRemaining: game.timeRemaining,
            history: game.history,
            role: 'guesser'
        };
        
        io.to(game.speaker).emit('gameStarted', speakerState);
        io.to(game.guesser).emit('gameStarted', guesserState);
        
        // Start timer
        game.timerInterval = setInterval(() => {
            if (game.ended) {
                clearInterval(game.timerInterval);
                return;
            }
            
            game.timeRemaining--;
            io.to(currentGameId).emit('timerUpdate', game.timeRemaining);
            
            if (game.timeRemaining <= 0) {
                clearInterval(game.timerInterval);
                endGame(game, 'timeout');
            }
        }, 1000);
        
        console.log(`Game ${currentGameId} started`);
    });

    // Speaker sends clue
    socket.on('sendClue', (clueText) => {
        const game = games.get(currentGameId);
        if (!game || !game.started || game.ended) return;
        if (socket.id !== game.speaker) return;
        
        // Parse clue words
        const clueWords = clueText.toLowerCase()
            .replace(/[^a-z' ]/g, '')
            .split(/\s+/)
            .filter(w => w.length > 0);
        
        if (clueWords.length === 0) {
            socket.emit('error', { message: 'Clue must contain at least one letter word.' });
            return;
        }

        // Check clue budget
        if (clueWords.length > game.cluesRemaining) {
            socket.emit('error', { message: `Not enough clues! You have ${game.cluesRemaining} remaining.` });
            return;
        }
        
        // Get current active word
        const activeWord = getActiveWord(game);
        
        // Check if any clue word is the target word
        if (clueWords.includes(activeWord)) {
            socket.emit('error', { message: 'Cannot use the target word as a clue!' });
            return;
        }
        
        // Deduct clues and broadcast
        game.cluesRemaining -= clueWords.length;
        
        const historyEntry = {
            type: 'clue',
            words: clueWords,
            timestamp: Date.now()
        };
        game.history.push(historyEntry);
        
        io.to(currentGameId).emit('clueReceived', {
            words: clueWords,
            cluesRemaining: game.cluesRemaining
        });
        
        // Check for clue exhaustion
        if (game.cluesRemaining <= 0 && !allWordsGuessed(game)) {
            endGame(game, 'noClues');
        }
        
        console.log(`Clue sent in game ${currentGameId}: ${clueWords.join(', ')}`);
    });

    // Guesser makes a guess
    socket.on('makeGuess', (guessText) => {
        const game = games.get(currentGameId);
        if (!game || !game.started || game.ended) return;
        if (socket.id !== game.guesser) return;
        
        const guess = guessText.toLowerCase().replace(/[^a-z']/g, '').trim();
        if (!guess) return;
        
        const activeWord = getActiveWord(game);
        const isCorrect = guess === activeWord;
        
        const historyEntry = {
            type: 'guess',
            word: guess,
            correct: isCorrect,
            timestamp: Date.now()
        };
        game.history.push(historyEntry);
        
        if (isCorrect) {
            // Mark word as guessed
            const wordIndex = game.words.indexOf(activeWord);
            game.guessedWords.add(wordIndex);
            
            // Advance to next unguessed word
            advanceToNextWord(game);
            
            io.to(currentGameId).emit('correctGuess', {
                word: activeWord,
                wordIndex: wordIndex,
                guessedWords: Array.from(game.guessedWords),
                currentWordIndex: game.currentWordIndex
            });
            
            // Check for victory
            if (allWordsGuessed(game)) {
                endGame(game, 'victory');
            }
        } else {
            io.to(currentGameId).emit('incorrectGuess', { word: guess });
        }
        
        console.log(`Guess in game ${currentGameId}: ${guess} (${isCorrect ? 'correct' : 'incorrect'})`);
    });

    // Speaker passes current word
    socket.on('passWord', () => {
        const game = games.get(currentGameId);
        if (!game || !game.started || game.ended) return;
        if (socket.id !== game.speaker) return;
        
        const activeWord = getActiveWord(game);
        if (!activeWord) return;
        
        // Move current word to passed list
        game.passedWords.push(game.currentWordIndex);
        advanceToNextWord(game);
        
        io.to(game.speaker).emit('wordPassed', {
            currentWordIndex: game.currentWordIndex,
            passedWords: game.passedWords
        });
        
        console.log(`Word passed in game ${currentGameId}`);
    });

    // Handle disconnect
    socket.on('disconnect', () => {
        console.log('Player disconnected:', socket.id);
        
        if (currentGameId) {
            const game = games.get(currentGameId);
            if (game && !game.ended) {
                endGame(game, 'disconnect');
            }
        }
    });
    
    // Helper functions
    function getActiveWord(game) {
        // Find next unguessed word starting from currentWordIndex
        for (let i = 0; i < game.words.length; i++) {
            const index = (game.currentWordIndex + i) % game.words.length;
            if (!game.guessedWords.has(index)) {
                return game.words[index];
            }
        }
        return null;
    }
    
    function advanceToNextWord(game) {
        // Find next unguessed word
        for (let i = 1; i <= game.words.length; i++) {
            const index = (game.currentWordIndex + i) % game.words.length;
            if (!game.guessedWords.has(index)) {
                game.currentWordIndex = index;
                return;
            }
        }
    }
    
    function allWordsGuessed(game) {
        return game.guessedWords.size >= game.words.length;
    }
    
    function endGame(game, reason) {
        if (game.ended) return;
        
        game.ended = true;
        if (game.timerInterval) {
            clearInterval(game.timerInterval);
        }
        
        const result = {
            reason: reason,
            victory: reason === 'victory',
            wordsGuessed: game.guessedWords.size,
            totalWords: game.words.length,
            cluesUsed: game.params.clueCount - game.cluesRemaining,
            timeUsed: game.params.timeLimit - game.timeRemaining,
            words: game.words
        };
        
        io.to(game.id).emit('gameEnded', result);
        
        // Clean up game after delay
        setTimeout(() => {
            games.delete(game.id);
        }, 60000);
        
        console.log(`Game ${game.id} ended: ${reason}`);
    }
});

const PORT = process.env.PORT || 3000;
server.listen(PORT, () => {
    console.log(`25 Words or Less server running on http://localhost:${PORT}`);
});
