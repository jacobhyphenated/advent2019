package com.jacobhyphenated.day14

import com.jacobhyphenated.Day
import java.io.File
import kotlin.math.ceil

// Space Stoichiometry
class Day14: Day<List<String>> {
    override fun getInput(): List<String> {
        return this.javaClass.classLoader.getResource("day14/input.txt")!!
            .readText()
            .lines()
    }

    /**
     * The ship processors can turn different materials into other materials.
     * Through a chain of such reactions, ORE can be turned into FUEL.
     * How much ORE is needed to produce 1 FUEL?
     */
    override fun part1(input: List<String>): Number {
        val createdFrom = parseInput(input)
        return requiredOre(createdFrom=createdFrom)
    }

    /**
     * We have 1 trillion ORE. How many fuel will this produce?
     *
     * A brute force solution that produces 1 fuel at a time until we run out of ore
     * and tracks the leftover materials between each step takes around 14 minutes to complete.
     *
     * This solution takes advantage of the fact that we don't need to produce 1 Fuel at a time.
     * We can pick any arbitrary amount of fuel.
     *
     * Doing high fuel increments like this solves the issue in ~60ms
     */
    override fun part2(input: List<String>): Number {
        val createdFrom = parseInput(input)
        val leftovers:MutableMap<String, Long> = mutableMapOf()
        var ore = 1_000_000_000_000
        var fuel = 0L

        // Start producing this amount of fuel per step
        var increment = 1_000_000L
        while (ore  > 0) {

            // To keep from overshooting our goal, we reduce our fuel increment if the next batch would use all our ore
            // Unless we are only doing 1 fuel at a time, then we use all the ore we can.
            if (increment != 1L){
                val lookaheadCost = requiredOre(ReactionComponent("FUEL", increment), createdFrom)
                if (ore - lookaheadCost  < 0 ) {
                    // The next batch more ore than we have, cut the increment by a factor of 10
                    increment /= 10
                    continue
                }
            }

            ore -= requiredOre(ReactionComponent("FUEL", increment), createdFrom, leftovers)
            fuel += increment
        }
        return fuel - 1
    }

    /**
     * Main recursive function for determining how many ORE are needed to produce some quantity of material.
     *
     * By default, the method will start at 1 FUEL.
     *
     * Track the leftovers where one material may be produced in greater quantity than needed. That can be re-used later.
     */
    private fun requiredOre(component: ReactionComponent = ReactionComponent("FUEL", 1),
                            createdFrom: Map<ReactionComponent, List<ReactionComponent>>,
                            leftovers: MutableMap<String, Long> = mutableMapOf() ): Long {

        // ORE is the base case - end the recursive stack.
        if (component.material == "ORE") {
            return component.quantity
        }

        // Find the reaction instructions to produce this material
        val reactionOutput = createdFrom.keys.first { it.material == component.material }

        // How many do we need to create (and do we have leftovers from other reactions we can use)?
        val numberToCreate = component.quantity - (leftovers[component.material] ?: 0)

        // If we have more leftovers than we need, then we can stop here.
        if (numberToCreate <= 0) {
            leftovers[component.material] = leftovers[component.material]!! - component.quantity
            return 0
        }

        // Determine how many times the reaction must run to produce the desired quantity of material
        val reactionsNeeded = ceil(numberToCreate.toDouble() / reactionOutput.quantity).toInt()

        // If we have an excess of materials after the reaction is run, save it for later
        leftovers[component.material] = reactionOutput.quantity * reactionsNeeded - numberToCreate

        // Now recursively count how many OREs are needed to produce the materials needed for this reaction
        return createdFrom.getValue(reactionOutput)
            .sumOf { requiredOre(ReactionComponent(it.material, it.quantity * reactionsNeeded), createdFrom, leftovers) }

    }

    /**
     * Here we break up the input lines. An example is:
     * 7 A, 1 B => 1 C
     *
     * This means it takes 7 A and 1 B to produce 1 C.
     *
     * We invert this in a map of (1C) to [(7A), (1B)]
     */
    private fun parseInput(input: List<String>): Map<ReactionComponent, List<ReactionComponent>> {
        val reactions = mutableMapOf<ReactionComponent, List<ReactionComponent>>()
        for (line in input) {
            val (lhs, rhs) = line.split("=>")
            reactions[parseReactionComponent(rhs)] = lhs.split(",").map { parseReactionComponent(it) }
        }

        return reactions
    }

    private fun parseReactionComponent(rc: String): ReactionComponent {
        val (quantity, material) = rc.trim().split(" ")
        return ReactionComponent(material, quantity.toLong())
    }
}

data class ReactionComponent(val material: String, val quantity: Long)