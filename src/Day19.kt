import kotlin.collections.HashSet

fun main() {
    data class Resources(val ore: Int = 0, val clay: Int = 0, val obsidian: Int = 0, val geode: Int = 0) :
        Comparable<Resources> {
        fun isAtLeast(other: Resources) =
            ore >= other.ore && clay >= other.clay && obsidian >= other.obsidian && geode >= other.geode

        operator fun minus(other: Resources) =
            Resources(ore - other.ore, clay - other.clay, obsidian - other.obsidian, geode - other.geode)

        operator fun plus(other: Resources) =
            Resources(ore + other.ore, clay + other.clay, obsidian + other.obsidian, geode + other.geode)

        operator fun times(count: Int) =
            Resources(ore * count, clay * count, obsidian * count, geode * count)

        override fun compareTo(other: Resources) =
            compareValuesBy(this, other, { it.geode }, { it.obsidian }, { it.clay }, { it.ore })

        override fun toString() = listOf(ore, clay, obsidian, geode).joinToString()
    }

    fun solve(blueprint: String, minutes: Int): Int {
        val parts = blueprint.split(':', '.')
        val orePrice = parts[1].extractNumbers().let { Resources(ore = it[0]) }
        val clayPrice = parts[2].extractNumbers().let { Resources(ore = it[0]) }
        val obsidianPrice = parts[3].extractNumbers().let { Resources(ore = it[0], clay = it[1]) }
        val geodePrice = parts[4].extractNumbers().let { Resources(ore = it[0], obsidian = it[1]) }

        data class State(val robots: Resources, val resources: Resources) : Comparable<State> {
            fun next(): List<State> = buildList {
                val resourcesAfter = resources + robots

                for ((price, newRobot) in listOf(
                    geodePrice to Resources(geode = 1),
                    obsidianPrice to Resources(obsidian = 1),
                    clayPrice to Resources(clay = 1),
                    orePrice to Resources(ore = 1)
                )) {
                    if (resources.isAtLeast(price)) {
                        add(State(robots + newRobot, resourcesAfter - price))
                    }
                }
                add(State(robots, resourcesAfter))
            }

            fun isBetter(other: State) =
                other != this && robots.isAtLeast(other.robots) && resources.isAtLeast(other.resources)

            override fun compareTo(other: State) = compareValuesBy(this, other, { it.resources }, { it.robots })
        }

        var states: List<State> = listOf(State(Resources(ore = 1), Resources()))
        repeat(minutes) { minute ->
            val newStatesSet = states.flatMapTo(HashSet()) { it.next() }

            val sorted = newStatesSet.toList().sortedDescending().take(100000)
            val prev = mutableListOf<State>()
            val iter = sorted.iterator()

            val newStates = mutableListOf<State>()
            while (iter.hasNext()) {
                val cur = iter.next()
                if (!prev.any { it.isBetter(cur) }) {
                    prev += cur
                    if (prev.size > 5) prev.removeAt(0)
                    newStates += cur
                }
            }
            states = newStates
        }

        val geodes = states.maxOf { it.resources.geode }
        return geodes
    }

    fun part1(input: List<String>) =
        input.withIndex().sumOf { line ->
            ((line.index + 1) * solve(line.value, 24)).also { println(it) }
        }

    fun part2(input: List<String>) = input.take(3).map { line -> solve(line, 32) }

    val testInput = readInputLines("Day19_test")
    check(part1(testInput), 33)
    check(part2(testInput), listOf(56, 62))

    val input = readInputLines("Day19")
    println(part1(input))
    val res2 = part2(input)
    println("$res2: ${res2[0] * res2[1] * res2[2]}")
}
