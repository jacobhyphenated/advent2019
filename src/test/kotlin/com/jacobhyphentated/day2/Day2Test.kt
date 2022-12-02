package com.jacobhyphentated.day2

import com.jacobhyphenated.day2.Day2
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day2Test {

    @Test
    fun testRunIntCode() {
        val day2 = Day2()
        assertEquals(30, day2.runIntCode(mutableListOf(1,1,1,4,99,5,6,0,99)))
    }
}