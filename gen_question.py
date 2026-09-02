import random
from copy import deepcopy
from gen_grid import gen_sudoku, does_fit, display_grid, find_empty_cell


def has_unique(grid):
    solution = {"count": 0}

    def solve_and_count(grid):
        empty_cell = find_empty_cell(grid)
        if not empty_cell:
            solution["count"] += 1
            return
        row, col = empty_cell
        for num in range(1, 10):
            if does_fit(grid, row, col, num):
                grid[row][col] = num
                solve_and_count(grid)
                grid[row][col] = 0

    solve_and_count(grid)
    return solution["count"] == 1


def remove_numbers(grid, n):
    removed_cells = set()
    while len(removed_cells) < n:
        row = random.randint(0, 8)
        col = random.randint(0, 8)
        if (row, col) not in removed_cells:
            removed_cells.add((row, col))
            removed_num = grid[row][col]
            grid[row][col] = 0
            if not has_unique(deepcopy(grid)):
                grid[row][col] = removed_num
                removed_cells.remove((row, col))
    return grid


def partially_filled_grid(fully_solved_grid, n):
    partial_grid = deepcopy(fully_solved_grid)
    return remove_numbers(partial_grid, n)


def gen_question(n):
    full = gen_sudoku()
    part = partially_filled_grid(full, n)
    return (full, part)


def main():
    pass


if __name__ == "__main__":
    main()
