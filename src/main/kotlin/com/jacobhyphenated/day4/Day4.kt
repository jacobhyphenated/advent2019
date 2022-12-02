package com.jacobhyphenated.day4

import com.jacobhyphenated.Day

// Secure Container
class Day4: Day<Pair<Int, Int>> {
    override fun getInput(): Pair<Int, Int> {
        return Pair(156218,652527)
    }

    /**
     * A password must be 6 digits
     * Must fall within the range of the input
     * Must have adjacent digits be the same
     * Going from left to right, the digits must never decrease
     *
     * How many passwords in the range meet this criteria?
     */
    override fun part1(input: Pair<Int, Int>): Int {
        val (start, end) = input
        var count = 0
        for (password in start..end) {
            var noDecrease = true
            var consecutive = false
            val passAsString = password.toString()
            for (i in 1 until passAsString.length) {
                val current = passAsString[i].digitToInt()
                val previous = passAsString[i-1].digitToInt()
                if (current < previous) {
                    noDecrease = false
                    break
                }
                if (current == previous) {
                    consecutive = true
                }
            }
            if (noDecrease && consecutive) {
                count += 1
            }
        }
        return count
    }

    /**
     * The same adjacent digits cannot belong to a group of more than 2
     * ex 111111 is invalid
     * ex 123444 is invalid
     * ex 111122 is valid (1111 doesn't work but 22 does)
     */
    override fun part2(input: Pair<Int, Int>): Int {
        val (start, end) = input
        var count = 0
        for (password in start..end) {
            val passAsString = password.toString()
            var noDecrease = true
            for (i in 1 until passAsString.length) {
                val current = passAsString[i].digitToInt()
                val previous = passAsString[i - 1].digitToInt()
                if (current < previous) {
                    noDecrease = false
                    break
                }
            }
            val groupOf2 = passAsString.toCharArray()
                .groupBy { it }
                .values.map { it.size }
                .any { it == 2 }
            if (noDecrease && groupOf2) {
                count += 1
            }
        }
        return count
    }
}