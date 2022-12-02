package com.jacobhyphenated.day22

import com.jacobhyphenated.Day
import java.io.File

class Day22: Day<List<Instruction>> {
    override fun getInput(): List<Instruction> {
        return parseInstructions(
            this.javaClass.classLoader.getResource("day22/input.txt")!!.readText()
        )
    }

    override fun part1(input: List<Instruction>): Number {
        val deck = List(10007) { it.toLong() }
        val shuffled = executeInstructions(input, deck)
        return shuffled.indexOf(2019)
    }

    override fun part2(input: List<Instruction>): Number {
        // TODO: verified we can reverse the value from the previous part
        // now we need to find a way to repeat the operation k times

        val reversed = input.reversed()
        var i = 2020L
        var lastDiff = 0L
        repeat(10) {
            i = reversed.fold(i){ index, instruction ->
                instruction.reverseFromIndex(index, 119315717514047L)
            }.also {newIndex ->
                val diff = if(newIndex < i) { newIndex + 119315717514047L - i } else { newIndex - i}
                println("run $it - $newIndex - $i = $diff} (${diff - lastDiff})")
                lastDiff = diff
            }
        }

        return input.reversed().fold(7171L){ index, instruction ->
            instruction.reverseFromIndex(index, 10007)
        }
    }

    fun executeInstructions(instructions: List<Instruction>, deck: List<Long>): List<Long> {
        return instructions.fold(deck) { d, instruction ->
            instruction.execute(d)
        }
    }

    fun parseInstructions(input: String): List<Instruction> {
        return input.lines().map {
            val n = it.split(" ").last()
            if (it.contains("stack")) {
                NewStack
            } else if (it.contains("increment")) {
                Increment(n.toInt())
            } else if (it.contains("cut")) {
                Cut(n.toInt())
            } else {
                throw UnsupportedOperationException("Invalid instruction $it")
            }
        }
    }
}

sealed interface Instruction {
    fun execute(deck: List<Long>): List<Long>

    fun reverseFromIndex(i: Long, size: Long): Long

}

object NewStack : Instruction {
    override fun execute(deck: List<Long>): List<Long> {
        return deck.reversed()
    }

    override fun reverseFromIndex(i: Long, size: Long): Long {
        return size - 1 - i
    }


}

class Increment(val n: Int): Instruction {
    override fun execute(deck: List<Long>): List<Long> {
        val newDeck: MutableList<Long?> = MutableList(deck.size) { null }
        var index = 0
        for (card in deck) {
            newDeck[index] = card
            index = (index + n) % deck.size
        }
        return newDeck.mapNotNull { it }
    }

    override fun reverseFromIndex(i: Long, size: Long): Long {
        // going forward:
        // index * n % size = final
        var modInverse = i
        while (modInverse % n != 0L) {
            modInverse += size
        }
        return modInverse / n
    }
}

class Cut(val n: Int): Instruction {
    override fun execute(deck: List<Long>): List<Long> {
        val (front, back) = if (n >= 0) {
            Pair(deck.subList(0,n), deck.subList(n, deck.size))
        } else {
            Pair(deck.subList(0, deck.size + n), deck.subList(deck.size + n, deck.size))
        }
        return back.toMutableList().apply {
            addAll(front)
        }
    }

    override fun reverseFromIndex(i: Long, size: Long): Long {
        // Note: as of kotlin 1.5, mod() is different from % and has the behavior of floorMod()
        // The % operator functions as .rem()
        // -9 % 10 == -9
        // -9.mod(10) == 1
        return (i + n).mod(size)
    }
}