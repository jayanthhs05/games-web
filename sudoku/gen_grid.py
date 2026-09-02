import random


def gen_sudoku():
    grid = [[0 for _ in range(9)] for _ in range(9)]
    solve(grid)
    return grid


def solve(grid):
    empty_cell = find_empty_cell(grid)
    if not empty_cell:
        return True
    row, col = empty_cell
    numbers = list(range(1, 10))
    random.shuffle(numbers)
    for num in numbers:
        if does_fit(grid, row, col, num):
            grid[row][col] = num
            if solve(grid):
                return True
            grid[row][col] = 0
    return False


def find_empty_cell(grid):
    for i in range(9):
        for j in range(9):
            if grid[i][j] == 0:
                return (i, j)
    return None


def does_fit(grid, row, col, num):
    if num in grid[row]:
        return False
    if num in [grid[i][col] for i in range(9)]:
        return False
    for i in range(3 * (row // 3), 3 * (row // 3) + 3):
        for j in range(3 * (col // 3), 3 * (col // 3) + 3):
            if grid[i][j] == num:
                return False
    return True


def display_grid(grid):
    for row in grid:
        print(" ".join(str(num) for num in row))


def main():
    pass


if __name__ == "__main__":
    main()
