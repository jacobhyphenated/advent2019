package com.jacobhyphentated.day22

import com.jacobhyphenated.day22.*
import org.junit.jupiter.api.Test
import kotlin.test.expect

class Day22Test {

    @Test
    fun testPart1() {
        val day = Day22()
        val input =
            """
                deal into new stack
                cut -2
                deal with increment 7
                cut 8
                cut -4
                deal with increment 7
                cut 3
                deal with increment 9
                deal with increment 3
                cut -1
            """.trimIndent()
        val instructions = day.parseInstructions(input)
        val deck = List(10) { it.toLong() }
        expect(listOf(9,2,5,8,1,4,7,0,3,6).map { it.toLong() }) { day.executeInstructions(instructions, deck) }
    }

    @Test
    fun testIncrement9() {
        val day = Day22()
        val instructions = listOf(Increment(9))
        val deck = List(10) { it.toLong() }
        expect(listOf(0,9,8,7,6,5,4,3,2,1).map { it.toLong() }) { day.executeInstructions(instructions, deck) }
    }

    @Test
    fun testReverseInstructions() {
        val day = Day22()
        val input =
            """
                deal into new stack
                cut -2
                deal with increment 7
                cut 8
                cut -4
                deal with increment 7
                cut 3
                deal with increment 9
                deal with increment 3
                cut -1
            """.trimIndent()
        val instructions = day.parseInstructions(input).reversed()
        val index4 = instructions.fold(4L){ index, instruction ->
            instruction.reverseFromIndex(index, 10).also {
                println("${printInstruction(instruction)} | from $index to $it")
            }
        }
        expect(1L) { index4 }

        val index5 = instructions.fold(5L){ index, instruction ->
            instruction.reverseFromIndex(index, 10).also {
                println("${printInstruction(instruction)} | from $index to $it")
            }
        }
        expect(4L) { index5 }

        val index0 = instructions.fold(0L){ index, instruction ->
            instruction.reverseFromIndex(index, 10).also {
                println("${printInstruction(instruction)} | from $index to $it")
            }
        }
        expect(9L) { index0 }
    }

    private fun printInstruction(i: Instruction): String {
        return when(i) {
            is Cut -> "Cut ${i.n}"
            is Increment -> "Increment ${i.n}"
            is NewStack -> "New Stack"
        }
    }
}