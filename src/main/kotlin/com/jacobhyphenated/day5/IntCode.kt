package com.jacobhyphenated.day5

/**
 * Class to execute IntCode instructions
 *
 * Takes a mutable list of integers that represents the instruction set for the opcode program.
 * [execute] will modify this instruction set and should only be done once
 *
 * |Code|Operation|
 * |-------|---------|
 * |1|Addition|
 * |2|Multiplication|
 * |3|Read input|
 * |4|Write output|
 * |5|Jump if not 0|
 * |6|Jump if 0|
 * |7|Less Than|
 * |8|Equal To|
 * |99|Halt|
 *
 * * *input* - The input value for the program
 * * *output* - the output of the program. If there are multiple outputs, this will be the last one
 *
 *
 */
class IntCode(val instructions: MutableList<Int>) {

    var input: Int = 0
    var output: Int = 0

    fun execute() {
        var pointer = 0
        while (pointer < instructions.size) {
//            println("$pointer | $output" )
//            println((0 until instructions.size).toList().map { it.toString().padStart(5, ' ') })
//            println(instructions.map { it.toString().padStart(5, ' ') })
            val (opcode, modes) = parseOpCode(instructions[pointer])
            val resolveParam = useParamModeResolver(pointer, modes)
            when(opcode) {
                1 -> instructions[instructions[pointer + 3]] = resolveParam(1)  + resolveParam(2)
                2 -> instructions[instructions[pointer + 3]] = resolveParam(1)  * resolveParam(2)
                3 -> instructions[instructions[pointer + 1]] = input
                4 -> output = resolveParam(1)
                7 -> instructions[instructions[pointer + 3]] = if (resolveParam(1) < resolveParam(2)) { 1 } else { 0 }
                8 ->  instructions[instructions[pointer + 3]] = if (resolveParam(1) == resolveParam(2)) { 1 } else { 0 }
                99 -> break
            }

            pointer = when(opcode) {
                1, 2, 7, 8 -> pointer + 4
                3, 4 -> pointer + 2
                5 -> if (resolveParam(1) != 0) { resolveParam(2) } else { pointer + 3 }
                6 -> if (resolveParam(1) == 0) { resolveParam(2) } else { pointer + 3 }
                else -> throw NotImplementedError("invalid opcode $opcode")
            }
        }
    }

    /**
     * Helper method that creates a higher order function to determine the value of a parameter
     *
     * Takes the instruction pointer and the list of modes for the operation parameters
     *
     * * Mode 0 is position mode - the parameter refers to a memory address
     * * Mode 1 is immediate mode - the value of the parameter in the instructions is used directly
     *
     * Usage:
     * ```
     * val resolveParam = useParamModeResolver(pointer, modes)
     * val firstParamValue = resolveParam(1)
     * val secondParamValue = resolveParam(2)
     * ```
     *
     * @param pointer the current instruction pointer for the operation
     * @param modes a list of parameter modes. Index 0 is used for parameter 1, etc.
     *
     * @return a function that takes the parameter number (1,2,etc.) and returns the appropriate value
     */
    private fun useParamModeResolver(pointer: Int, modes: List<Int>): (Int) -> Int {
        return { paramNumber: Int ->
            val parameter = instructions[pointer + paramNumber]
            when (modes[paramNumber - 1]) {
                0 -> instructions[parameter]
                1 -> parameter
                else -> throw NotImplementedError("No parameter value $modes")
            }
        }
    }

    private fun parseOpCode(opcode: Int): Pair<Int, List<Int>> {
        val padded = opcode.toString().padStart(4, '0').reversed()
        val opInstruction = "${padded[1]}${padded[0]}".toInt()
        val modes = padded.substring(2).toCharArray().map { it.digitToInt() }
        return Pair(opInstruction, modes)
    }
}