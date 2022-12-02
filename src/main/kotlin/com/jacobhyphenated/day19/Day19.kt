package com.jacobhyphenated.day19

import com.jacobhyphenated.Day
import com.jacobhyphenated.day9.IntCode
import java.io.File

// Tractor Beam
class Day19: Day<List<Long>> {
    override fun getInput(): List<Long> {
        return this.javaClass.classLoader.getResource("day19/input.txt")!!
            .readText()
            .split(",")
            .map { it.toLong() }
    }

    /**
     * The IntCode program that is the input takes an x,y coordinate and determines if
     * that position is inside the tractor beam (1) or not (0). Both x and y coordinates must be positive.
     *
     * Search a grid from 0 to 50 for x and y and find how many positions are in the range of the tractor beam.
     */
    override fun part1(input: List<Long>): Number {
        var countOn = 0
        for (x in 0 until 50) {
            for (y in 0 until 50) {
                val tractorBeam = IntCode(input)
                tractorBeam.execute(listOf(x.toLong(),y.toLong()))
                if (tractorBeam.output[0] == 1L) {
                    countOn++
                }
            }
        }
        return countOn
    }

    /**
     * Find the first coordinate such that a 100x100 square fits entirely within the tractor beam
     * and its top left point is that coordinate. Return x*10,000 + y
     *
     * Binary search doesn't work for this since valid solutions may skip lines.
     *
     * We search every line y, but we can restrict what x values we search through
     *  * use the first x on this y line where (x,y+99) is valid as the starting point on the next line
     *    (It's not possible for a lower value of x to be valid on subsequent lines)
     *  * If there is no x value that satisfies the above condition on this line (common for most lines),
     *    then when we reach an x such that (x+99,y) is no longer valid, use that x as the starting point on the next line.
     *  * Search the top right (x+99,y) and bottom left (x,y+99) corners. If both corners are valid, the square is valid
     *
     */
    override fun part2(input: List<Long>): Number {
        var y = 0L
        var minX = 0L
        while (true ){
            var foundX = false
            // the starting x is always at minimum the same as the previous y's starting x
            var x = minX
            while (true) {
                val topRight = IntCode(input)
                topRight.execute(listOf(x+99, y))
                // If the top right coordinate is not in range, go to the next line
                if(topRight.output[0] == 0L) {
                    if (!foundX) {
                        // If we haven't found a valid bottom left yet on this line
                        // use this x position as the starting place to search the following line
                        minX = x
                    }
                    break
                }

                val bottomLeft = IntCode(input)
                bottomLeft.execute(listOf(x, y+99))
                // If the bottom left corner is valid from this x position, remember that position
                // use that as the starting point for the x coordinates to search through on the next line
                if (bottomLeft.output[0] == 1L && !foundX) {
                    foundX = true
                    minX = x
                }

                if (bottomLeft.output[0] == 1L && topRight.output[0] == 1L) {
                    // this is the first valid square - return the result
                    return x * 10000 + y
                }
                x++
            }
            y++
        }
    }

 }