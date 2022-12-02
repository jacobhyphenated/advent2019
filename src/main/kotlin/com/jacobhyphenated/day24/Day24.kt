package com.jacobhyphenated.day24

import com.jacobhyphenated.Day
import java.io.File
import kotlin.math.pow

// Planet of Discord
class Day24: Day<List<List<Boolean>>> {
    override fun getInput(): List<List<Boolean>> {
        return parseInputGrid(
            this.javaClass.classLoader.getResource("day24/input.txt")!!
                .readText()
        )
    }

    /**
     * Bugs appear in a 5x5 grid.
     * * Bugs die unless there is exactly 1 bug adjacent (no diagonals)
     * * Empty spaces become bugs if there are 1 or 2 adjacent bugs
     * * All updates occur simultaneously each "minute"
     *
     * Find the first time a 5x5 grid layout matches any previous grid layout.
     *
     * The biodiversity rating is based on numbering the grid from left to right, top to bottom
     * (spaces in the 5x5 grid are 0 - 24).
     * Each space increases the biodiversity score by a power of 2 (1,2,4,8,16...)
     *
     * Sum the biodiversity score of each space containing a bug
     */
    override fun part1(input: List<List<Boolean>>): Number {
        val previousStates = mutableSetOf<List<List<Boolean>>>()
        var currentState = input
        while (true) {
           val nextState = mutableListOf<List<Boolean>>()
           for (row in currentState.indices) {
               val nextRow = mutableListOf<Boolean>()
               for (col in currentState[row].indices) {
                   val adjacentCount = findAdjacent(row, col, currentState).count { it }
                   if (currentState[row][col]) {
                       nextRow.add(adjacentCount == 1)
                   } else {
                       nextRow.add(adjacentCount == 1 || adjacentCount == 2)
                   }
               }
               nextState.add(nextRow)
           }
           currentState = nextState
           if (!previousStates.add(nextState)) {
               break
           }
        }
        // calculate biodiversity score
        return currentState.mapIndexed { r, row ->
            row.mapIndexed { c, bug ->
                if (bug) { 2.0.pow(c + r * 5).toInt() } else { 0 }
            }.sum()
        }.sum()
    }

    /**
     * The bugs exist in an infinitely recursive grid.
     * In each grid, the middle space (row = 2, col = 2) is a new grid.
     * The depth of the recursion goes forever, but only the starting grid (depth 0) has bugs
     *
     * Spaces along the outside of the grid are adjacent to the outer grid interior spaces
     *  ex - 0,3 is a adjacent to (0,2), (0,4) (1,3) and outer grid (1,2)
     *
     *  Spaces adjacent to the interior grid are also adjacent ot each interior grid space that borders is
     *  ex - 2,1 is adjacent to (1,1), (2,0), (3,1) and outer grid: (0,0),(1,0),(2,0),(3,0),(4,0)
     *
     *  Find how many bugs exist after 200 minutes
     */
    override fun part2(input: List<List<Boolean>>): Number {
        return bugsAfterMinutes(input, 200)
    }

    fun bugsAfterMinutes(input: List<List<Boolean>>, minutes: Int): Int {
        var depthMap = mutableMapOf<Int, List<List<Boolean>>>()
        depthMap[0] = input
        repeat(minutes) {
            val nextDepthMap = mutableMapOf<Int, List<List<Boolean>>>()
            for (depth in depthMap.keys.min() - 1 .. depthMap.keys.max() + 1) {
                // map this grid at depth to the next grid at the same depth
                val nextGrid = getGridAtDepth(depthMap, depth)
                    .mapIndexed { r, row ->
                        row.mapIndexed { c, isBug ->
                            val adjacentCount = findAdjacentDepth(r,c, depth, depthMap).count { it }
                            // this is not a real space, since it represents an interior grid
                            if (r == 2 && c == 2) {
                                false
                            }
                            else if (isBug) {
                                adjacentCount == 1
                            } else {
                                adjacentCount == 1 || adjacentCount == 2
                            }
                        }
                    }
                nextDepthMap[depth] = nextGrid
            }
            depthMap = nextDepthMap
        }
        return depthMap.values.sumOf { grid ->
            grid.sumOf { row -> row.count { it } }
        }
    }

    /**
     * Find adjacent spaces in a flat 5x5 grid
     */
    private fun findAdjacent(row: Int, col: Int, grid: List<List<Boolean>>): List<Boolean> {
        val result = mutableListOf<Boolean>()
        for (r in (row - 1).coerceAtLeast(0) .. (row + 1).coerceAtMost(grid.size - 1)) {
            if (r == row) {
                continue
            }
            result.add(grid[r][col])
        }
        for (c in (col - 1).coerceAtLeast(0) .. (col + 1).coerceAtMost(grid[row].size - 1)) {
            if (c == col) {
                continue
            }
            result.add(grid[row][c])
        }
        return result
    }

    /**
     * Find adjacent spaces in the recursive grid
     */
    private fun findAdjacentDepth(row: Int, col: Int, depth: Int, depthMap: Map<Int, List<List<Boolean>>>): List<Boolean> {
        val result = mutableListOf<Boolean>()
        for (r in (row-1)..(row+1)) {
            if (r == row) {
                continue
            }
            if (r < 0) {
                result.add(getGridAtDepth(depthMap, depth + 1)[1][2])
            }
            else if (r == 2 && col == 2) {
                val innerGrid = getGridAtDepth(depthMap, depth - 1)
                if (row == 1){
                    result.addAll(innerGrid[0])
                } else {
                    result.addAll(innerGrid[4])
                }
            }
            else if (r > 4) {
                result.add(getGridAtDepth(depthMap, depth + 1)[3][2])
            }
            else {
                result.add(getGridAtDepth(depthMap, depth)[r][col])
            }
        }
        for (c in (col-1)..(col+1)) {
            if (c == col) {
                continue
            }
            if (c < 0) {
                result.add(getGridAtDepth(depthMap, depth+1)[2][1])
            }
            else if (row == 2 && c == 2) {
                val innerGrid = getGridAtDepth(depthMap, depth - 1)
                if (col == 1) {
                    result.addAll((0..4).map { innerGrid[it][0] })
                } else {
                    result.addAll((0..4).map { innerGrid[it][4] })
                }
            }
            else if (c > 4) {
                result.add(getGridAtDepth(depthMap, depth + 1)[2][3])
            }
            else {
                result.add(getGridAtDepth(depthMap, depth)[row][c])
            }
        }
        return result

    }

    private fun getGridAtDepth(depthMap: Map<Int, List<List<Boolean>>>, depth: Int): List<List<Boolean>> {
        return depthMap[depth] ?: List(5) { List(5) { false} }
    }

    fun parseInputGrid(input: String): List<List<Boolean>> {
        return input.lines()
            .map { it.toCharArray().map { c -> c == '#' } }
    }

}