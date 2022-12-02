package com.jacobhyphenated.day12

import com.jacobhyphenated.Day
import java.io.File
import kotlin.math.absoluteValue

// The N-Body Problem
class Day12: Day<List<List<Int>>> {
    override fun getInput(): List<List<Int>> {
        return this.javaClass.classLoader.getResource("day12/input.txt")!!
            .readText()
            .lines()
            .map {line -> line
                .trim()
                .removeSuffix(">")
                .split(", ")
                .map { it.split("=")[1].toInt() }
            }
    }

    /**
     * Given a list of moons (4) and their x,y,z positions (the puzzle input)
     * Each position updates based on the velocity.
     * Velocity is computed by the differences to every other moon along each dimension.
     *
     * So moon1 with an x position of 3 and moon 2 with an x position of -1,
     * moon1 x velocity decreases by 1, and moon2 x velocity increases by 1.
     *
     * For each step, update the velocities for all dimensions by comparing all moons to each other.
     * Then update the positions by adding the velocity to the appropriate dimension.
     *
     * After 1000 steps, what is the total energy of the system?
     */
    override fun part1(input: List<List<Int>>): Number {
        val (positions, velocities) = doSteps(1000, input)
        return totalEnergy(positions, velocities)

    }

    /**
     * Eventually, the state of the universe will repeat. At what step does that happen?
     *
     * Because the next state be computed as a function of itself, the first state to repeat must be the first state.
     * (you can calculate step n-1 from step n)
     * It can take a very long time to get a repeat, brute force wont do it.
     *
     * Each dimension can be calculated independently, x is not dependent on y or z.
     * Compute the periodic repeat of x, y, and z independently.
     * Then return the least common multiple of each period
     *
     * Performance note: Finding the repeat periods sequentially does a lot of duplicate computations.
     * We could improve this by doing all three at once - stepping until all 3 periods found.
     */
    override fun part2(input: List<List<Int>>): Number {
        return findRepeatPeriods(input).reduce { a,b -> leastCommonMultiple(a, b)}
    }

    /**
     * Total energy for a moon is computed by
     * - adding the absolute values of each position value
     * - adding the absolute values of each velocity value
     * - multiplying these two numbers together.
     *
     * Do this for all moons and sum the results
     */
    fun totalEnergy(positions: List<List<Int>>, velocities: List<List<Int>>): Int {
        return positions.zip(velocities)
            .sumOf { (position, velocity) ->
                position.sumOf { it.absoluteValue } * velocity.sumOf { it.absoluteValue }
            }
    }

    // Helper method to execute n number of steps and return the position and velocity information at the end
    fun doSteps(steps: Int, input: List<List<Int>>): Pair<List<List<Int>>, List<List<Int>>> {
        val positions = input.map { mutableListOf(*it.toTypedArray()) }
        val velocities = mutableListOf<MutableList<Int>>()
        repeat(positions.size) { velocities.add(mutableListOf(0,0,0)) }
        repeat(steps) {
            doStep(positions, velocities)
        }
        return Pair(positions, velocities)
    }

    /**
     * Each dimension repeats its state eventually, and x,y,z are independent.
     * do steps until we find the minimum steps to repeat each dimensions state
     *
     * @return a list of the number of steps required to repeat for each dimension
     */
    private fun findRepeatPeriods(input: List<List<Int>>): List<Long> {
        val positions = input.map { mutableListOf(*it.toTypedArray()) }
        val velocities = mutableListOf<MutableList<Int>>()
        repeat(positions.size) { velocities.add(mutableListOf(0,0,0)) }

        val isolateDimension: (List<List<Int>>, List<List<Int>>, Int) -> List<Pair<Int,Int>> = {ps, vs, dimension ->
            ps.map { it[dimension] }.zip(vs.map { it[dimension]} )
        }

        val initialStates = (0..2).map { isolateDimension(positions, velocities, it)}
        val periods = mutableListOf<Long?>(null, null, null)

        var count: Long = 0
        do {
            doStep(positions, velocities)
            count++
            for (dimension in 0..2) {
                if (periods[dimension] == null
                        && isolateDimension(positions, velocities, dimension) == initialStates[dimension]) {
                    periods[dimension] = count
                }
            }
        } while (periods.any { it == null })
        return periods.filterNotNull()
    }

    // Helper method to compute the state after the next step
    private fun doStep(positions: List<MutableList<Int>>, velocities: List<MutableList<Int>>) {
        // This is unpleasant - the first two loops compare all moons to each other. The 3rd loop covers all 3 dimensions.
        for (position1 in 0 until positions.size - 1) {
            for (position2 in position1 until positions.size) {
                for (dimension in 0..2) {
                    val p1 = positions[position1][dimension]
                    val p2 = positions[position2][dimension]
                    if (p1 < p2) {
                        velocities[position1][dimension] += 1
                        velocities[position2][dimension] -= 1
                    } else if (p1 > p2) {
                        velocities[position1][dimension] -= 1
                        velocities[position2][dimension] += 1
                    }
                }
            }
        }

        // After velocities have been properly calculated, update the positions
        for ((i, position) in positions.withIndex()) {
            for (dimension in 0..2) {
                position[dimension] += velocities[i][dimension]
            }
        }
    }

    private fun leastCommonMultiple(m: Long, n:Long): Long {
        return m*n / gcd(m,n)
    }

    // tailrec allows the compiler to optimize for tail recursion
    private tailrec fun gcd(a: Long, b: Long): Long {
        return if (b == 0L) a.absoluteValue else gcd(b, a % b)
    }

}