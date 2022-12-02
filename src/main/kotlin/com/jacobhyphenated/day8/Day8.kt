package com.jacobhyphenated.day8

import com.jacobhyphenated.Day
import java.io.File

// Space Image Format
class Day8: Day<List<Int>> {
    override fun getInput(): List<Int> {
        return this.javaClass.classLoader.getResource("day8/input.txt")!!
            .readText()
            .toCharArray()
            .map { it.digitToInt() }
    }

    /**
     *  The image comes in multiple layers. Your image size is 25 x 6.
     *  The input is therefore n layers of 25x6 length.
     *
     *  Find the layer with the fewest 0 digits.
     *  Return the number of 1s times the number of 2s for that layer.
     */
    override fun part1(input: List<Int>): Int {
        val imageSize = 25 * 6
        val layers = separateLayers(imageSize, input)
        val layerWithFewest0 = layers.minBy { it.count { digit -> digit == 0 } }

        return layerWithFewest0.count { it == 1 } * layerWithFewest0.count { it == 2 }
    }

    /**
     * Each digit can be 0 - black, 1 - white, or 2 - transparent.
     * The first layer is read first. For each pixel, you only need to look at
     * other layers if the top layers are transparent.
     *
     * Print the output of the message (black and white pixels) and read the message:
     * Message is: ZLRJF
     */
    override fun part2(input: List<Int>): Int {
        val imageSize = 25 * 6
        val layers = separateLayers(imageSize, input)
        val result = mutableListOf<Int>()

        for (i in 0 until imageSize) {
            for (l in layers) {
                if (l[i] == 0 || l[i] == 1) {
                    result.add(l[i])
                    break
                }
            }
        }

        // Print out the result substituting characters for colors that are readable in console output
        val sb = java.lang.StringBuilder()
        for ((i, digit) in result.withIndex()) {
            if (i % 25 == 0) {
                sb.append("\n")
            }
            sb.append(if (digit == 0) { " " } else { "0"})
        }
        println(sb.toString())

        // return value doesn't actually matter since we have to eyeball the answer
        return 0
    }

    fun separateLayers(imageSize: Int, input: List<Int>): List<List<Int>> {
        return input.foldIndexed(mutableListOf<MutableList<Int>>()) { i, acc, digit ->
            if (i % imageSize == 0){
                acc.add(mutableListOf())
            }
            acc[acc.size - 1].add(digit)
            acc
        }
    }

}