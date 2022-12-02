package com.jacobhyphentated.day8

import com.jacobhyphenated.day8.Day8
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day8Test {

    @Test
    fun testSeparateLayers() {
        val day8 = Day8()
        val layers = day8.separateLayers(6, listOf(1,2,3,4,5,6,7,8,9,0,1,2))
        assertEquals(2, layers.size)
        assertEquals(3, layers[0][2])
        assertEquals(6, layers[0].last())
        assertEquals(7, layers[1][0])
        assertEquals(0, layers[1][3])
    }
}