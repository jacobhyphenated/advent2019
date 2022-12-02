package com.jacobhyphentated.day1

import com.jacobhyphenated.day1.Day1
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day1Test {
    @Test
    fun testPart1() {
        val day1 = Day1()
        assertEquals(2, day1.part1(listOf(12)))
        assertEquals(2, day1.part1(listOf(14)))
        assertEquals(654, day1.part1(listOf(1969)))
        assertEquals(33583, day1.part1(listOf(100756)))
    }

    @Test
    fun testPart2() {
        val day1 = Day1()
        assertEquals(966, day1.part2(listOf(1969)))
        assertEquals(50346, day1.part2(listOf(100756)))
        assertEquals(50346, day1.part2(listOf(100756)))
    }
}