package com.jacobhyphentated.day18

import com.jacobhyphenated.day18.Day18
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day18Test {

    @Test
    fun testPart1_simple() {
        val input =
            """
                #########
                #b.A.@.a#
                #########
            """.trimIndent()
        val day18 = Day18()
        assertEquals(8, day18.part1(day18.createGrid(input)))
    }

    @Test
    fun testPart1_medium() {
        val input =
            """
                ########################
                #f.D.E.e.C.b.A.@.a.B.c.#
                ######################.#
                #d.....................#
                ########################
            """.trimIndent()
        val day18 = Day18()
        assertEquals(86, day18.part1(day18.createGrid(input)))
    }

    @Test
    fun testPart1_complex() {
        val input =
            """
                #################
                #i.G..c...e..H.p#
                ########.########
                #j.A..b...f..D.o#
                ########@########
                #k.E..a...g..B.n#
                ########.########
                #l.F..d...h..C.m#
                #################
            """.trimIndent()
        val day18 = Day18()
        assertEquals(136, day18.part1(day18.createGrid(input)))
    }

    @Test
    fun testPart2_simple() {
        val input =
            """
                #######
                #a.#Cd#
                ##...##
                ##.@.##
                ##...##
                #cB#Ab#
                #######
            """.trimIndent()
        val day18 = Day18()
        assertEquals(8, day18.part2(day18.createGrid(input)))
    }

    @Test
    fun testPart2_complex() {
        val input =
            """
                #############
                #g#f.D#..h#l#
                #F###e#E###.#
                #dCba...BcIJ#
                #####.@.#####
                #nK.L...G...#
                #M###N#H###.#
                #o#m..#i#jk.#
                #############
            """.trimIndent()
        val day18 = Day18()
        assertEquals(72, day18.part2(day18.createGrid(input)))
    }
}