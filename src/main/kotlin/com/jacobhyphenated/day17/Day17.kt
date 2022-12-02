package com.jacobhyphenated.day17

import com.jacobhyphenated.Day
import com.jacobhyphenated.day9.IntCode
import java.io.File

// Set And Forget
class Day17: Day<List<Long>> {
    override fun getInput(): List<Long> {
        return this.javaClass.classLoader.getResource("day17/input.txt")!!
            .readText()
            .split(",")
            .map { it.toLong() }
    }

    /**
     * The IntCode input is a program that looks at cameras and controls the robot.
     * The robot communicates through ASCII characters. The output of running the program
     * shows where the scaffolding spaces that the robot can move on are located.
     *
     * The output is in ASCII where 35 == # == scaffold and 46 == . == empty space.
     * New lines use the new line character of 10.
     * The robot is always facing a certain direction and is represented by <, >, ^, or v
     *
     * The scaffolding generally is a straight path, but there are points where the paths intersect.
     * For each intersection, take the row and column index locations and multiply them. Add the results.
     */
    override fun part1(input: List<Long>): Number {
        val asciiBot = IntCode(input)
        asciiBot.execute()
        val (rows, direction) = parseAsciiOutput(asciiBot.output)
        printGrid(rows, direction)

        // An intersection cannot occur on the edge, no need to iterate through edge spaces
        val intersections = mutableSetOf<Pair<Int,Int>>()
        for (r in 1 until rows.size -1) {
            for (c in 1 until rows[r].size - 1) {
                if (rows[r][c] == Space.EMPTY) {
                    continue
                }
                // If this space is a scaffold and all adjacent spaces are scaffolds, this space is an intersection
                if (listOf(rows[r-1][c], rows[r+1][c], rows[r][c-1], rows[r][c+1])
                        .all { it == Space.SCAFFOLD || it == Space.ROBOT }){
                    intersections.add(Pair(r,c))
                }
            }
        }
        return intersections.sumOf { (r,c) -> r * c }
    }

    /**
     * Give the robot a command sequence to visit all spaces on the scaffolding at least once.
     *
     * The robot only knows 3 movement commands (a,b,c). The main program tells the robot what order
     * and how often to execute a,b, and c.
     *
     * A movement command is either R (turn right), L (turn left) or some number of spaces to move forward.
     *
     * Example: L,6,L,2 translates in ascii to 76, 44, 54, 44, 76, 44, 50, 10
     * (use 44 for commas and 10 to represent a new line.
     *
     * First, put the robot in move mode by setting the IntCode value at 0 to 2
     *
     * Then give the program that is:
     * main
     * a
     * b
     * c
     *
     * Then add a final line that is debug mode (n or y)
     * Terminate all lines with a new line character (10).
     *
     * The last value of the output is the amount of dust collected. Return that.
     */
    override fun part2(input: List<Long>): Number {
        val wakeUp = input.toMutableList()
        wakeUp[0] = 2
        val asciiBot = IntCode(wakeUp)

        // Solved here with Pen and Paper, then checked by running the code.
        // My approach was to look at the first steps and the last steps, which must
        // always hve the same pattern of instructions. Then look at some loops and what steps
        // would traverse the loops. Once I identified several repeating patterns, I fit
        // those patterns to the data and double-checked that it worked.
        val mainRunner = "C,B,C,B,A,B,C,A,B,A".toCharArray().map { it.code }
        val a = "R,10,L,8,L,4,R,10".toCharArray().map { it.code }
        val b = "L,6,L,4,L,12".toCharArray().map { it.code }
        val c = "L,12,L,8,R,10,R,10".toCharArray().map { it.code }
        val enableVideo = "n".toCharArray().map { it.code }

        val program: List<Int> = mutableListOf<Int>().apply {
            addAll(mainRunner)
            add(10)
            addAll(a)
            add(10)
            addAll(b)
            add(10)
            addAll(c)
            add(10)
            addAll(enableVideo)
            add(10)
        }
        asciiBot.execute(program.map { it.toLong() })
        return asciiBot.output.last()
    }

    private fun printGrid(grid: List<List<Space>>, direction: Direction) {
        val sb = java.lang.StringBuilder()
        for (row in grid) {
            for (col in row) {
                if (col == Space.ROBOT) {
                    sb.append(direction.character)
                } else {
                    sb.append(col.character)
                }
            }
            sb.append("\n")
        }
        println(sb.toString())
    }

    private fun parseAsciiOutput(output: List<Long>): Pair<List<List<Space>>, Direction> {
        val rows = mutableListOf<MutableList<Space>>()
        var cols = mutableListOf<Space>()
        var robotDirection = Direction.DOWN
        for (element in output) {
            val code = element.toInt()
            val space = spaceFromCode(code)
            if (space == Space.NEW_LINE) {
                if (cols.size > 0) {
                    rows.add(cols)
                }
                cols = mutableListOf()
                continue
            }
            cols.add(space)
            if (space == Space.ROBOT) {
                robotDirection = when(code) {
                    60 -> Direction.LEFT
                    62 -> Direction.RIGHT
                    94 -> Direction.UP
                    118 -> Direction.DOWN
                    else -> throw NotImplementedError("Invalid space code $code")
                }
            }
        }
        return Pair(rows, robotDirection)
    }

    private fun spaceFromCode(code: Int): Space {
        return when(code) {
            10 -> Space.NEW_LINE
            35 -> Space.SCAFFOLD
            46 -> Space.EMPTY
            60,62,94,118 -> Space.ROBOT
            else -> throw NotImplementedError("Invalid space code $code")
        }
    }
}

private enum class Space(val character: String) {
    SCAFFOLD("#"),
    EMPTY("."),
    ROBOT("R"),
    NEW_LINE(""),
}

private enum class Direction(val character: String) {
    UP("^"),
    DOWN("v"),
    LEFT("<"),
    RIGHT(">")
}