package com.jacobhyphentated.day10

import com.jacobhyphenated.day10.Asteroid
import com.jacobhyphenated.day10.Day10
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Day10Test {

    @Test
    fun testMapAsteroids() {
        val input =
            """
                .#..#
                .....
                #####
                ....#
                ...##
            """.trimIndent()
        val day10 = Day10()
        val asteroids = day10.mapAsteroids(input.lines())
        assertEquals(10, asteroids.size)
        assertTrue(asteroids.contains(Asteroid(1,0)))
        assertTrue(asteroids.contains(Asteroid(0,2)))
        assertTrue(asteroids.contains(Asteroid(1,2)))
        assertTrue(asteroids.contains(Asteroid(4,4)))
    }

    @Test
    fun testPart1Simple() {
        val input =
            """
                .#..#
                .....
                #####
                ....#
                ...##
            """.trimIndent()
        val day10 = Day10()
        val asteroids = day10.mapAsteroids(input.lines())
        assertEquals(8, day10.part1(asteroids))
    }

    @Test
    fun testPart1Expanded() {
        val input =
            """
                ......#.#.
                #..#.#....
                ..#######.
                .#.#.###..
                .#..#.....
                ..#....#.#
                #..#....#.
                .##.#..###
                ##...#..#.
                .#....####
            """.trimIndent()
        val day10 = Day10()
        val asteroids = day10.mapAsteroids(input.lines())
        assertEquals(33, day10.part1(asteroids))
    }

    @Test
    fun testPart1() {
        val input =
            """
                .#..#..###
                ####.###.#
                ....###.#.
                ..###.##.#
                ##.##.#.#.
                ....###..#
                ..#.#..#.#
                #..#.#.###
                .##...##.#
                .....#.#..
            """.trimIndent()
        val day10 = Day10()
        val asteroids = day10.mapAsteroids(input.lines())
        assertEquals(41, day10.part1(asteroids))
    }

    @Test
    fun testPart1Big() {
        val input =
            """
               .#..##.###...#######
                ##.############..##.
                .#.######.########.#
                .###.#######.####.#.
                #####.##.#.##.###.##
                ..#####..#.#########
                ####################
                #.####....###.#.#.##
                ##.#################
                #####.##.###..####..
                ..######..##.#######
                ####.##.####...##..#
                .#####..#.######.###
                ##...#.##########...
                #.##########.#######
                .####.#.###.###.#.##
                ....##.##.###..#####
                .#.#.###########.###
                #.#.#.#####.####.###
                ###.##.####.##.#..##
            """.trimIndent()
        val day10 = Day10()
        val asteroids = day10.mapAsteroids(input.lines())
        assertEquals(210, day10.part1(asteroids))
    }

    @Test
    fun testPart2() {
        val input =
            """
               .#..##.###...#######
                ##.############..##.
                .#.######.########.#
                .###.#######.####.#.
                #####.##.#.##.###.##
                ..#####..#.#########
                ####################
                #.####....###.#.#.##
                ##.#################
                #####.##.###..####..
                ..######..##.#######
                ####.##.####...##..#
                .#####..#.######.###
                ##...#.##########...
                #.##########.#######
                .####.#.###.###.#.##
                ....##.##.###..#####
                .#.#.###########.###
                #.#.#.#####.####.###
                ###.##.####.##.#..##
            """.trimIndent()
        val day10 = Day10()
        val asteroids = day10.mapAsteroids(input.lines())
        assertEquals(1006, day10.part2(asteroids))
    }
}