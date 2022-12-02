package com.jacobhyphenated.day10

import com.jacobhyphenated.Day
import java.io.File
import java.math.BigDecimal
import kotlin.math.absoluteValue

// Monitoring Station
class Day10: Day<List<Asteroid>> {
    override fun getInput(): List<Asteroid> {
        return mapAsteroids(
            this.javaClass.classLoader.getResource("day10/input.txt")!!
                .readText()
                .lines()
        )
    }

    /**
     * Find the asteroid with the line of sight to the most other asteroids.
     *
     * Do this by grouping based on the slope of the line between each asteroid.
     * the best location is the asteroid that can see the most other asteroids.
     * Return the number of asteroids that location can see.
     */
    override fun part1(input: List<Asteroid>): Number {
        return determineBestPosition(input).second.size
    }

    /**
     * From the best location for the asteroid base, shoot down the other asteroids.
     * The firing mechanism starts pointed straight up, then rotates clockwise.
     * In each position it will shoot down the first asteroid in line of sight, then rotate.
     * If there are still asteroids around, it will rotate again, with the same firing sequence.
     *
     * For the 200th asteroid shot down, return the x coordinate multiplied by 100 plus the y coordinate
     */
    override fun part2(input: List<Asteroid>): Number {
        val (bestPosition, others) = determineBestPosition(input)
        val sortedOrder = others.keys.sorted()
        var removeCount = 0
        while (others.values.any { it.size > 0 }) {
            // each firing direction is the slope of the line of sight. Sorted in order
            for (slope in sortedOrder) {
                val otherAsteroids = others[slope] ?: mutableListOf()
                if (otherAsteroids.isNotEmpty()) {
                    val closest = otherAsteroids.minBy { it.distance(bestPosition) }
                    otherAsteroids.remove(closest)
                    removeCount++
                    if (removeCount == 200) {
                        return closest.x * 100 + closest.y
                    }
                }
            }
        }
        return 0
    }

    fun mapAsteroids(input: List<String>): List<Asteroid> {
        return input.foldIndexed(mutableListOf()) { y, acc, line ->
            line.toCharArray().forEachIndexed { x, value ->
                if (value == '#') {
                    acc.add(Asteroid(x,y))
                }
            }
            acc
        }
    }

    /**
     * Determine the best location for the asteroid monitoring station. Used for parts 1 and 2
     *
     * Returns a tuple
     * - the first value is the location of the best asteroid.
     * - the second value is a map with the slope of the line of sight from the asteroid's location
     * to a list of all asteroids along that line of sight.
     */
    private fun determineBestPosition(input: List<Asteroid>): Pair<Asteroid, Map<Slope, MutableList<Asteroid>>> {
        var bestLocation = Pair(Asteroid(0,0), mapOf<Slope, MutableList<Asteroid>>())
        for (asteroid in input) {
            val slopes = mutableMapOf<Slope, MutableList<Asteroid>>()
            for (target in input) {
                if (target == asteroid) {
                    continue
                }
                val (x1, y1) = asteroid
                val (x2, y2) = target
                val dx = (x2 - x1).toBigDecimal()
                val slope = if (dx == BigDecimal.ZERO) {
                    // protect against division by 0
                    BigDecimal.valueOf(Long.MAX_VALUE)
                }
                else {
                    (y2 - y1).toBigDecimal().setScale(3) / dx
                }
                val up = if (y2 != y1) { y2 < y1 } else { x2 < x1 }
                slopes.getOrPut(Slope(slope, up)) { mutableListOf() }.add(target)
            }
            if (slopes.size > bestLocation.second.size) {
                bestLocation = Pair(asteroid, slopes)
            }
        }
        return bestLocation
    }
}

// Custom asteroid type with a helper function to calculate the manhattan distance between 2 asteroids
data class Asteroid(val x: Int, val y: Int ){
    fun distance(other: Asteroid): Int {
        return (other.x - this.x).absoluteValue + (other.y - this.y).absoluteValue
    }
}

// Custom slope class that adds natural ordering
private data class Slope(val slope: BigDecimal, val up: Boolean): Comparable<Slope> {

    // slopes are sorted according to how the asteroid station fires at incoming asteroids
    // starting at the top and going clockwise
    override fun compareTo(other: Slope): Int {
        val unitCircle = this.getUnitCircle()
        val quadrantCompare = unitCircle.compareTo(other.getUnitCircle())
        if (quadrantCompare != 0) {
            return quadrantCompare
        }
        return slope.compareTo(other.slope)
    }

    private fun getUnitCircle(): UnitCircle {
        val infinity = BigDecimal(Long.MAX_VALUE)
        return when {
            slope == infinity && up -> UnitCircle.UP
            slope < BigDecimal.ZERO && up -> UnitCircle.QUADRANT1
            slope.compareTo(BigDecimal.ZERO) == 0 && !up -> UnitCircle.RIGHT
            slope > BigDecimal.ZERO && !up -> UnitCircle.QUADRANT4
            slope  == infinity && !up -> UnitCircle.DOWN
            slope < BigDecimal.ZERO && !up -> UnitCircle.QUADRANT3
            slope.compareTo(BigDecimal.ZERO) == 0 && up -> UnitCircle.LEFT
            slope > BigDecimal.ZERO && up -> UnitCircle.QUADRANT2
            else -> throw NotImplementedError("slope: $slope, up: $up")
        }
    }

}

private enum class UnitCircle {
    UP,
    QUADRANT1,
    RIGHT,
    QUADRANT4,
    DOWN,
    QUADRANT3,
    LEFT,
    QUADRANT2

}