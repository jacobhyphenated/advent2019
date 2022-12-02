package com.jacobhyphentated.day5

import com.jacobhyphenated.day5.IntCode
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day5Test {

    @Test
    fun testIntCode() {
        val ic = IntCode(mutableListOf(1002,4,3,4,33))
        ic.execute()
        assertEquals(99, ic.instructions[ic.instructions.size - 1])

        val ic2 = IntCode(mutableListOf(1101,100,-1,4,0))
        ic2.execute()
        assertEquals(99, ic2.instructions[4])
    }

    @Test
    fun testInputOutputOperations() {
        val ic = IntCode(mutableListOf(3,0,4,0,99))
        ic.input = 500
        assertEquals(0, ic.output)
        ic.execute()
        assertEquals(500, ic.output)
    }

    @Test
    fun testEqualOperation() {
        val instructions = listOf(3,3,1108,-1,8,3,4,3,99)
        val ic = IntCode(mutableListOf(*instructions.toTypedArray()))
        ic.input = 4
        ic.execute()
        assertEquals(0, ic.output)

        val ic2 = IntCode(mutableListOf(*instructions.toTypedArray()))
        ic2.input = 8
        ic2.execute()
        assertEquals(1, ic2.output)
    }

    @Test
    fun testJumpOperation() {
        val icPositionMode = IntCode(mutableListOf(3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9))
        icPositionMode.input = 0
        icPositionMode.execute()
        assertEquals(0, icPositionMode.output)

        val icImmediateMode = IntCode(mutableListOf(3,3,1105,-1,9,1101,0,0,12,4,12,99,1))
        icImmediateMode.input = 100
        icImmediateMode.execute()
        assertEquals(1, icImmediateMode.output)
    }

    @Test
    fun testLongerExample() {
        val instructions = listOf(3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,
            1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,
            999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99)
        val icLessThan = IntCode(mutableListOf(*instructions.toTypedArray()))
        icLessThan.input = 2
        icLessThan.execute()
        assertEquals(999, icLessThan.output)

        val icEqualTo = IntCode(mutableListOf(*instructions.toTypedArray()))
        icEqualTo.input = 8
        icEqualTo.execute()
        assertEquals(1000, icEqualTo.output)

        val icGreaterThan = IntCode(mutableListOf(*instructions.toTypedArray()))
        icGreaterThan.input = 50
        icGreaterThan.execute()
        assertEquals(1001, icGreaterThan.output)
    }
}