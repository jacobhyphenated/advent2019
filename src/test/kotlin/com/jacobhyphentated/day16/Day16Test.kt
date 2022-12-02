package com.jacobhyphentated.day16

import com.jacobhyphenated.day16.Day16
import org.junit.jupiter.api.Test
import kotlin.math.absoluteValue
import kotlin.test.Ignore
import kotlin.test.assertEquals

class Day16Test {

    @Test
    fun testPhaseCalculation() {
        val input = listOf(1,2,3,4,5,6,7,8)
        val day16 = Day16()
        val p1 = listOf(4,8,2,2,6,1,5,8)
        assertEquals(p1, day16.calculatePhase(input))

        val p2 = listOf(3,4,0,4,0,4,3,8)
        assertEquals(p2, day16.calculatePhase(p1))

        val p3 = listOf(0,3,4,1,5,5,1,8)
        assertEquals(p3, day16.calculatePhase(p2))

        val p4 = listOf(0,1,0,2,9,4,9,8)
        assertEquals(p4, day16.calculatePhase(p3))
    }

    @Test
    fun testPart1() {
        val input = "80871224585914546619083218645595".toCharArray().map { it.digitToInt() }
        val day16 = Day16()
        assertEquals(24176176, day16.part1(input))
    }

    @Test
    fun testPart2() {
        val input = "03036732577212944063491565474664".toCharArray().map { it.digitToInt() }
        val day16 = Day16()
        assertEquals(84462026, day16.part2(input))
    }
}