package com.jacobhyphenated.day1

import com.jacobhyphenated.Day
import java.io.File

// The Tyranny of the Rocket Equation
class Day1: Day<List<Int>> {

    override fun getInput(): List<Int> {
        return this.javaClass.classLoader.getResource("day1/input.txt")!!
            .readText()
            .lines()
            .map { it.toInt() }
    }

    /**
     * Each rocket module has a mass with a fuel cost of mass / 3, rounded down - 2 (min of 0)
     * ex 14 mass  requires 2 fuel (14 / 3 - 2)
     * Return the sum of all fuel requirements for each module
     */
    override fun part1(input: List<Int>): Int {
        return input
            .sumOf { maxOf(0,it / 3 - 2) }
    }

    /**
     * We also have to include the fuel cost of the fuel. Use the same equation
     * until the fuel cost falls to 0 for each additional fuel added.
     * ex) mass of 1969 = 654 + 216 + 70 + 21 + 5 = 966 fuel
     */
    override fun part2(input: List<Int>): Int {
        return input
            .sumOf { moduleFuelCost(it) }
    }

    private fun moduleFuelCost(mass: Int): Int {
        var fuel = part1(listOf(mass))
        var fuelCost = fuel
        while (fuelCost > 0) {
            fuelCost = part1(listOf(fuelCost))
            fuel += fuelCost
        }
        return fuel
    }

}