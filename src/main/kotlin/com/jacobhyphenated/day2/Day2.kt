package com.jacobhyphenated.day2

import com.jacobhyphenated.Day
import java.io.File

// 1202 Program Alarm
class Day2: Day<List<Int>> {
    override fun getInput(): List<Int> {
        return this.javaClass.classLoader.getResource("day2/input.txt")!!
            .readText()
            .split(",")
            .map { it.toInt() }
    }

    /**
     * Using at IntCode program as the input,
     * Replace the value in position 1 with 12 and in position 2 with 2
     */
    override fun part1(input: List<Int>): Int {
        val alarmState = mutableListOf(*input.toTypedArray())
        alarmState[1] = 12
        alarmState[2] = 2
        return runIntCode(alarmState)
    }

    /**
     * Determine what pairs of inputs in positions 1 and 2 produce the output 19690720
     * Return 100 * position1 + position2
     */
    override fun part2(input: List<Int>): Int {
        for (noun in 0..99) {
            for (verb in 0..99) {
                val alarmState = mutableListOf(*input.toTypedArray())
                alarmState[1] = noun
                alarmState[2] = verb
                if (runIntCode(alarmState) == 19690720) {
                    return 100 * noun + verb
                }
            }
        }
        return 0
    }

    /**
     * The IntCode program runs in steps of 4 positions
     * 0 is the Opcode
     *      1 is addition
     *      2 is multiplication
     *      99 ends the program
     *  1 is the memory address of the first instruction
     *  2 is the memory address of the second instruction
     *  3 is the memory address to store the operation result
     *
     *  Run until a 99 termination occurs
     *  Return the value at address 0
     */
    fun runIntCode(input: MutableList<Int>): Int {
        for (i in 0 until input.size step 4) {
            when (input[i]) {
                1 -> input[input[i+3]] = input[input[i+1]] + input[input[i+2]]
                2 -> input[input[i+3]] = input[input[i+1]] * input[input[i+2]]
                99 -> break
                else -> throw IllegalArgumentException("Invalid control digit ${input[i]}")
            }
        }
        return input[0]
    }

}