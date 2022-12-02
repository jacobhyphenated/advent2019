package com.jacobhyphenated

interface Day<T> {

    fun getInput(): T
    fun part1(input: T): Number
    fun part2(input: T): Number

    fun run() {
        val input = getInput()
        var start = System.currentTimeMillis()
        println("Part 1: ${part1(input)} (${System.currentTimeMillis() - start}ms)")
        start = System.currentTimeMillis()
        println("Part 2: ${part2(input)} (${System.currentTimeMillis() - start}ms)")
    }
}