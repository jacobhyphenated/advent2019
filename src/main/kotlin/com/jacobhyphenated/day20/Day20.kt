package com.jacobhyphenated.day20

import com.jacobhyphenated.Day
import java.io.File
import java.util.*

// Donut Maze
class Day20: Day<String> {
    override fun getInput(): String {
        return this.javaClass.classLoader.getResource("day20/input.txt")!!
            .readText()
    }

    /**
     * The maze has portals (described with 2-letter codes) that connect separate parts of the maze.
     * Find the shortest path between the start node (AA) and the end node (ZZ).
     *
     * Note that portal nodes (AA,ZZ,AK, etc.) are not spaces in the maze, but labels next to their adjacent spaces
     * (stepping onto the portal does not count as a step)
     *
     * For part 1, parsing a graph from the maze input is the hard part.
     * Once that graph is created, use Dijkstra's algorithm.
     */
    override fun part1(input: String): Number {
        val start = createGraph(input)
        val distances: MutableMap<String, Int> = mutableMapOf()
        // Use a priority queue implementation - min queue sorted by lowest "cost"
        val queue = PriorityQueue<NodeCost> { a, b -> a.cost - b.cost }
        queue.add(NodeCost(start, 0))
        distances[start.name] = 0

        while (queue.size > 0) {
            // Traverse from the lowest cost position on our queue. This position is "solved"
            val current = queue.remove()

            // If we already found a less expensive way to reach this position
            if (current.cost > (distances[current.node.name] ?: Int.MAX_VALUE)) {
                continue
            }

            // We've found the end of the maze, we can stop
            if (current.node.name == "ZZ") {
                break
            }

            current.node.neighbors.forEach {
                // when computing cost, do not increment cost for portal spaces
                val cost = distances.getValue(current.node.name) + if (current.node.isPortal) { 0 } else { 1 }
                // If the cost to this space is less than what was previously known, put this on the queue
                if (cost < (distances[it.name] ?: Int.MAX_VALUE)) {
                    distances[it.name] = cost
                    queue.add(NodeCost(it, cost))
                }
            }
        }
        return distances["ZZ"]!! - 1 // subtract 1 because "ZZ" is not technically a space
    }

    /**
     * Each portal in the maze is in the interior or the exterior of the maze.
     * When a portal is passed through, you enter a new depth (interior portals increase depth, exterior portals decrease).
     * You start on depth of 0 and can only exit ZZ at depth 0.
     * Find the shortest number of steps to get from AA to ZZ.
     */
    override fun part2(input: String): Number {
        val start = createGraph(input)
        val next = start.neighbors[0]
        return recursiveMaze(next, start, 0, 0)!!
    }

    /**
     * Use a recursive Depth First Search driven by this function.
     * Each move taken goes from on portal location to another portal location (use Dijkstra to find the shortest path to each portal).
     *
     * @param current the current node - this node is always adjecent to the portal that was just passed through
     * @param previous the node representing the portal that was just passed through (so we don't go backwards)
     * @param currentCost the total cost so far to reach this node
     * @param depth the current depth
     * @param solutions a mutable map representing all solutions found so far
     *
     * @return the total cost to solve the maze for this DFS branch, or null if the DFS branch is invalid
     */
    private fun recursiveMaze(current: Node, previous: Node, currentCost: Int, depth: Int, solutions: MutableSet<Int> = mutableSetOf() ): Int? {

        // We've already solved the maze in less time, we can prune this DFS branch
        if (currentCost > (solutions.minOrNull() ?: Int.MAX_VALUE)) {
            return null
        }

        // The puzzle input has less than 30 portals. We should never go that deep.
        // Also set a boundary for too much cost
        // Use these to prune DFS branches that are clearly not going to be correct
        if (depth > 30 || currentCost > 20000) {
            return null
        }

        // Use Dijkstra to find all accessible portals and the cost to get their
        val distances: MutableMap<String, Int> = mutableMapOf()
        val queue = PriorityQueue<NodeCost> { a, b -> a.cost - b.cost }
        queue.add(NodeCost(current, 0))
        distances[current.name] = 0
        val portals: MutableList<Pair<Node, Int>> = mutableListOf()

        while (queue.size > 0) {
            val currentNode = queue.remove()

            // If we already found a less expensive way to reach this position
            if (currentNode.cost > (distances[currentNode.node.name] ?: Int.MAX_VALUE)) {
                continue
            }

            for (neighbor in currentNode.node.neighbors){
                // don't go back through the portal we came from
                if (neighbor.name == previous.name) {
                    continue
                }
                val cost = distances.getValue(currentNode.node.name)
                // Don't traverse to portals, instead save the node/cost to the portal's edge
                if (neighbor.isPortal) {
                    portals.add(Pair(currentNode.node, cost))
                }
                else if (cost + 1 < (distances[neighbor.name] ?: Int.MAX_VALUE)) {
                    distances[neighbor.name] = cost + 1
                    queue.add(NodeCost(neighbor, cost))
                }
            }
        }

        // Once we have the portals, recursively search the next leg of the solution for each portal we can reach
        return portals.mapNotNull { (node, costToPortal) ->
            val portal = node.neighbors.filter { it.isPortal }[0]
            if (portal.name == "AA") { // we never go back to the start
                null
            } else if (portal.name == "ZZ"){
                if (depth != 0) {
                    null// can't solve the maze unless we are at depth 0
                } else {
                    // We have a valid solution!
                    solutions.add(currentCost + costToPortal)
                    currentCost + costToPortal
                }
            } else{
                val nextNode = portal.neighbors.filter { it.name != node.name }[0]
                val nextDepth = if (node.outer) { depth - 1 } else { depth + 1}
                if (nextDepth < 0) {
                    null
                }else {
                    recursiveMaze(nextNode, portal, currentCost + costToPortal + 1, nextDepth, solutions)
                }
            }
        }.minOrNull() // Filter our invalid null branches, then keep only the lowest cost branch
    }

    /**
     * Create a graph of nodes, where each node is a position in the maze
     *
     * Portals count as their own nodes connecting separate parts of the graph
     * (this is important since portals don't really work that way in the maze.
     * In the maze, you pass from one node next to the portal to the other in 1 step.
     * But since we need info on the portal for part 2, we keep them in the graph)
     *
     * @param input string representation of the maze
     * @return the starting node of the maze
     */
    private fun createGraph(input: String): Node{
        val grid = input.lines().map { it.toCharArray().map { c -> c }  }
        val byPosition: MutableMap<Pair<Int,Int>, Node> = mutableMapOf()
        val byName: MutableMap<String, Node> = mutableMapOf()
        val startName = "AA"
        val (startingPosition, startDirection) = findPortalPosition(grid, startName)
        val startNode = Node(startName, outer = true, isPortal = true)

        // To prevent duplicate node creation, maintain lookup maps for position and portal name
        byPosition[startingPosition] = startNode
        byName[startName] = startNode

        // Use a stack to track nodes/positions we need to resolve
        val stack = mutableListOf(ResolveNode(startDirection.location(startingPosition), startDirection, startNode))
        while (stack.isNotEmpty()) {
            val next = stack.removeAt(0)
            if (next.position in byPosition) {
                val existingNode = byPosition[next.position]!!
                next.parent.neighbors.add(existingNode)
                existingNode.neighbors.add(next.parent)
                continue
            }
            val (row,col) = next.position
            // use an outer/inner label to help determine portal depth for part 2
            // anything along the outside edge of the maze gets labeled outer
            val outer =  row < 3 || row >= grid.size - 3 || col < 3 || col >= grid[row].size - 3

            // Nodes must have a name for identification (use a position string for maze nodes and the portal name for portals)
            var node = Node(next.position.let{ (r,c) -> "$r,$c"}, outer)

            // This next node is a named portal, so it requires special logic
            if (grid[row][col] != '.') {
                val c1 = grid[row][col]
                val c2 = next.direction.location(next.position).let { (r,c) -> grid[r][c] }
                val name = when(next.direction) {
                    Direction.DOWN, Direction.RIGHT -> "$c1$c2"
                    Direction.LEFT, Direction.UP -> "$c2$c1"
                }
                // If we don't already know about this portal, create the node
                if (!byName.contains(name)) {
                    node = Node(name, outer,true)
                    byName[name] = node
                    if (name != "ZZ"){
                        // Find the portal's other end, and continue parsing out the graph from there as well
                        val (otherPortal, otherDirection)  = findPortalPosition(grid, name, next.position)
                        val neighborPosition = otherDirection.location(otherPortal)
                        stack.add(ResolveNode(neighborPosition, otherDirection, node))
                    }
                }
            }
            next.parent.neighbors.add(node)
            node.neighbors.add(next.parent)
            byPosition[next.position] = node
            // find neighbors
            if (grid[row][col] == '.') {
                for (direction in Direction.values()) {
                    if (direction == next.direction.reverse()) {
                        continue
                    }
                    val position = direction.location(next.position)
                    val (r,c) = position
                    if (grid[r][c] == ' ' || grid[r][c] == '#') {
                        continue
                    }
                    stack.add(ResolveNode(position, direction, node))
                }
            }
        }
        return startNode
    }

    /**
     * Parsing out portals from the maze is difficult because they are 2 characters in length and can be up/down or sideways
     *
     * This method looks through the 2d input grid for a portal of a particular 2 character name.
     * For portals that are not AA and ZZ, there are two such locations, so there is an optional exclusion for one end of the portal.
     *
     * @return The position in the grid of the portal (adjacent to an open maze position '.')
     * AND the direction from this position the open maze position is.
     */
    private fun findPortalPosition(grid: List<List<Char>>, name: String, exclude: Pair<Int,Int>? = null): Pair<Pair<Int,Int>, Direction> {
        val (char1, char2) = name.toCharArray().map { c -> c }
        for (r in 0 until grid.size - 1) {
            for (c in 0 until grid[r].size - 1) {
                if (exclude != null ) {
                    // don't look for the portal in areas we explicitly excluded (to keep from finding the portal end we already know about)
                    val (excludeR, excludeC) = exclude
                    if (r >= excludeR - 1 && r <= excludeR + 1 && c >= excludeC - 1 && c <= excludeC + 1) {
                        continue
                    }
                }
                if (grid[r][c] == char1) {
                    // up/down
                    if (grid[r+1][c] == char2) {
                        return if (r > 0 && grid[r-1][c] == '.') { Pair(Pair(r,c), Direction.UP) } else { Pair(Pair(r+1, c), Direction.DOWN) }
                    }
                    // sideways
                    if (grid[r][c+1] == char2) {
                        return if (c > 0 && grid[r][c-1] == '.') { Pair(Pair(r,c), Direction.LEFT) } else { Pair(Pair(r, c+1), Direction.RIGHT) }
                    }
                }
            }
        }
        throw UnsupportedOperationException("Could not find the portal location $name")
    }

}

private class Node(val name: String, val outer: Boolean, val isPortal: Boolean = false, val neighbors: MutableList<Node> = mutableListOf())

private class ResolveNode(val position: Pair<Int,Int>, val direction: Direction, val parent: Node)

private class NodeCost(val node: Node, val cost: Int)

/**
 * Helper enum class used when initially creating the graph
 * Helps track what direction we are traversing the input grid to prevent duplications
 */
private enum class Direction {
    UP,
    LEFT,
    DOWN,
    RIGHT;

    fun location(current: Pair<Int,Int>): Pair<Int,Int> {
        val (r,c) = current
        return when(this) {
            UP -> Pair(r-1,c)
            LEFT -> Pair(r, c-1)
            DOWN -> Pair(r+1, c)
            RIGHT -> Pair(r, c+1)
        }
    }

    fun reverse(): Direction {
        return when(this) {
            UP -> DOWN
            LEFT -> RIGHT
            DOWN -> UP
            RIGHT -> LEFT
        }
    }
}