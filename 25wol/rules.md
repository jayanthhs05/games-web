# 25 Words or Less - Game Rules

## Overview
A real-time, two-player cooperative word-guessing game. One player (Speaker) gives clue words to help the other (Guesser) identify secret words within time and clue budgets.

---

## Game Parameters

| Parameter | Description | Default |
|-----------|-------------|---------|
| **W** (Words) | Number of words to guess | 10 |
| **C** (Clues) | Total clue words the Speaker can use | 25 |
| **T** (Timer) | Time limit in seconds | 180 |

**Word Source:** All target words come from `words.txt` (one lowercase word per line).

---

## Player Roles

### Speaker (S)
- **Sees:** Full list of all W words with the "Active" word highlighted
- **Input:** Text box for typing clue words (lowercase letters and apostrophes only)
- **Controls:** PASS button to skip the current word
- **Restrictions:**
  - Every clue must be a valid English dictionary word
  - Cannot type the target word itself

### Guesser (G)
- **Sees:** Masked word list ("Word 1", "Word 2"...) until successfully guessed
- **Input:** Text box to guess the current "Active" word
- **Display:** Real-time clue words and guess history (shared with Speaker)

### Guess History
- All guesses are visible to **both players**
- **Green:** Correct guesses
- **Red:** Incorrect guesses

---

## Game Mechanics

### Word Cycling
- Words are revealed to the Speaker one at a time
- Clicking "PASS" moves the current word to the back of the queue
- Game continues until all words are marked "Guessed"

### Clue Deduction
- When the Speaker submits clue(s), each word (space-separated) costs 1 clue
- C decreases by the number of words sent

### System-Validated Guessing
- The system monitors the Guesser's input automatically
- Exact match with the Active word → word marked "Guessed"
- W decreases by 1 and advances to the next word

### Real-Time Sync
Both players see the same:
- Timer (T)
- Remaining clue count (C)
- Chat history (all clues sent)

---

## Win/Loss Conditions

| Result | Condition |
|--------|-----------|
| **Victory** | W reaches 0 while T > 0 and C ≥ 0 |
| **Defeat** | T reaches 0 before all words guessed |
| **Defeat** | C reaches 0 before all words guessed |

---

## Input Rules

- All inputs are forced to **lowercase**
- Only **letters** and **apostrophes** are allowed
- Speaker clues are validated against an English dictionary
- Speaker cannot type the target word as a clue
