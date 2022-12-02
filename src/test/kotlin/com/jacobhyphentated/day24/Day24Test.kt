package com.jacobhyphentated.day24

import com.jacobhyphenated.day24.Day24
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day24Test {

    @Test
    fun testPart1() {
        val input = """
            ....#
            #..#.
            #..##
            ..#..
            #....
        """.trimIndent()
        val day = Day24()
        val grid = day.parseInputGrid(input)
        assertEquals(2129920, day.part1(grid))
    }

    @Test
    fun testPart2() {
        val input = """
            ....#
            #..#.
            #..##
            ..#..
            #....
        """.trimIndent()
        val day = Day24()
        val grid = day.parseInputGrid(input)
        assertEquals(99, day.bugsAfterMinutes(grid, 10))
    }
}