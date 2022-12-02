package com.jacobhyphenated.day13

import com.jacobhyphenated.Day
import com.jacobhyphenated.day9.IntCode
import java.io.File

// Care Package
class Day13: Day<List<Long>> {
    override fun getInput(): List<Long> {
        return this.javaClass.classLoader.getResource("day13/input.txt")!!
            .readText()
            .split(",")
            .map { it.toLong() }
    }

    /**
     * The arcade program runs on an IntCode software.
     *
     * The output values are read in groups of 3.
     * * Value 1 is the x position
     * * Value 2 is the y position
     * * Value 3 is the type of space to display on the screen
     *
     * Run the program. How many Wall spaces are on the screen?
     */
    override fun part1(input: List<Long>): Number {
        // Re-use the IntCode from Day9
        val software = IntCode(input)
        software.execute()
        val screen = mutableMapOf<Pair<Long,Long>, Space>()
        for (i in 0 until software.output.size step 3) {
            val x = software.output[i]
            val y = software.output[i+1]
            val space = getSpace(software.output[i+2])
            screen[Pair(x,y)] = space
        }
        return screen.values.count { it == Space.BLOCK }
    }

    /**
     * The arcade program is a game of breakout. The objective is to destroy all blocks.
     * There is one ball and one paddle. The paddle moves left or right along the bottom row.
     * Input -1 is left, 1 is right, 0 keeps the same position.
     *
     * There is also a score that is displayed at (-1,0).
     *
     * What is the score when all blocks are destroyed?
     */
    override fun part2(input: List<Long>): Number {
        val instructions = mutableListOf(*input.toTypedArray())
        // initialize memory 0 as 2 for free play mode
        instructions[0] = 2
        val software = IntCode(instructions)
        var score = 0L
        var joystick = 0L
        val screen = mutableMapOf<Pair<Long,Long>, Space>()

        // Main game loop. Continue until all blocks are gone
        do {
            // The first output is the initial game state.
            // Subsequent outputs or only diffs showing when something changes
            software.output.clear()
            software.execute(listOf(joystick))
            for (i in 0 until software.output.size step 3) {
                val x = software.output[i]
                val y = software.output[i+1]
                if (x == -1L && y == 0L) {
                    score =  software.output[i+2]
                } else {
                    val space = getSpace(software.output[i+2])
                    screen[Pair(x,y)] = space
                }
            }
            val (ball, _) = screen.entries.first { (_, space) -> space == Space.BALL }
            val (paddle, _) = screen.entries.first { (_, space) -> space == Space.PADDLE }

            // Move joystick so the paddle always tries to be under the ball (same x coordinate)
            joystick = when {
                ball.first < paddle.first -> -1L
                ball.first > paddle.first -> 1L
                else -> 0L
            }
        } while (screen.values.count { it == Space.BLOCK } > 0)
        return score
    }

    private fun getSpace(code: Long): Space {
       return  when(code) {
            0L -> Space.EMPTY
            1L -> Space.WALL
            2L -> Space.BLOCK
            3L -> Space.PADDLE
            4L -> Space.BALL
            else -> throw NotImplementedError("Space not defined $code")
        }
    }
}

enum class Space {
    EMPTY,
    WALL,
    BLOCK,
    PADDLE,
    BALL
}