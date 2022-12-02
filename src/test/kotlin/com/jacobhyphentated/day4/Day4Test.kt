package com.jacobhyphentated.day4

import com.jacobhyphenated.day4.Day4
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day4Test {

    @Test
    fun testPart1() {
        val day4 = Day4()
        assertEquals(1, day4.part1(Pair(111111,111111)))
        assertEquals(0, day4.part1(Pair(223450,223450)))
        assertEquals(0, day4.part1(Pair(123789,123789)))
    }

    @Test
    fun testPart2() {
        val day4 = Day4()
        assertEquals(1, day4.part2(Pair(112233,112233)))
        assertEquals(0, day4.part2(Pair(123444,123444)))
        assertEquals(1, day4.part2(Pair(111122,111122)))
    }
}