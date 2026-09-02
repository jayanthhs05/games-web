import colorama, os, random  # type: ignore

def get_words(file: str):
    """Gets all the words of specified length from that file."""
    words = open(file, "r").read().split("\n")
    matches = [word for word in words if len(word) == LEN_WORD]
    print()
    return matches

def choose_word(matches):
    """Chooses a random word"""
    return random.choice(matches)

def mark_word(guess: str, ref: str) -> tuple[str, bool]:
    """Marks the guess against the reference word."""
    if len(guess) != len(ref):
        return f"Please make sure that the word is of {len(ref)} letters!", False

    guess, ref = list(guess.lower()), list(ref.lower())
    marked = ["*"] * len(guess)

    chars = {}
    for ch in ref:
        chars[ch] = chars.get(ch, 0) + 1

    for i, ch in enumerate(guess):
        if not ch.isalpha():
            return "Please make sure that you enter letters only!", False
        if ref[i] == guess[i]:
            marked[i] = "G"
            chars[ch] -= 1

    for i, ch in enumerate(guess):
        if marked[i] == "G":
            continue
        elif ch in chars and chars[ch] > 0:
            marked[i] = "Y"
            chars[ch] -= 1
        else:
            marked[i] = "B"

    return "".join(marked), True

def print_static():
    """Prints static information on top of screen."""
    global NAME
    os.system("cls" if os.name == "nt" else "clear")
    print(colorama.Fore.WHITE, "Wordle by Jayanth", sep="")
    print(f"{NAME}'s Game {GAMES_PLAYED+1}, Wins: {GAMES_WON}/{GAMES_PLAYED}, To guess: {LEN_WORD}-letter word")

def color(guess: str, marked: str):
    """Colors and prints the guess."""
    guess, marked = list(guess), list(marked)
    for g, m in zip(guess, marked):
        if m == 'G':
            print(colorama.Fore.GREEN + g, end="")
        elif m == 'Y':
            print(colorama.Fore.YELLOW + g, end="")
        else:
            print(colorama.Fore.WHITE + g, end="")
    print(colorama.Fore.WHITE, end="")

def process(marked: str, guess: str, ref: str, chance: int, guesses: list[str], marked_guesses: list[str]):
    """Processes the marked guess and checks game status."""
    global GAMES_PLAYED, GAMES_WON
    print_static()
    for i, (past_guess, marked_guess) in enumerate(zip(guesses, marked_guesses)):
        print(f"Guess {i+1}/6: ", end="")
        color(past_guess, marked_guess)
        print()
    if marked == "G" * len(marked):
        print("You win!")
        GAMES_WON += 1
        GAMES_PLAYED += 1
        return True
    elif chance == 6:
        print("Game over! The word was:", ref)
        GAMES_PLAYED += 1
    return False

def game(ref: str):
    """Plays a single game."""
    guesses, marked_guesses = [], []
    chance = 1
    print_static()
    while chance <= NUM_CHANCES:
        guess = input(f"Guess {chance}/6: ")
        marked, valid = mark_word(guess, ref)
        if valid:
            guesses.append(guess)
            marked_guesses.append(marked)
            won = process(marked, guess, ref, chance, guesses, marked_guesses)
            chance += 1
            if won:
                return True
        else:
            print(f"ERROR: {marked}")
    return False

LEN_WORD = 5
NUM_CHANCES = 6
GAMES_PLAYED = 0
GAMES_WON = 0
NAME = ""

def main():
    global NAME, LEN_WORD
    NAME = input("Enter Name: ").strip().title()
    if not NAME:
        NAME = "Player"
    LEN_WORD = input("Enter the length of the word you want to guess (5 or 6): ").strip()
    while not LEN_WORD.isnumeric() or int(LEN_WORD) not in [5, 6]:
        LEN_WORD = input("Enter the length of the word you want to guess (5 or 6): ").strip()
    LEN_WORD = int(LEN_WORD)
    words = get_words("words.txt")
    while True:
        ref = choose_word(words)
        won = game(ref)
        inp = input("Play again? Y/n: ").strip().lower()
        while inp not in ["y", "n", ""]:
            inp = input("Play again? Y/n: ").strip().lower()
        if inp == "n":
            break

main()