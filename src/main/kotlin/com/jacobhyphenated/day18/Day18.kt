package com.jacobhyphenated.day18

import com.jacobhyphenated.Day
import com.jacobhyphenated.day15.PathCost
import java.io.File
import java.util.*

// Many Worlds Interpretation
class Day18: Day<List<List<Char>>> {
    override fun getInput(): List<List<Char>> {
        return createGrid(
            this.javaClass.classLoader.getResource("day18/input.txt")!!.readText()
        )
    }

    /**
     * You are in a cave with keys (lower case letters) and locked doors (upper case letters).
     * When you find the key, you can pass through the corresponding doors.
     * Your robot's location is signified with an @ (. means open space, # means wall)
     *
     * What is the fewest number of moves necessary to find all the keys (runs in 1.5 seconds)
     * */
    override fun part1(input: List<List<Char>>): Number {
        // start at @ symbol
        val initialPosition = initialStartingLocation(input)
        val grid = deepCopy(input)
        val lowestCostPath = mutableSetOf<Int>()
        return recursePath(grid, setOf(initialPosition), 0, lowestCostPath, mutableMapOf())!!
    }

    /**
     * The pattern around the initial robot looks like
     *      ...
     *      .@.
     *      ...
     *
     *  But instead, there are 4 robots, and the pattern should look like:
     *      @#@
     *      ###
     *      @#@
     *
     *  Each robot can move independently (but only one can move at a time)
     *  Using all 4 robots, what is the fewest number of total moves needed to collect all keys?
     *
     *  Runs in 45 seconds. Obviously not ideal and needs further optimizations
     */
    override fun part2(input: List<List<Char>>): Number {
        val (r,c) = initialStartingLocation(input)
        val grid = deepCopy(input)

        // replace the starting initial robot pattern with the part 2 pattern
        for (row in r-1..r+1) {
            for (col in c-1..c+1) {
                grid[row][col] = '#'
            }
        }
        val robots = setOf(Pair(r-1,c-1), Pair(r-1, c+1), Pair(r+1, c-1), Pair(r+1, c+1))
        robots.forEach { (row, col) -> grid[row][col] = '@' }
        val lowestCostPath = mutableSetOf<Int>()

        // re-use the part 1 logic but with a set of 4 robot positions rather than 1 position
        return recursePath(grid, robots, 0, lowestCostPath, mutableMapOf())!!
    }

    fun createGrid(input: String): List<List<Char>> {
        return input.lines()
            .map { it.toCharArray().map { c -> c } }
    }

    /**
     * Use a Depth First recursive search to find the shortest path to collect all keys.
     *
     * @param grid state of the map
     * @param robots current position(s) of the robot(s). Part 1 has one robot while part 2 has four
     * @param currentCost the cost to get the robot to its current position
     * @param lowestCostPath The final costs found so far to solve the problem
     * @param memo Memoization map that tracks the set of keys remaining + the robot positions to the cost to solve that map state
     *
     * @return The cost of getting from this current state to a solved state (null if this branch is not valid)
     */
    private fun recursePath(grid: List<List<Char>>,
                            robots: Set<Pair<Int,Int>>,
                            currentCost: Int,
                            lowestCostPath: MutableSet<Int>,
                            memo: MutableMap<Pair<Set<Char>, Set<Pair<Int,Int>>>, Int>): Int? {

        val remainingKeys: Map<Char, Pair<Int,Int>> = grid
            .flatMapIndexed { r, row -> row.mapIndexed{ c, space -> Pair(Pair(r,c), space)}}
            .fold(mutableMapOf()){ acc, (position, space) ->
                if (space != '.' && space != '#' && space != '@' && space == space.lowercaseChar()) {
                    acc[space] = position
                }
                acc
            }
        // There are no keys left, so we can stop - the total cost to get all keys is the cost to get here
        if (remainingKeys.isEmpty()) {
            lowestCostPath.add(currentCost)
            return 0
        }

        // If we have previously solved this map state, use that memoized value
        memo[Pair(remainingKeys.keys, robots)]?.let { return it }

        val nextMove = robots.mapNotNull { robot ->
            val distances = validPathsForPosition(grid, robot)

            // If the cost of getting to a key from here is greater than an already solved cost, this branch
            // of the DFS is dead and can be pruned
            val currentMinCost = lowestCostPath.minOrNull() ?: Int.MAX_VALUE
            if (remainingKeys.any { (_,position) ->  (distances[position] ?: 0) + currentCost > currentMinCost}) {
                return null
            }

            // for each key to find, do a recursive DFS
            // remove the key and corresponding door from the grid, then re-calculate position movement costs
            remainingKeys.entries.mapNotNull keyMap@{ (key, nextPosition) ->
                // no path to this key yet
                val costToPosition = distances[nextPosition] ?:  return@keyMap null

                val (row, col) = nextPosition
                val newGrid = deepCopy(grid)
                newGrid[row][col] = '@'
                newGrid[robot.first][robot.second] = '.'

                clearDoor(newGrid, key)
                val nextRobots = robots.toMutableSet()
                nextRobots.remove(robot)
                nextRobots.add(nextPosition)
                recursePath(newGrid, nextRobots, costToPosition + currentCost, lowestCostPath, memo)
                    ?.let { it + costToPosition }
            }.minOrNull()
        }.minOrNull()
        if (nextMove != null) {
            // Memoize the solved value for the given grid state
            memo[Pair(remainingKeys.keys, robots)] = nextMove
        }
        return nextMove
    }

    /**
     * Dijkstra's algorithm implementation for the grid
     * Finds the lowest movement cost to each space from the given location
     * Does not pass through walls, doors, keys, or other robots
     */
    private fun validPathsForPosition(grid:List<List<Char>>, position: Pair<Int,Int>): Map<Pair<Int,Int>, Int> {
        val distances: MutableMap<Pair<Int,Int>, Int> = mutableMapOf()
        // Use a priority queue implementation - min queue sorted by lowest "cost"
        val queue = PriorityQueue<PathCost> { a, b -> a.cost - b.cost }
        queue.add(PathCost(position, 0))
        distances[position] = 0
        var current: PathCost
        do {
            current = queue.remove()
            // If we already found a less expensive way to reach this position
            if (current.cost > (distances[current.position] ?: Int.MAX_VALUE)) {
                continue
            }
            val (r,c) = current.position
            // if this position is a key or a door or a robot, we won't look further as the path is essentially blocked
            if (current.position != position && grid[r][c] != '.') {
                continue
            }
            // From the current position, look in each direction for a valid move
            // (the outside border is always walls, so we don't need to worry about index out of bounds issues)
            listOf(Pair(r-1,c), Pair(r+1,c), Pair(r,c-1), Pair(r,c+1))
                .filter { (row,col) -> grid[row][col] != '#' }
                .forEach {
                    val cost = distances.getValue(current.position) + 1
                    // If the cost to this space is less than what was previously known, put this on the queue
                    if (cost < (distances[it] ?: Int.MAX_VALUE)) {
                        distances[it] = cost
                        queue.add(PathCost(it, cost))
                    }
                }
        } while (queue.size > 0)
        return distances
    }

    private fun clearDoor(grid: MutableList<MutableList<Char>>, key: Char) {
        for (r in grid.indices) {
            for (c in grid[r].indices) {
                if (grid[r][c] == key.uppercaseChar()) {
                    grid[r][c] = '.'
                    return
                }
            }
        }
    }

    private fun initialStartingLocation(grid: List<List<Char>>): Pair<Int,Int> {
        for (r in grid.indices) {
            for (c in grid[r].indices) {
                if (grid[r][c] == '@') {
                    return Pair(r,c)
                }
            }
        }
        throw IllegalStateException("No valid starting character")
    }
    private fun deepCopy(grid: List<List<Char>>): MutableList<MutableList<Char>> {
        val newGrid = mutableListOf<MutableList<Char>>()
        for (row in grid) {
            newGrid.add(row.toMutableList())
        }
        return newGrid
    }

}