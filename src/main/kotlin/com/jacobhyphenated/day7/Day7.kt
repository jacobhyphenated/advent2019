package com.jacobhyphenated.day7

import com.jacobhyphenated.Day
import java.io.File

// Amplification Circuit
class Day7: Day<List<Int>> {
    override fun getInput(): List<Int> {
        return this.javaClass.classLoader.getResource("day7/input.txt")!!
            .readText()
            .split(",")
            .map { it.toInt() }
    }

    /**
     * 5 amplifiers are strung together. They each run the same IntCode program.
     * The initial input is 0, and the input for each subsequent amplifier
     * is the output from the previous amplifier.
     *
     * Additionally, each amplifier has a different phase input state.
     * The first input to each amplifier is the phase state, then the outputs of the chain.
     * The phase state is a number from 0 to 4, each one used only once.
     *
     * Find the maximum output after trying all phase state combinations
     */
    override fun part1(input: List<Int>): Int {
        val phaseInputs = allPossiblePhaseSettings()
        var maxThrusterSignal = 0
        for (phaseInput in phaseInputs) {
            var output = 0
            repeat(5) {
                val amplifier = IntCode(mutableListOf(*input.toTypedArray()))
                amplifier.execute(listOf(phaseInput[it], output))
                output = amplifier.output
            }
            if (output > maxThrusterSignal) {
                maxThrusterSignal = output
            }
        }

        return maxThrusterSignal
    }

    /**
     * Run the amplifiers on loop such that the output from the last
     * amplifier is the input from the first until the amplifiers receive a HALT (99)
     *
     * Phase states for this mode are in a range of 5 to 9.
     * Note: the phase state input should be read only once per amplifier.
     * Each amplifier should be re-used for each execution.
     */
    override fun part2(input: List<Int>): Int {
        val phaseInputs = allPossiblePhaseSettings(5..9)
        var maxThrusterSignal = 0
        for (phaseInput in phaseInputs) {
            val amps = mutableListOf<IntCode>()
            repeat(5) {
                val ic = IntCode(mutableListOf(*input.toTypedArray()))
                ic.input.add(phaseInput[it])
                amps.add(ic)
            }
            var output = 0
            while (!amps[amps.size - 1].isHalted) {
                for (amp in amps) {
                    amp.execute(listOf(output))
                    output = amp.output
                }
            }
            if (output > maxThrusterSignal) {
                maxThrusterSignal = output
            }
        }
        return maxThrusterSignal
    }

    private fun allPossiblePhaseSettings(range: IntRange = 0..4, list: MutableList<List<Int>> = mutableListOf(), prefix: List<Int> = listOf()): List<List<Int>> {
        if (prefix.size == 4) {
            for (i in range) {
                if (i !in prefix) {
                    list.add(listOf(*prefix.toTypedArray(), i))
                }
            }
            return list
        }
        for (i in range) {
            if (i !in prefix) {
                allPossiblePhaseSettings(range, list, listOf(*prefix.toTypedArray(), i))
            }
        }
        return list
    }
}