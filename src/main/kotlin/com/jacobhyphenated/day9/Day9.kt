package com.jacobhyphenated.day9

import com.jacobhyphenated.Day
import java.io.File

// Sensor Boost
class Day9: Day<List<Long>> {
    override fun getInput(): List<Long> {
        return this.javaClass.classLoader.getResource("day9/input.txt")!!
            .readText()
            .split(",")
            .map { it.toLong() }
    }

    /**
     * Add some new features to the IntCode processor
     * - relative base mode
     * - opcode 9 for modifying the relative base
     * - support for large numbers (Long)
     *
     * Run the IntCode program in test mode (input 1)
     * Return the only output value
     */
    override fun part1(input: List<Long>): Long {
        val ic = IntCode(input)
        ic.execute(listOf(1))
        return ic.output[0]
    }

    /**
     * Run the program in boost mode (input 2)
     * Return the only output
     */
    override fun part2(input: List<Long>): Long {
        val ic = IntCode(input)
        ic.execute(listOf(2))
        return ic.output[0]
    }

}