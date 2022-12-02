package com.jacobhyphenated.day25

import com.jacobhyphenated.Day
import com.jacobhyphenated.day9.IntCode
import java.io.File

// Cryostasis
class Day25: Day<List<Long>> {
    override fun getInput(): List<Long> {
        return this.javaClass.classLoader.getResource("day25/input.txt")!!
            .readText()
            .split(",")
            .map { it.toLong() }
    }

    /**
     * Play the text based game using console input to navigate the robot
     * pick up items along the way to pass through the weight based security scanner.
     *
     * Use commands provided by the program, plus two additional commands (help, quit)
     *
     * Spoiler - collect: spool of cat6, fixed point, candy cane, shell
     */
    override fun part1(input: List<Long>): Number {
        val robot = IntCode(input)
        robot.execute()

        val helpText = """
            Commands are:
                north       Move north
                south       Move south
                east        Move east
                west        Move west
                take <item> Pick up a nearby item
                drop <item> Drop an item from the inventory
                inv         List items in the inventory
                help        Show help text and list of commands
                quit        Exit the program
        """.trimIndent()
        readln()
        while(true) {
            val outputAsString = robot.output
                .map { it.toInt() }
                .map { it.toChar() }
                .joinToString("")
            println(outputAsString)
            robot.output.clear()
            print("cmd: ")
            when(val command = readln().lines()[0].lowercase()){
                "help" -> println(helpText)
                "quit" -> break
                else -> robot.execute(commandToAscii(command))
            }
        }
        return 0
    }

    /**
     * Day 25 has no part 2
     */
    override fun part2(input: List<Long>): Number {
        return 0
    }

    private fun commandToAscii(command: String): List<Long> {
        return command.toCharArray().map { it.code }
            .map { it.toLong() }
            .toMutableList().also { it.add(10L) }
    }
}