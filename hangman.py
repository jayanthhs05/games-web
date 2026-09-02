import os
import random
import time

easy, medium, hard = [], [], []


def cls():
    """Clear the terminal screen on any OS."""
    os.system("cls" if os.name == "nt" else "clear")


def guessr(word, guess, guessed):
    """function that takes in word, guess and the guessed word and returns the updated guess"""
    guessed = list(guessed)
    for i in range(len(word)):
        if word[i] == guess:
            guessed[i] = guess
    return "".join(guessed)


def dispform(guessed):
    """function that displays the guessed in correct format"""
    temp, guessed = list(guessed), ""
    for i in temp:
        guessed += f"{i} "
    return guessed


def genlists():
    """generates lists of words"""
    jayanth = 0
    for filename in ["words.txt"]:
        with open(filename, "r") as f:
            word = f.readline().strip()
            while word != "":
                if len(word) == 7 or len(word) == 8:
                    easy.append(word)
                    jayanth += 1
                elif len(word) == 9 or len(word) == 10:
                    medium.append(word)
                    jayanth += 1
                elif len(word) > 10:
                    hard.append(word)
                    jayanth += 1
                word = f.readline().strip()
    # print(jayanth)
        


def genword(l):
    """function that generates random word"""
    genlists()
    match (l):
        case 1:
            lst = easy
        case 2:
            lst = medium
        case 3:
            lst = hard
    word = random.choice(lst)
    return word.lower()


def points(result, l):
    """returns points based on tries left and level"""
    if result == 0:
        return int(0)
    result = 0.3 + result * 0.1
    if l == 1:
        return int(30 * result)
    if l == 2:
        return int(50 * result)
    if l == 3:
        return int(100 * result)


def intro(name):
    """intro of person, rules"""
    print(f"Hey {name}! Welcome to Hangman by Jayanth!")
    print("\nRULES: ")
    print("a. An easy game is worth 30 points; medium, 50 points and hard, 100 points.")
    print("b. You get 7 tries to guess a word, letter by letter.")
    print(
        "c. For each mistake, you lose 10% of points. If you lose, you do not get points."
    )
    print()


def play():
    """asks for name and calls functions"""
    name = input("Name: ").title()
    intro(name)
    games(name)


def levels():
    """gets user input for the level"""
    print("LEVELS")
    print("1. Easy")
    print("2. Medium")
    print("3. Hard")
    s = input("Level: ")
    while s not in ["1", "2", "3"]:
        s = input("Invalid choice. Level: ")
    print()
    return int(s)


def number():
    """gets number of games user wants to play"""
    print("NUMBER OF GAMES")
    print("You can play up to 5 games!")
    n = input("Number of games: ")
    while n not in list("12345"):
        n = input("Invalid number. Number of games: ")
    print()
    return int(n)


def victory(word, mistakes, level):
    """functions that are called on victory/defeat"""
    pt = int(points(mistakes, level))
    print(f"Correctly guessed! +{pt} points!")
    print(f'The word was "{word}"')


def defeat(word):
    """functions that are called on victory/defeat"""
    print("Better luck next time!")
    print(f'The word was "{word}"')


def countdown(s):
    """used to count down at the end of the """
    print(f"{s} in", end=" ")
    for i in range(5, 0, -1):
        print(i, end=" ")
        time.sleep(1)
        print("\b\b", end="", flush=True)
    cls()


def disp_top(name, game, total, level, guessed, points, mistakes, lst=[]):
    print(f"{name}'s Game #{game} out of {total}")
    print(f"Level: {level}, Points: {points}, Tries left: {mistakes}")
    if lst != []:
        print("Letters guessed:", end=" ")
        for i in lst:
            print(i, end=" ")
        print()
    print(f"Current word: {dispform(guessed)}")


def get_in(name, game, total, level, guessed, points, mistakes, lst=[]):
    """function that gets the user input as a single alphabetical character, takes a key"""
    s = input("New guess: ").strip().lower()
    cls()
    disp_top(name, game, total, level, guessed, points, mistakes, lst)
    while not (len(s) == 1 and s >= "a" and s <= "z" and s not in lst):
        if s in lst:
            print("Already guessed.", end=" ")
        else:
            print("Invalid character.", end=" ")
        s = input("New guess: ").strip().lower()
        cls()
        disp_top(name, game, total, level, guessed, points, mistakes, lst)
    return s


def userin(name, game, total, level, guessed, points, mistakes, word, lst=[]):
    """function that takes in word, gets user input and calls guessr"""
    guessed, guesses, mistakes = "_" * len(word), [], 7
    disp_top(name, game, total, level, guessed, points, mistakes, guesses)
    while mistakes != 0:
        guess = get_in(name, game, total, level, guessed, points, mistakes, guesses)
        guesses.append(guess)
        new_guessed = guessr(word, guess, guessed)
        if guessed != new_guessed:
            guessed = new_guessed
        else:
            mistakes -= 1
        cls()
        disp_top(name, game, total, level, guessed, points, mistakes, guesses)
        if guessed == word:
            victory(word, mistakes, level)
            return mistakes
    defeat(word)
    return 0


def games(name):
    """play n number of games by choosing a level"""
    pts = 0
    l = levels()
    n = number()
    cls()
    for i in range(1, n + 1):
        word = genword(l)
        result = userin(name, i, n, l, "", pts, 7, word)
        pts += int(points(result, l))
        if i != n:
            countdown("Next game")
        else:
            countdown("Result")
    print(f"Congratulations {name}! Your final score: {pts}!")


def main():
    play()


if __name__ == "__main__":
    main()
