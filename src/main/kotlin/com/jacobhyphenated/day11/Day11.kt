package com.jacobhyphenated.day11

import com.jacobhyphenated.Day
import com.jacobhyphenated.day9.IntCode
import java.io.File

// Space Police
class Day11: Day<List<Long>> {
    override fun getInput(): List<Long> {
        return this.javaClass.classLoader.getResource("day11/input.txt")!!
            .readText()
            .split(",")
            .map { it.toLong() }
    }

    /**
     * Use a point robot to paint the ship id
     * Return the number of spaces the robot painted during its run
     * (only count each location once, even if painted multiple times)
     */
    override fun part1(input: List<Long>): Number {
        val robot = Robot(input)
        robot.execute()
        return robot.paint.keys.size
    }

    /**
     * The starting location is white, not black.
     * Print the message painted by the robot
     */
    override fun part2(input: List<Long>): Number {
        val robot = Robot(input)
        robot.paint[Pair(0,0)] = 1
        robot.execute()

        val xVals = robot.paint.keys.map { it.first }
        val yVals = robot.paint.keys.map { it.second }

        // The robot stores a sparse map of spaces it painted. Fill this into a 2x2 grid for printing
        val grid: List<List<Long>> = (yVals.min().. yVals.max()).fold<Int, MutableList<List<Long>>>(mutableListOf()){ acc, y ->
            acc.add((xVals.min()..xVals.max()).fold(mutableListOf()){ row, x ->
                row.add(robot.paint[Pair(x, y)] ?: 0)
                row
            })
            acc
        }.reversed() // reverse the y-axis for readability

        val sb = java.lang.StringBuilder()
        for (row in grid) {
           for (col in row) {
               sb.append(if (col == 0L) { " " } else { "#" })
           }
            sb.append("\n")
        }
        println(sb.toString())
        // return value doesn't matter since we are eyeballing the printed output
        return 0
    }
}

/**
 * The paint robot takes the IntCode instructions as it's running program.
 *
 * Each execution of the IntCode takes an input (the color at its current location).
 * There are two output values. The first is the color to paint the location.
 * The second is the direction to turn (0 is left, 1 is right)
 *
 * Once the IntCode program runs, change the direction and move forward one space.
 * All locations start at black. The staring direction is UP
 */
class Robot(instructions: List<Long>) {
    // use the IntCode implementation from Day9
    private val brain = IntCode(instructions)
    private var direction = Direction.UP
    private var position = Pair(0,0)

    val paint = mutableMapOf<Pair<Int,Int>, Long>()

    fun execute() {
        while (!brain.isHalted) {
            brain.execute(listOf(paint[position] ?: 0))
            val (color, turn) = brain.output
            brain.output.clear()
            paint[position] = color
            when(turn) {
                0L -> turnLeft()
                1L -> turnRight()
                else -> throw NotImplementedError("Invalid turn direction $turn")
            }
            moveForward()
        }
    }

    private fun moveForward() {
        val (x, y) = position
        position = when(direction) {
            Direction.UP -> Pair(x, y+1)
            Direction.RIGHT -> Pair(x+1, y)
            Direction.DOWN -> Pair(x, y - 1)
            Direction.LEFT -> Pair(x - 1, y)
        }
    }

    private fun turnLeft() {
        direction = when(direction) {
            Direction.UP -> Direction.LEFT
            Direction.RIGHT -> Direction.UP
            Direction.DOWN -> Direction.RIGHT
            Direction.LEFT -> Direction.DOWN
        }
    }

    private fun turnRight() {
        direction = when(direction) {
            Direction.UP -> Direction.RIGHT
            Direction.RIGHT -> Direction.DOWN
            Direction.DOWN -> Direction.LEFT
            Direction.LEFT -> Direction.UP
        }
    }
}

private enum class Direction {
    UP,
    RIGHT,
    DOWN,
    LEFT
}