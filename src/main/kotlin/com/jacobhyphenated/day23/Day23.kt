package com.jacobhyphenated.day23

import com.jacobhyphenated.Day
import com.jacobhyphenated.day9.IntCode
import java.io.File

// Category 6
class Day23: Day<List<Long>> {
    override fun getInput(): List<Long> {
        return this.javaClass.classLoader.getResource("day23/input.txt")!!
            .readText()
            .split(",")
            .map { it.toLong() }
    }

    /**
     * The network consists of 50 network controllers on ports 0 - 49.
     * Each program starts with the IntCode program with the initial input of its port
     *
     * The controllers output a packet in the structure of port,x,y.
     * The (x,y) packet is read into the network controller operating on 'port'.
     *
     * No operations are blocking. If there is no packet for a controller, it should read the input of -1
     *
     * return the first y value delivered to port 255
     */
    override fun part1(input: List<Long>): Number {
        val packets: MutableMap<Long, MutableList<Pair<Long,Long>>> = mutableMapOf()
        val networkControllers = List(50) { port ->
            packets[port.toLong()] = mutableListOf()
            IntCode(input).also { it.execute(listOf(port.toLong())) }
        }

        while(true) {
            networkControllers.forEachIndexed { port, controller ->
                while (controller.output.isNotEmpty()) {
                    val destination = controller.output.removeAt(0)
                    val x = controller.output.removeAt(0)
                    val y = controller.output.removeAt(0)
                    if (destination == 255L) {
                        return y
                    }
                    (packets[destination] ?: mutableListOf()).add(Pair(x,y))
                }
                if (packets.getValue(port.toLong()).isNotEmpty()) {
                    packets.getValue(port.toLong()).forEach {
                        val (x,y) = it
                        controller.input.add(x)
                        controller.input.add(y)
                    }
                }
                else {
                    controller.input.add(-1L)
                }
                controller.execute()
            }
        }
    }

    /**
     * The NAT keeps track of the last packet sent to port 255.
     *
     * If at any point, all network controllers are waiting for input (-1) such that no new packets are sent,
     * the nat delivers its packet to the network controller on port 0.
     *
     * return the first y value sent by the NAT twice in a row
     */
    override fun part2(input: List<Long>): Number {
        val packets: MutableMap<Long, MutableList<Pair<Long,Long>>> = mutableMapOf()
        val networkControllers = List(50) { port ->
            packets[port.toLong()] = mutableListOf()
            IntCode(input).also { it.execute(listOf(port.toLong())) }
        }
        var nat: Pair<Long,Long>? = null
        var lastNatDelivered = -1L

        while(true) {
            var allBlocked = true
            networkControllers.forEachIndexed { port, controller ->
                while (controller.output.isNotEmpty()) {
                    val destination = controller.output.removeAt(0)
                    val x = controller.output.removeAt(0)
                    val y = controller.output.removeAt(0)
                    if (destination == 255L) {
                        nat = Pair(x,y)
                    }
                    (packets[destination] ?: mutableListOf()).add(Pair(x,y))
                    if (destination in 0..49) {
                        allBlocked = false
                    }
                }
                if (packets.getValue(port.toLong()).isNotEmpty()) {
                    allBlocked = false
                    packets.getValue(port.toLong()).forEach {
                        val (x,y) = it
                        controller.input.add(x)
                        controller.input.add(y)
                    }
                    packets.getValue(port.toLong()).clear()
                }
                else {
                    controller.input.add(-1L)
                }
                controller.execute()
            }
            if (allBlocked && nat != null) {
                val (x,y) = nat!!
                networkControllers[0].execute(listOf(x,y))
                if (y == lastNatDelivered) {
                    return y
                } else {
                    lastNatDelivered = y
                }
            }
        }
    }
}