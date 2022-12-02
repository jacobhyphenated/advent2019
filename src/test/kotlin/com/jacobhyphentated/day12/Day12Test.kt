package com.jacobhyphentated.day12

import com.jacobhyphenated.day12.Day12
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day12Test {

    @Test
    fun testTotalEnergy() {
        val positions = listOf(
            listOf(2,1,-3),
            listOf(1,-8,0),
            listOf(3,-6,1),
            listOf(2,0,4)
        )

        val velocities = listOf(
            listOf(-3,-2,1),
            listOf(-1,1,3),
            listOf(3,2,-3),
            listOf(1,-1,-1)
        )
        val day12 = Day12()
        assertEquals(179, day12.totalEnergy(positions, velocities) )
    }

    @Test
    fun testPart1() {
        val input = listOf(
            listOf(-8,-10,0),
            listOf(5,5,10),
            listOf(2,-7,3),
            listOf(9,-8,-3)
        )
        val day12 = Day12()
        val (positions, velocities) = day12.doSteps(100, input)
        assertEquals(1940, day12.totalEnergy(positions, velocities))
    }

    @Test
    fun testPart2_short() {
        val input = listOf(
            listOf(-1,0,2),
            listOf(2,-10,-7),
            listOf(4,-8,8),
            listOf(3,5,-1)
        )
        val day12 = Day12()
        assertEquals(2772L, day12.part2(input))
    }

    @Test
    fun testPart2_long() {
        val input = listOf(
            listOf(-8,-10,0),
            listOf(5,5,10),
            listOf(2,-7,3),
            listOf(9,-8,-3)
        )
        val day12 = Day12()
        assertEquals(4686774924L, day12.part2(input))
    }
}