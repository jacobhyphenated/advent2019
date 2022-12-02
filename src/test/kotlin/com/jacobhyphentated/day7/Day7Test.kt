package com.jacobhyphentated.day7

import com.jacobhyphenated.day7.Day7
import com.jacobhyphenated.day7.IntCode
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day7Test {

    @Test
    fun testPart1(){
        val day7 = Day7()
        assertEquals(65210, day7.part1(listOf(3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,
            1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0)))
    }

    @Test
    fun testPart2() {
        val day7 = Day7()
        assertEquals(18216, day7.part2(listOf(3,52,1001,52,-5,52,3,53,1,52,56,54,1007,54,5,55,1005,55,26,1001,54,
            -5,54,1105,1,12,1,53,54,53,1008,54,0,55,1001,55,1,55,2,53,55,53,4,
            53,1001,56,-1,56,1005,56,6,99,0,0,0,0,10)))

        assertEquals(139629729, day7.part2(listOf(3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,
            27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5)))
    }

    @Test
    fun testSequence() {
        val ampA = IntCode(mutableListOf(3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0))
        ampA.execute(listOf(4,0))

        val ampB = IntCode(mutableListOf(3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0))
        ampB.execute(listOf(3,ampA.output))

        val ampC = IntCode(mutableListOf(3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0))
        ampC.execute(listOf(2,ampB.output))

        val ampD = IntCode(mutableListOf(3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0))
        ampD.execute(listOf(1,ampC.output))

        val ampE = IntCode(mutableListOf(3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0))
        ampE.execute(listOf(0,ampD.output))

        assertEquals(43210, ampE.output)
    }
}