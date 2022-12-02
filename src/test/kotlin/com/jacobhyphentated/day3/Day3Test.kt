package com.jacobhyphentated.day3

import com.jacobhyphenated.day3.Day3
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day3Test {

    @Test
    fun testPart1() {
        val day3 = Day3()
        val first = listOf("R8","U5","L5","D3")
        val second = listOf("U7","R6","D4","L4")
        assertEquals(6, day3.part1(Pair(first, second)))
    }

    @Test
    fun testPart1Longer() {
        val day3 = Day3()
        val first = listOf("R75","D30","R83","U83","L12","D49","R71","U7","L72")
        val second = listOf("U62","R66","U55","R34","D71","R55","D58","R83")
        assertEquals(159, day3.part1(Pair(first, second)))
    }

    @Test
    fun testPart2() {
        val day3 = Day3()
        val first = listOf("R8","U5","L5","D3")
        val second = listOf("U7","R6","D4","L4")
        assertEquals(30, day3.part2(Pair(first, second)))
    }

    @Test
    fun testPart2Longer() {
        val day3 = Day3()
        val first = listOf("R75","D30","R83","U83","L12","D49","R71","U7","L72")
        val second = listOf("U62","R66","U55","R34","D71","R55","D58","R83")
        assertEquals(610, day3.part2(Pair(first, second)))
    }
}