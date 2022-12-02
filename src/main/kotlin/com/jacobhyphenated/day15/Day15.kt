package com.jacobhyphenated.day15

import com.jacobhyphenated.Day
import com.jacobhyphenated.day9.IntCode
import java.io.File
import java.util.*
import kotlin.random.Random

// Oxygen System
class Day15: Day<List<Long>> {
    override fun getInput(): List<Long> {
        return this.javaClass.classLoader.getResource("day15/input.txt")!!
            .readText()
            .split(",")
            .map { it.toLong() }
    }

    /**
     * Move the repair robot to the oxygen system.
     * The repair robot runs an IntCode program that takes an input
     * (A direction 1-4) and returns an output (0 - wall, 1 - empty space, 2 - oxygen system)
     *
     * We have no knowledge of what is around the repair robot.
     * What is the shortest number of commands we can use to move the robot to the oxygen system?
     */
    override fun part1(input: List<Long>): Number {
        val robot = IntCode(input)

        var currentLocation = Pair(0,0)
        val grid = mutableMapOf(currentLocation to Location.EMPTY)
        var lastDirection: Direction? = null

        // Do a random walk with the robot until we find the system, mapping as we go
        while(true) {
            val moveAttempt = chooseNextMove(currentLocation, grid, lastDirection)

            robot.execute(listOf(moveAttempt.getCode()))
            val result = getLocationType(robot.output[0].toInt())
            robot.output.clear()

            val position = fromCurrentLocation(currentLocation, moveAttempt)
            grid[position] = result
            // stay in our current position if the robot hits a wall
            if (result != Location.WALL) {
                currentLocation = position
                lastDirection = moveAttempt
            }

            // Once we find the O2 System, we can stop
            if (result == Location.SYSTEM) {
                break
            }
        }
        val target = grid.entries.first { (_,v) -> v == Location.SYSTEM }.key
        return shortestPath(Pair(0,0), grid).getValue(target)
    }

    /**
     * Once the Oxygen is repaired, it will fill up, starting at the system
     * and moving one adjacent space in all directions per minute.
     * How long until the entire area fills with Oxygen?
     */
    override fun part2(input: List<Long>): Number {
        val robot = IntCode(input)

        var currentLocation = Pair(0,0)
        val grid = mutableMapOf(currentLocation to Location.EMPTY)
        var lastDirection: Direction? = null

        // In this loop, we just map the entire location
        while(true) {
            val moveAttempt = chooseNextMove(currentLocation, grid, lastDirection)
            robot.execute(listOf(moveAttempt.getCode()))
            val result = getLocationType(robot.output[0].toInt())
            robot.output.clear()

            val position = fromCurrentLocation(currentLocation, moveAttempt)
            grid[position] = result
            if (result != Location.WALL) {
                currentLocation = position
                lastDirection = moveAttempt
            }

            // Break out of the loop if there are no spaces with adjacent unexplored locations
            val unexplored = grid.entries.filter { (_,v) -> v == Location.EMPTY || v == Location.SYSTEM }
                .any { (k, _) -> Direction.values().map{ grid[fromCurrentLocation(k, it)]}.any { it == null }}
            if (!unexplored) {
                break
            }
        }

        // Re-use dijkstra's algorithm, this time starting at the System space
        val target = grid.entries.first { (_,v) -> v == Location.SYSTEM }.key
        val paths = shortestPath(target, grid)
        return paths.values.filter { it < Int.MAX_VALUE }.max()
    }

    /**
     * Implementation of Dijkstra's algorithm. The only weight/cost is the distance from the start position
     * as measured by the path the robot can take through empty adjacent (non-diagonal) spaces
     *
     * @param start the start position from which distances will be measured
     * @param grid a map of (x,y) pairs to the type of space at that location (empty, wall, etc.)
     *
     * @return A map of all positions to the cost of getting to that position.
     * Note: WALL positions may have a cost of MAX_INT
     */
    private fun shortestPath(start: Pair<Int,Int>, grid: Map<Pair<Int,Int>, Location>): Map<Pair<Int,Int>, Int> {
        val distances: MutableMap<Pair<Int,Int>, Int> = grid.keys.fold(mutableMapOf()){ map, position ->
            map[position] = Int.MAX_VALUE
            map
        }
        // Use a priority queue implementation - min queue sorted by lowest "cost"
        val queue = PriorityQueue<PathCost> { a, b -> a.cost - b.cost }
        queue.add(PathCost(start, 0))
        distances[start] = 0

        var current: PathCost
        do {
            // Traverse from the lowest cost position on our queue. This position is "solved"
            current = queue.remove()

            // If we already found a less expensive way to reach this position
            if (current.cost > (distances[current.position] ?: Int.MAX_VALUE)) {
                continue
            }

            // From the current position, look in each direction for an empty space
            val adjacent = Direction.values().map { fromCurrentLocation(current.position, it) }
                .filter { grid[it] == Location.EMPTY || grid[it] == Location.SYSTEM }
            adjacent.forEach {
                val cost = distances.getValue(current.position) + 1
                // If the cost to this space is less than what was previously known, put this on the queue
                if (cost < distances.getValue(it)) {
                    distances[it] = cost
                    queue.add(PathCost(it, cost))
                }
            }
        } while (queue.size > 0)
        return distances
    }

    /**
     * Helper method to determine where the robot should move to next.
     *
     * If there is an unexplored position adjacent to the current location, do that first.
     * Otherwise, try not to go back the direction we just came from unless there is no other option
     *
     * If there are multiple valid moves to make, pick one at random
     */
    private fun chooseNextMove(currentLocation: Pair<Int,Int>, grid:Map<Pair<Int,Int>, Location>, lastDirection: Direction?): Direction {
        val moves = Direction.values().map { it to grid[fromCurrentLocation(currentLocation, it)] }
        val (unknown, known) = moves.partition { it.second == null }
        if (unknown.isNotEmpty()) {
           return unknown[Random.nextInt(unknown.size)].first
        }
        val allowedMoves = known.filter { it.second != Location.WALL }.map { it.first }.toMutableList()
        if (lastDirection != null && allowedMoves.size > 1) {
            allowedMoves.remove(lastDirection.inverse())
        }
        return allowedMoves[Random.nextInt(allowedMoves.size)]
    }

    private fun getLocationType(locationType: Int): Location {
        return when(locationType) {
            0 -> Location.WALL
            1 -> Location.EMPTY
            2 -> Location.SYSTEM
            else -> throw NotImplementedError("Invalid location code $locationType")
        }
    }

    private fun fromCurrentLocation(location: Pair<Int,Int>, direction: Direction): Pair<Int,Int> {
        val (x,y) = location
        return when(direction) {
            Direction.NORTH -> Pair(x, y-1)
            Direction.SOUTH -> Pair(x, y+1)
            Direction.WEST -> Pair(x-1, y)
            Direction.EAST -> Pair(x+1, y)
        }
    }
}

enum class Direction {
    NORTH, SOUTH, WEST, EAST;

    fun getCode(): Long {
        return (this.ordinal + 1).toLong()
    }

    fun inverse(): Direction {
        return when(this) {
            NORTH -> SOUTH
            SOUTH -> NORTH
            EAST -> WEST
            WEST -> EAST
        }
    }
}

enum class Location {
    EMPTY, WALL, SYSTEM
}

data class PathCost(val position: Pair<Int,Int>, val cost: Int)