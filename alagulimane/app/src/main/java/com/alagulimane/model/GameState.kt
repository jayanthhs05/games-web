package com.alagulimane.model

/**
 * Represents a player in the game
 */
enum class Player {
    PLAYER_ONE,  // Bottom row (index 7-13)
    PLAYER_TWO   // Top row (index 0-6)
}

/**
 * Represents a single hole on the board
 * 
 * Board layout (as seen on screen):
 *   Top row (Player 2):    [0] [1] [2] [3] [4] [5] [6]
 *   Bottom row (Player 1): [7] [8] [9] [10][11][12][13]
 * 
 * Counter-clockwise sowing:
 *   Bottom: 7 → 8 → 9 → 10 → 11 → 12 → 13 → (wrap to top-right)
 *   Top:    6 → 5 → 4 → 3 → 2 → 1 → 0 → (wrap to bottom-left)
 */
data class Hole(
    val index: Int,
    val seedCount: Int = 5,
    val owner: Player = if (index < 7) Player.PLAYER_TWO else Player.PLAYER_ONE,
    val isPauper: Boolean = false
)

/**
 * Represents the complete game state
 */
data class GameState(
    val holes: List<Hole> = createInitialBoard(),
    val currentPlayer: Player = Player.PLAYER_ONE,
    val capturedSeeds: Map<Player, Int> = mapOf(
        Player.PLAYER_ONE to 0,
        Player.PLAYER_TWO to 0
    ),
    val roundNumber: Int = 1,
    val isGameOver: Boolean = false,
    val winner: Player? = null,
    val seedsInHand: Int = 0,
    val currentHoleIndex: Int = -1,
    val isAnimating: Boolean = false,
    val message: String = "Player 1's turn - Select a hole"
) {
    companion object {
        /**
         * Creates the initial board with 5 seeds in each hole
         */
        fun createInitialBoard(): List<Hole> {
            return (0 until 14).map { index ->
                Hole(
                    index = index,
                    seedCount = 5,
                    owner = if (index < 7) Player.PLAYER_TWO else Player.PLAYER_ONE
                )
            }
        }
    }
    
    /**
     * Gets the opposite hole index for a given hole
     * Opposite means across the board:
     * 0 ↔ 13, 1 ↔ 12, 2 ↔ 11, 3 ↔ 10, 4 ↔ 9, 5 ↔ 8, 6 ↔ 7
     */
    fun getOppositeHoleIndex(index: Int): Int {
        return if (index < 7) {
            13 - index  // Top row: 0→13, 1→12, 2→11, etc.
        } else {
            13 - index + 7  // Actually: 7↔6, 8↔5, 9↔4, 10↔3, 11↔2, 12↔1, 13↔0
            // Simpler: bottom index - 7 gives 0-6, then we want 6-0, so 6 - (index-7) = 13 - index
        }
        // Actually the simple formula works: 13 - index gives wrong results for some
        // Let's be explicit:
        // 0 ↔ 7 (directly across), 1 ↔ 8, 2 ↔ 9, 3 ↔ 10, 4 ↔ 11, 5 ↔ 12, 6 ↔ 13
        // So: if index < 7, opposite = index + 7; else opposite = index - 7
    }
    
    /**
     * Gets the opposite hole - directly across the board
     */
    fun getOpposite(index: Int): Int {
        return if (index < 7) index + 7 else index - 7
    }
    
    /**
     * Checks if a hole belongs to the current player
     */
    fun isCurrentPlayerHole(index: Int): Boolean {
        return holes[index].owner == currentPlayer
    }
    
    /**
     * Gets the next hole index in COUNTER-CLOCKWISE direction (same for both players)
     * 
     * Counter-clockwise movement around the board:
     * Bottom row goes RIGHT: 7 → 8 → 9 → 10 → 11 → 12 → 13
     * Then wraps to top-right: 13 → 6
     * Top row goes LEFT: 6 → 5 → 4 → 3 → 2 → 1 → 0
     * Then wraps to bottom-left: 0 → 7
     */
    fun getNextHoleIndex(currentIndex: Int, sowingPlayer: Player): Int {
        // Counter-clockwise is the same direction for both players
        return when (currentIndex) {
            // Bottom row: go right
            in 7..12 -> currentIndex + 1
            // End of bottom row: wrap to top-right
            13 -> 6
            // Top row: go left
            in 1..6 -> currentIndex - 1
            // End of top row: wrap to bottom-left
            0 -> 7
            else -> currentIndex + 1
        }
    }
    
    /**
     * Calculate total seeds in the game (should always be 70)
     */
    fun getTotalSeeds(): Int {
        val onBoard = holes.sumOf { it.seedCount }
        val captured = (capturedSeeds[Player.PLAYER_ONE] ?: 0) + (capturedSeeds[Player.PLAYER_TWO] ?: 0)
        return onBoard + captured + seedsInHand
    }
}

/**
 * Sealed class representing game events/actions
 */
sealed class GameAction {
    data class SelectHole(val holeIndex: Int) : GameAction()
    object NewGame : GameAction()
    object NewRound : GameAction()
    object Undo : GameAction()
}
