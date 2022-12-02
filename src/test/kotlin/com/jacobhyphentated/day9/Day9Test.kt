package com.jacobhyphentated.day9

import com.jacobhyphenated.day9.IntCode
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day9Test {

    @Test
    fun testQuineOutput() {
        val input: List<Long> = listOf(109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99)
        val ic = IntCode(input)
        ic.execute()
        assertEquals(input, ic.output)
    }

    @Test
    fun testLargeNumber() {
        val ic = IntCode(listOf(1102,34915192,34915192,7,4,7,99,0))
        ic.execute()
        assertEquals(16, ic.output[0].toString().length)

        val ic2 = IntCode(listOf(104,1125899906842624,99))
        ic2.execute()
        assertEquals(1125899906842624, ic2.output[0])
    }
}