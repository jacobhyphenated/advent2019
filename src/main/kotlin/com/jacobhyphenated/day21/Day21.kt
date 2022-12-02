package com.jacobhyphenated.day21

import com.jacobhyphenated.Day
import com.jacobhyphenated.day9.IntCode
import java.io.File

//Springdroid Adventure
class Day21: Day<List<Long>> {
    override fun getInput(): List<Long> {
        return this.javaClass.classLoader.getResource("day21/input.txt")!!
            .readText()
            .split(",")
            .map { it.toLong() }
    }

    /**
     * The IntCode (input) program takes an ascii encoded "springscript" program.
     * There are 3 commands (AND, OR, NOT)
     * There are 4 input registers (A,B,C,D)
     *      These represent true if a space exists 1 (A), 2 (B)... 4(D) spaces from the current location
     *      False if there is a hole in that location
     * There are 2 output registers T (temp) and J
     * At the end of the program, if J is true, the robot jumps 4 spaces.
     * End the program with WALK
     *
     * Ex)
     *      NOT D J
     *      WALK
     * Stores the opposite value of D in register J
     * -- Jumps if and only if there is a hole 4 spaces away (always fails)
     *
     * The logic for the program is to jump if D is true (not a hole) but there is a hole in A, B, or C.
     * (and don't jump if A,B and C are open):
     *      D ^ !(A ^ B ^ C)
     */
    override fun part1(input: List<Long>): Number {
        val program: List<Long> = """
            OR A J
            OR B T
            AND J T
            NOT T J
            NOT C T
            OR T J
            AND D J
            WALK
            
        """.trimIndent()
            .toCharArray().map { it.code }
            .map { it.toLong() }

        val robot = IntCode(input)
        robot.execute(program)
        return robot.output[robot.output.size - 1]
    }

    /**
     * Use the command RUN instead of WALK and get 5 new registers E - I, for a total of 9 spaces to look ahead.
     *
     * In this scenario, our original program fails in the following case:
     * R
     * ###.#.##.#
     *  ABCDEFGHI
     *
     * The program will jump, landing on D, but then be trapped because both E and H are holes.
     *
     * We can add a new condition to our previous program to jump if either E or H are not holes.
     *      D ^ !(A ^ B ^ C) ^ (E v H)
     */
    override fun part2(input: List<Long>): Number {
        val program: List<Long> = """
            OR A J
            OR B T
            AND J T
            NOT T J
            NOT C T
            OR T J
            AND D J
            NOT E T
            NOT T T
            OR H T
            AND T J
            RUN
            
        """.trimIndent()
            .toCharArray().map { it.code }
            .map { it.toLong() }

        val robot = IntCode(input)
        robot.execute(program)
        return robot.output[robot.output.size - 1]
    }
}