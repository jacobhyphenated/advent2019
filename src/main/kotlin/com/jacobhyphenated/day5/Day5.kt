package com.jacobhyphenated.day5

import com.jacobhyphenated.Day
import java.io.File

// Sunny with a Chance of Asteroids
class Day5: Day<List<Int>> {
    override fun getInput(): List<Int> {
        return this.javaClass.classLoader.getResource("day5/input.txt")!!
            .readText()
            .split(",")
            .map { it.toInt() }
    }

    override fun part1(input: List<Int>): Int {
        val intCode = IntCode(mutableListOf(*input.toTypedArray()))
        intCode.input = 1
        intCode.execute()
        return intCode.output
    }

    override fun part2(input: List<Int>): Int {
        val intCode = IntCode(mutableListOf(*input.toTypedArray()))
        intCode.input = 5
        intCode.execute()
        return intCode.output
    }
}