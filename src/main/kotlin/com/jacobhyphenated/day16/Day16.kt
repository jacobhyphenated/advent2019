package com.jacobhyphenated.day16

import com.jacobhyphenated.Day
import java.io.File
import kotlin.math.absoluteValue

// Flawed Frequency Transmission
class Day16: Day<List<Int>> {
    override fun getInput(): List<Int> {
        return this.javaClass.classLoader.getResource("day16/input.txt")!!
            .readText()
            .toCharArray()
            .map { it.digitToInt() }
    }

    /**
     * You take an input as a list of integers. Then apply 100 phases to the input.
     * Each digit is recalculated in each phase by applying a pattern to all digits in the input,
     * adding them up, and taking only the 1's column digit of the result.
     *
     * The pattern is [0,1,0,-1], but the pattern changes for each digit.
     * For digit 1: [0,1,0,-1], for digit 2 [0,0,1,1,0,0,-1,-1], 3: [0,0,0,1,1,1,0,0,0,-1,-1,-1].
     * the pattern repeats for all digits of the input, but truncate left one space (remove the first 0 only once).
     *
     * What is the first 8 digits of the final output?
     */
    override fun part1(input: List<Int>): Number {
        var current = input
        repeat(100) {
            current = calculatePhase(current)
        }
        return current.subList(0,8).joinToString("").toInt()
    }

    /**
     * The real input is the input repeated 10,000 times. Brute force no longer works.
     * The input length is 6.5MM. The brute force algorithm is O(m*n^2) where n is the input length (m is phases)
     *
     * We care about the 8 digits represented by the offset, which is the first 7 digits of the input.
     * Those digits are in the second half of the total input. That's important because of how the pattern works.
     * For any digit, i such that i >= fullInput.size / 2,
     * the pattern will look like 0,0,...,0,1,1...,1
     *
     * Starting at the last digit, the pattern is always ...0,1 (so the last digit never changes).
     * The second to last digit is always the sum of itself from the last phase, and the last digit
     *
     * We can write this another way - where f(i) is the value of the digit at index i after the phase change
     * and f'(i) is the value of the digit at index i before the phase change:
     * f(i) = f'(i) + f(i+1)
     *
     * I tried to implement this recursively and even with memoization and careful stack ordering it took 3 minutes to run.
     *
     * Instead, we'll do this iteratively (700ms)
     *
     * Return the 8 digits indicated by the offset after 100 phases.
     */
    override fun part2(input: List<Int>): Number {
        val offset = input.subList(0, 7).joinToString("").toInt()
        val fullInput: List<Int> =  mutableListOf<Int>().apply {
            repeat(10_000){
                addAll(input)
            }
        }
        // We don't need to care about any part of the input before the offset value, truncate that off the input
        var stepInput = fullInput.subList(offset, fullInput.size)
        repeat(100){// for each phase
            // the last digit is always equal to its original value (and the value of its previous phase)
            val nextPhase = mutableListOf(stepInput.last())
            // go backwards starting at the second to last digit in the input
            for (i in stepInput.size - 2 downTo 0) {
                // the digit is its value from the last phase plus the value of the digit to its right
                nextPhase.add((stepInput[i] + nextPhase.last()) % 10)
            }
            // next phase was computed in reverse order, flip it around, and we can go again
            stepInput = nextPhase.reversed()
        }
        return stepInput.subList(0,8).joinToString("").toInt()
    }

    /**
     * Brute force the phase calculation by applying the correct version of the pattern to each digit
     */
    fun calculatePhase(input: List<Int>): List<Int> {
        val basePattern = listOf(0,1,0,-1)
        // map over each digit starting at 1
        return (1..input.size).map {
            // that digit is then how many times each part of the pattern repeats
            // ex. for digit 4, we do 0,0,0,0,1,1,1,1,0,0,0,0,-1,-1,-1,-1 (4 repeats)
            val pattern = basePattern.flatMap { p -> List(it){ p } }
            // apply this pattern to the entire input to get the result for this digit
            applyOutputPattern(input, pattern).sum().absoluteValue % 10
        }

    }

    private fun applyOutputPattern(input: List<Int>, outputPattern: List<Int>): List<Int> {
        val pattern = outputPattern.toMutableList()
        while (pattern.size <= input.size) {
            pattern.addAll(pattern)
        }
        pattern.removeAt(0)

        return input.mapIndexed{i, value -> value * pattern[i] }
    }
}