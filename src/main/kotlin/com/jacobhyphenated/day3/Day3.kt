package com.jacobhyphenated.day3

import com.jacobhyphenated.Day
import java.io.File
import kotlin.math.absoluteValue

// Crossed Wires
class Day3: Day<Pair<List<String>, List<String>>> {
    override fun getInput(): Pair<List<String>, List<String>> {
        val lines = this.javaClass.classLoader.getResource("day3/input.txt")!!
            .readText()
            .lines()
            .map { it.split(",") }
        return Pair(lines[0], lines[1])
    }

    /**
     * Given instructions for the path of 2 wires, assuming the wires start at the same point,
     * find the point where the wires intersect that is the closest manhattan distance to the start point
     */
    override fun part1(input: Pair<List<String>, List<String>>): Int {
        val cord1 = followCord(input.first)
        val cord2 = followCord(input.second)
        return cord1.keys.intersect(cord2.keys)
            .minOf { it.first.absoluteValue + it.second.absoluteValue }
    }

    /**
     * Find the point where the wires intersect that is the closest along the wires from the start,
     * calculated by taking the steps to reach the intersection for each wire added together
     */
    override fun part2(input: Pair<List<String>, List<String>>): Int {
        val cord1 = followCord(input.first)
        val cord2 = followCord(input.second)
        return cord1.keys.intersect(cord2.keys)
            .minOf { cord1[it]!! + cord2[it]!! }
    }

    /**
     * Maps out the path of the cord in a way that can be used in both part 1 and 2
     *
     * The result is a Map of points in a coordinate plane with 0,0 as the start point
     * The value of each mapped point is the number of steps needed to reach that point along the cord.
     * If a point is reached multiple times, the lower step count is used.
     *
     * Instructions have the form of U7 - go up 7 steps
     */
    private fun followCord(cord: List<String>): Map<Pair<Int, Int>, Int> {
        var x = 0
        var y = 0
        var steps = 0;
        val coordinates = mutableMapOf<Pair<Int, Int>, Int>()
        for (path in cord) {
            val direction = path[0]
            val len = path.substring(1).toInt()
            repeat(len) {
                steps += 1
                when(direction) {
                    'R' -> x += 1
                    'L' -> x -= 1
                    'U' -> y += 1
                    'D' -> y -= 1
                    else -> throw IllegalArgumentException("Invlid direction character $direction")
                }
                coordinates.putIfAbsent(Pair(x, y), steps)
            }
        }
        return coordinates
    }
}