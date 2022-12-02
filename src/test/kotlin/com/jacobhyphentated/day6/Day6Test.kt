package com.jacobhyphentated.day6

import com.jacobhyphenated.day6.Day6
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day6Test {

    @Test
    fun testOrbitMapParse() {
        val day6 = Day6()
        val input = """
            COM)B
            B)C
            C)D
            D)E
            E)F
            B)G
            G)H
            D)I
            E)J
            J)K
            K)L""".trimIndent().lines()
        val map = day6.createChildToParentMap(input)
        assertEquals("COM", map["B"])
        assertEquals("D", map["I"])
        assertEquals("E", map["F"])
        assertEquals("J", map["K"])
    }

    @Test
    fun testPart1() {
        val day6 = Day6()
        val input = """
            COM)B
            B)C
            C)D
            D)E
            E)F
            B)G
            G)H
            D)I
            E)J
            J)K
            K)L""".trimIndent().lines()
        val map = day6.createChildToParentMap(input)
        assertEquals(42, day6.part1(map))
    }

    @Test
    fun testPart2() {
        val day6 = Day6()
        val input = """
            COM)B
            B)C
            C)D
            D)E
            E)F
            B)G
            G)H
            D)I
            E)J
            J)K
            K)L
            K)YOU
            I)SAN""".trimIndent().lines()
        val map = day6.createChildToParentMap(input)
        assertEquals(4, day6.part2(map))
    }
}