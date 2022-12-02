package com.jacobhyphenated.day6

import com.jacobhyphenated.Day
import java.io.File

// Universal Orbit Map
class Day6: Day<Map<String, String>> {
    override fun getInput(): Map<String, String> {
        return createChildToParentMap(
            this.javaClass.classLoader.getResource("day6/input.txt")!!
                .readText()
                .lines()
        )
    }

    /**
     * Count all indirect orbits recursively with memoization to prevent repeated sub problems
     * Note: with recursion is 2ms, without recursion is 17ms (definitely not necessary here)
     */
    override fun part1(input: Map<String, String>): Int {
        val memo = mutableMapOf<String, Int>()
        var orbits = 0
        for (body in input.keys) {
            orbits += countIndirectOrbits(body, input, memo)
        }
        return orbits
    }

    /**
     * Determine the number of orbital transfers from your position (YOU) to santa (SAN)
     *
     * list out the objects orbited until we find the first common parent
     * The total transfers is sum of the indexes to the common parent
     */
    override fun part2(input: Map<String, String>): Int {
        val youOrbitPath = getOrbitPath("YOU", input)
        val santaOrbitPath = getOrbitPath("SAN", input)

        var orbitIntersect = 0
        while (youOrbitPath[orbitIntersect] !in santaOrbitPath) {
            orbitIntersect++
        }
        return orbitIntersect + santaOrbitPath.indexOf(youOrbitPath[orbitIntersect])
    }

    private fun countIndirectOrbits(body: String, orbitMap: Map<String,String>, memo: MutableMap<String, Int>): Int {
        memo[body]?.let { return it }
        return orbitMap[body]?.let {
            val orbitCount = 1 + countIndirectOrbits(it, orbitMap, memo)
            memo[body] = orbitCount
            orbitCount
        } ?: 0
    }

    private fun getOrbitPath(start: String, orbits: Map<String, String>): List<String> {
        var node: String? = orbits[start]
        val orbitPath = mutableListOf<String>()
        while (node != null) {
            orbitPath.add(node)
            node = orbits[node]
        }
        return orbitPath
    }

    /**
     * The orbit map is a series of lines like:
     * COM)B
     * B)C
     * Which indicates the object B orbits COM and C orbits B
     *
     * We represent that as a map of each body to the body it orbits:
     * B -> COM
     * C -> B
     */
    fun createChildToParentMap(input: List<String>): Map<String, String> {
        return input
            .map { it.split(")") }
            .associate { it[1] to it[0] }
    }
}