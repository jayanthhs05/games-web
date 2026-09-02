package com.alagulimane.viewmodel

import com.alagulimane.model.GameAction
import com.alagulimane.model.GameState
import com.alagulimane.model.Hole
import com.alagulimane.model.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Holds the Alagulimane game logic and animation state.
 *
 * Total seeds in the game is always 70 (14 holes × 5 seeds). This was an
 * Android `ViewModel`; on Compose Multiplatform it is a plain class that owns
 * its own coroutine scope, created once via `remember { GameViewModel() }`.
 */
class GameViewModel {

    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()
    
    // History stack for undo - stores states within current round
    private val undoHistory = mutableListOf<Pair<GameState, Player>>()
    
    // Can undo if there are states in history
    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()
    
    // Track the player who started sowing (for direction)
    private var sowingPlayer: Player = Player.PLAYER_ONE
    
    // Track who started the current round (for alternating)
    private var roundStarter: Player = Player.PLAYER_ONE
    
    // Animation delay in milliseconds (fast but visible)
    private val dropDelay = 120L
    
    /**
     * Save current state to history before making changes
     */
    private fun saveStateToHistory() {
        undoHistory.add(Pair(_gameState.value.copy(), sowingPlayer))
        _canUndo.value = true
    }
    
    /**
     * Clear undo history (called at start of new round)
     */
    private fun clearUndoHistory() {
        undoHistory.clear()
        _canUndo.value = false
    }
    
    /**
     * Process game actions
     */
    fun onAction(action: GameAction) {
        when (action) {
            is GameAction.SelectHole -> handleHoleSelection(action.holeIndex)
            GameAction.NewGame -> startNewGame()
            GameAction.NewRound -> startNewRound()
            GameAction.Undo -> undoLastMove()
        }
    }
    
    /**
     * Handles when a player selects a hole to pick up seeds
     */
    private fun handleHoleSelection(holeIndex: Int) {
        val currentState = _gameState.value
        
        // Can't select during animation or game over
        if (currentState.isAnimating || currentState.isGameOver) return
        
        // Must be current player's hole
        if (!currentState.isCurrentPlayerHole(holeIndex)) {
            _gameState.update { it.copy(message = "Select a hole from your side!") }
            return
        }
        
        val selectedHole = currentState.holes[holeIndex]
        
        // Can't select empty or pauper holes
        if (selectedHole.seedCount == 0) {
            _gameState.update { it.copy(message = "This hole is empty!") }
            return
        }
        
        if (selectedHole.isPauper) {
            _gameState.update { it.copy(message = "This is a pauper hole!") }
            return
        }
        
        // Pick up seeds and start sowing with animation
        sowingPlayer = currentState.currentPlayer
        val seedsToSow = selectedHole.seedCount
        val newHoles = currentState.holes.toMutableList()
        newHoles[holeIndex] = selectedHole.copy(seedCount = 0)
        
        // Save state before making move (for undo)
        saveStateToHistory()
        
        _gameState.update { 
            it.copy(
                holes = newHoles,
                seedsInHand = seedsToSow,
                currentHoleIndex = holeIndex,
                isAnimating = true,
                message = "Sowing $seedsToSow seeds..."
            )
        }
        
        // Start animated sowing in coroutine
        viewModelScope.launch {
            sowSeedsAnimated()
        }
    }
    
    /**
     * Main sowing logic with animation - drops seeds one by one with delay
     */
    private suspend fun sowSeedsAnimated() {
        var currentState = _gameState.value
        var seedsInHand = currentState.seedsInHand
        var currentIndex = currentState.currentHoleIndex
        val newHoles = currentState.holes.toMutableList()
        val capturedSeeds = currentState.capturedSeeds.toMutableMap()
        
        while (seedsInHand > 0) {
            // Move to next hole (counter-clockwise)
            currentIndex = currentState.getNextHoleIndex(currentIndex, sowingPlayer)
            
            val targetHole = newHoles[currentIndex]
            
            // Skip pauper holes
            if (targetHole.isPauper) {
                continue
            }
            
            // Drop one seed with animation delay
            delay(dropDelay)
            
            seedsInHand--
            val newSeedCount = targetHole.seedCount + 1
            newHoles[currentIndex] = targetHole.copy(seedCount = newSeedCount)
            
            // Update state for visual feedback
            _gameState.update {
                it.copy(
                    holes = newHoles.toList(),
                    capturedSeeds = capturedSeeds.toMap(),
                    currentHoleIndex = currentIndex,
                    seedsInHand = seedsInHand,
                    message = "Dropping... ($seedsInHand left)"
                )
            }
            
            // Check for Karu (exactly 4 seeds) - owner of that hole captures immediately
            if (newSeedCount == 4) {
                delay(dropDelay) // Brief pause for Karu
                val holeOwner = targetHole.owner
                capturedSeeds[holeOwner] = (capturedSeeds[holeOwner] ?: 0) + 4
                newHoles[currentIndex] = newHoles[currentIndex].copy(seedCount = 0)
                
                val ownerName = if (holeOwner == Player.PLAYER_ONE) "P1" else "P2"
                _gameState.update {
                    it.copy(
                        holes = newHoles.toList(),
                        capturedSeeds = capturedSeeds.toMap(),
                        message = "Karu! +4"
                    )
                }
            }
            
            currentState = _gameState.value
        }
        
        // Brief pause before checking next action
        delay(dropDelay)
        
        // Hand is empty - check next hole
        handleEmptyHandAnimated(currentIndex, newHoles, capturedSeeds)
    }
    
    /**
     * Handles the situation when the hand is empty (with animation)
     */
    private suspend fun handleEmptyHandAnimated(
        lastIndex: Int,
        holes: MutableList<Hole>,
        capturedSeeds: MutableMap<Player, Int>
    ) {
        val currentState = _gameState.value
        val nextIndex = currentState.getNextHoleIndex(lastIndex, sowingPlayer)
        val nextHole = holes[nextIndex]
        
        // Skip pauper holes when checking
        if (nextHole.isPauper) {
            var searchIndex = nextIndex
            var foundNonPauper = false
            for (i in 0 until 14) {
                searchIndex = currentState.getNextHoleIndex(searchIndex, sowingPlayer)
                if (!holes[searchIndex].isPauper) {
                    foundNonPauper = true
                    break
                }
            }
            if (!foundNonPauper) {
                endRound(holes, capturedSeeds)
                return
            }
        }
        
        when {
            // Next hole has seeds - pick them up and continue
            nextHole.seedCount > 0 && !nextHole.isPauper -> {
                val seedsToPickUp = nextHole.seedCount
                holes[nextIndex] = nextHole.copy(seedCount = 0)
                
                _gameState.update {
                    it.copy(
                        holes = holes.toList(),
                        seedsInHand = seedsToPickUp,
                        currentHoleIndex = nextIndex,
                        message = "Picking up $seedsToPickUp more"
                    )
                }
                
                delay(dropDelay)
                sowSeedsAnimated()
            }
            
            // Next hole is empty - check for capture (wipe)
            nextHole.seedCount == 0 && !nextHole.isPauper -> {
                val afterNextIndex = currentState.getNextHoleIndex(nextIndex, sowingPlayer)
                val afterNextHole = holes[afterNextIndex]
                
                // Double empty rule - turn ends with no capture
                if (afterNextHole.seedCount == 0 || afterNextHole.isPauper) {
                    endTurn(holes, capturedSeeds, "Empty")
                } else {
                    // Wipe and capture: take seeds from the next non-empty hole
                    // AND from the hole directly opposite to it
                    val oppositeIndex = currentState.getOpposite(afterNextIndex)
                    val oppositeHole = holes[oppositeIndex]
                    
                    var capturedCount = afterNextHole.seedCount
                    holes[afterNextIndex] = afterNextHole.copy(seedCount = 0)
                    
                    // Also capture from opposite hole if not pauper and has seeds
                    if (!oppositeHole.isPauper && oppositeHole.seedCount > 0) {
                        capturedCount += oppositeHole.seedCount
                        holes[oppositeIndex] = oppositeHole.copy(seedCount = 0)
                    }
                    
                    val currentPlayer = currentState.currentPlayer
                    capturedSeeds[currentPlayer] = (capturedSeeds[currentPlayer] ?: 0) + capturedCount
                    
                    endTurn(holes, capturedSeeds, "Won $capturedCount seeds")
                }
            }
            
            else -> {
                endTurn(holes, capturedSeeds, "Turn ends")
            }
        }
    }
    
    /**
     * Ends the current turn and switches to the other player
     */
    private fun endTurn(
        holes: MutableList<Hole>,
        capturedSeeds: MutableMap<Player, Int>,
        message: String
    ) {
        val currentState = _gameState.value
        val nextPlayer = if (currentState.currentPlayer == Player.PLAYER_ONE) 
            Player.PLAYER_TWO else Player.PLAYER_ONE
        
        // Check if the board is empty (round ends)
        val totalSeedsOnBoard = holes.sumOf { it.seedCount }
        if (totalSeedsOnBoard == 0) {
            endRound(holes, capturedSeeds)
            return
        }
        
        // Check if next player has any valid moves
        val nextPlayerHasSeeds = holes.any { 
            it.owner == nextPlayer && it.seedCount > 0 && !it.isPauper 
        }
        
        if (!nextPlayerHasSeeds) {
            // Current player gets remaining seeds on board
            val remainingSeeds = holes.filter { !it.isPauper }.sumOf { it.seedCount }
            capturedSeeds[currentState.currentPlayer] = 
                (capturedSeeds[currentState.currentPlayer] ?: 0) + remainingSeeds
            holes.forEachIndexed { index, hole ->
                if (!hole.isPauper) {
                    holes[index] = hole.copy(seedCount = 0)
                }
            }
            endRound(holes, capturedSeeds)
            return
        }
        
        _gameState.update {
            it.copy(
                holes = holes.toList(),
                capturedSeeds = capturedSeeds.toMap(),
                currentPlayer = nextPlayer,
                seedsInHand = 0,
                currentHoleIndex = -1,
                isAnimating = false,
                message = message
            )
        }
    }
    
    /**
     * Ends the current round and prepares for the next
     * Each player fills their holes with 5 seeds each from their captured seeds
     * Excess seeds carry over, holes that can't be filled become pauper
     */
    private fun endRound(
        holes: MutableList<Hole>,
        capturedSeeds: MutableMap<Player, Int>
    ) {
        val currentState = _gameState.value
        
        // Get total captured by each player
        val player1Total = capturedSeeds[Player.PLAYER_ONE] ?: 0
        val player2Total = capturedSeeds[Player.PLAYER_TWO] ?: 0
        
        // Calculate how many holes each can fill (5 seeds per hole)
        val player1Holes = player1Total / 5
        val player2Holes = player2Total / 5
        
        // Check for game over (can't fill any holes)
        if (player1Holes == 0) {
            _gameState.update {
                it.copy(
                    isGameOver = true,
                    isAnimating = false,
                    winner = Player.PLAYER_TWO,
                    capturedSeeds = mapOf(
                        Player.PLAYER_ONE to player1Total,
                        Player.PLAYER_TWO to player2Total
                    ),
                    message = "Game Over! Player 2 wins!"
                )
            }
            return
        }
        
        if (player2Holes == 0) {
            _gameState.update {
                it.copy(
                    isGameOver = true,
                    isAnimating = false,
                    winner = Player.PLAYER_ONE,
                    capturedSeeds = mapOf(
                        Player.PLAYER_ONE to player1Total,
                        Player.PLAYER_TWO to player2Total
                    ),
                    message = "Game Over! Player 1 wins!"
                )
            }
            return
        }
        
        // Create new board
        val newHoles = mutableListOf<Hole>()
        
        // Player 2's row (top, indices 0-6)
        val player2ActiveHoles = minOf(player2Holes, 7)
        // Excess = total captured - seeds used to fill holes
        val player2Excess = player2Total - (player2ActiveHoles * 5)
        for (i in 0 until 7) {
            newHoles.add(Hole(
                index = i,
                seedCount = if (i < player2ActiveHoles) 5 else 0,
                owner = Player.PLAYER_TWO,
                isPauper = i >= player2ActiveHoles
            ))
        }
        
        // Player 1's row (bottom, indices 7-13)
        val player1ActiveHoles = minOf(player1Holes, 7)
        // Excess = total captured - seeds used to fill holes
        val player1Excess = player1Total - (player1ActiveHoles * 5)
        for (i in 7 until 14) {
            val relativeIndex = i - 7
            newHoles.add(Hole(
                index = i,
                seedCount = if (relativeIndex < player1ActiveHoles) 5 else 0,
                owner = Player.PLAYER_ONE,
                isPauper = relativeIndex >= player1ActiveHoles
            ))
        }
        
        // Alternate who starts the round (opposite of who started last round)
        val nextPlayer = if (roundStarter == Player.PLAYER_ONE) Player.PLAYER_TWO else Player.PLAYER_ONE
        roundStarter = nextPlayer
        
        _gameState.update {
            it.copy(
                holes = newHoles,
                currentPlayer = nextPlayer,
                // Excess seeds carry over to next round
                capturedSeeds = mapOf(
                    Player.PLAYER_ONE to player1Excess,
                    Player.PLAYER_TWO to player2Excess
                ),
                roundNumber = currentState.roundNumber + 1,
                seedsInHand = 0,
                currentHoleIndex = -1,
                isAnimating = false,
                message = "Round ${currentState.roundNumber + 1}"
            )
        }
        
        // Clear undo history at start of new round
        clearUndoHistory()
    }
    
    /**
     * Undo the last move (step by step within the current round)
     */
    private fun undoLastMove() {
        if (undoHistory.isNotEmpty() && !_gameState.value.isAnimating) {
            val (previousState, previousSowingPlayer) = undoHistory.removeLast()
            _gameState.value = previousState
            sowingPlayer = previousSowingPlayer
            _canUndo.value = undoHistory.isNotEmpty()
        }
    }
    
    private fun startNewGame() {
        // Random first player
        val firstPlayer = if (Random.nextBoolean()) Player.PLAYER_ONE else Player.PLAYER_TWO
        roundStarter = firstPlayer
        
        val newState = GameState(
            currentPlayer = firstPlayer,
            message = "Tap a hole to start"
        )
        _gameState.value = newState
        sowingPlayer = firstPlayer
        clearUndoHistory()
    }
    
    private fun startNewRound() {
        val currentState = _gameState.value
        endRound(
            currentState.holes.toMutableList(),
            currentState.capturedSeeds.toMutableMap()
        )
    }
}
