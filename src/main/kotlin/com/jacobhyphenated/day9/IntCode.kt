package com.jacobhyphenated.day9

/**
 * Class to execute IntCode instructions
 *
 * Takes a mutable list of integers that represents the instruction set for the opcode program.
 *
 * [execute] will modify this instruction set. [execute] may be called multiple times
 * and will resume where the instruction pointer stopped after the previous execute.
 *
 * Once the program reaches the *halt* command, [isHalted] will be set to true
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
 * |9|Adjust Relative Base|
 * |99|Halt|
 *
 * * *input* - The input values for the program. Treated as a FIFO queue
 * * *output* - the output of the program. This is a list of outputted values in order reached during execution
 *
 *
 */
class IntCode(intCodeInstructions: List<Long>) {

    private val instructions = intCodeInstructions.withIndex().associate { (index, digit) -> index to digit }.toMutableMap()
    var input = mutableListOf<Long>()
    var output = mutableListOf<Long>()
    var isHalted = false
    private var pointer = 0
    private var relativeBase = 0

    /**
     * Execute the main run loop of the program.
     *
     * The execution loop will continue until halt(99) is reached
     * or if the program is awaiting on new input
     * or if the program runs out of instructions.
     *
     * @param inputs Optionally add input values for the program to read from.
     * These values are added to the end of the input queue
     */
    fun execute(inputs: List<Long> = listOf()) {
        input.addAll(inputs)
        while (pointer < instructions.size) {
//            println("$pointer | $output" )
//            println(instructions.map { it.toString() })
            val (opcode, modes) = parseOpCode(instructions.getValue(pointer))
            val resolveParam = useParamModeResolver(pointer, modes)
            val resolveWriteAddress = useParamWriteAddressResolver(pointer, modes)
            when(opcode) {
                1 -> instructions[resolveWriteAddress(3)] = resolveParam(1)  + resolveParam(2)
                2 -> instructions[resolveWriteAddress(3)] = resolveParam(1)  * resolveParam(2)
                3 -> instructions[resolveWriteAddress(1)] = if (input.isNotEmpty()) {input.removeAt(0) } else { break }
                4 -> output.add(resolveParam(1))
                7 -> instructions[resolveWriteAddress(3)] = if (resolveParam(1) < resolveParam(2)) { 1 } else { 0 }
                8 ->  instructions[resolveWriteAddress(3)] = if (resolveParam(1) == resolveParam(2)) { 1 } else { 0 }
                9 -> relativeBase += resolveParam(1).toInt()
                99 -> {isHalted = true; break}
            }

            pointer = when(opcode) {
                1, 2, 7, 8 -> pointer + 4
                3, 4, 9 -> pointer + 2
                5 -> if (resolveParam(1) != 0L) { resolveParam(2).toInt() } else { pointer + 3 }
                6 -> if (resolveParam(1) == 0L) { resolveParam(2).toInt() } else { pointer + 3 }
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
     * * Mode 2 is relative mode - the parameter refers to a memory address offset from the relative base
     *
     * Usage:
     * ```
     * val resolveParam = useParamModeResolver(pointer, modes)
     * val firstParamValue = resolveParam(1)
     * val secondParamValue = resolveParam(2)
     *
     * instructions[resolveWriteAddress(3)] = firstParamValue + secondParamValue
     * ```
     *
     * @param pointer the current instruction pointer for the operation
     * @param modes a list of parameter modes. Index 0 is used for parameter 1, etc.
     *
     * @return a function that takes the parameter number (1,2,etc.) and returns the appropriate value
     */
    private fun useParamModeResolver(pointer: Int, modes: List<Int>): (Int) -> Long {
        return { paramNumber: Int ->
            val parameter = instructions[pointer + paramNumber] ?: 0
            when (modes[paramNumber - 1]) {
                0 -> instructions[parameter.toInt()] ?: 0
                1 -> parameter
                2 -> instructions[relativeBase + parameter.toInt()] ?: 0
                else -> throw NotImplementedError("No parameter value $modes")
            }
        }
    }

    /**
     * Use like [useParamModeResolver] except for getting the memory address to write to
     *
     * Parameters associated with write operations cannot use immediate mode, so the only supported modes are:
     * * Mode 0 is position mode - the parameter refers to a memory address
     * * Mode 2 is relative mode - the parameter refers to a memory address offset from the relative base
     */
    private fun useParamWriteAddressResolver(pointer: Int, modes: List<Int>): (Int) -> Int {
        return { paramNumber: Int ->
            val parameter = instructions.getValue(pointer + paramNumber).toInt()
            when (modes[paramNumber - 1]) {
                0 -> parameter
                1 -> throw java.lang.IllegalArgumentException("Cannot use immediate mode with write address")
                2 -> relativeBase + parameter
                else -> throw NotImplementedError("No parameter value $modes")
            }
        }
    }

    private fun parseOpCode(opcode: Long): Pair<Int, List<Int>> {
        val padded = opcode.toString().padStart(5, '0').reversed()
        val opInstruction = "${padded[1]}${padded[0]}".toInt()
        val modes = padded.substring(2).toCharArray().map { it.digitToInt() }
        return Pair(opInstruction, modes)
    }
}