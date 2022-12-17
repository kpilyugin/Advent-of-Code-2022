import kotlin.math.max

fun main() {
    data class Valve(val id: String, val rate: Int, var tunnels: List<Pair<String, Int>>, val index: Int) {
        var opened = false

        fun needOpen() = rate > 0 && !opened
    }

    fun parseValves(input: List<String>): Map<String, Valve> {
        var index = 0
        return input.associate { line ->
            val (valvePart, tunnelsPart) = line.split(";")
            val (valveStr, rateStr) = valvePart.split(" has")
            val id = valveStr.substringAfter("Valve ")
            val rate = rateStr.substringAfter("=").toInt()
            val tunnels = if ("valves" in tunnelsPart)
                tunnelsPart.substringAfter("valves ").split(", ") else
                listOf(tunnelsPart.substringAfter("valve "))
            val valve = Valve(id, rate, tunnels.map { it to 1 }, index)
            index++
            id to valve
        }
    }

    fun prepareValves(input: List<String>): Map<String, Valve> {
        val valves = parseValves(input).toMutableMap()
        val initial = valves["AA"]!!

        fun Valve.useful() = this == initial || rate > 0
        fun Valve.replaceUnused() {
            val resolved = mutableListOf<Pair<String, Int>>()
            for ((id, dist) in tunnels) {
                val next = valves[id]!!
                if (next.useful()) {
                    resolved += id to dist
                } else {
                    resolved += next.tunnels.map { it.first to (it.second + dist) }
                }
            }
            tunnels = resolved.filter { it.first != id }
                .groupBy { it.first }
                .map { entry -> entry.key to entry.value.minOf { it.second } }
        }
        repeat(valves.size) {
            for (valve in valves.values) {
                valve.replaceUnused()
            }
        }
        valves.values.removeIf { !it.useful() }
        return valves
    }

    fun solve(valves: Map<String, Valve>, minutes: Int): Int {
        data class Key(val id: String, val minute: Int, val mask: Long)
        val memo = hashMapOf<Key, Int>()

        fun solveRecursive(minute: Int, sumOpened: Int, openedMask: Long, current: Valve): Int {
            if (minute > minutes) return 0
            val key = Key(current.id, minute, openedMask)
            val existing = memo[key]
            if (existing != null) {
                return existing
            }
            var res = sumOpened * (minutes - minute + 1)
            if (current.needOpen()) {
                current.opened = true
                val newSum = sumOpened + current.rate
                val mask = openedMask or (1L shl current.index)
                res = max(res, sumOpened + solveRecursive(minute + 1, newSum, mask, current))
                current.opened = false
            }
            for ((idNext, dist) in valves[current.id]!!.tunnels) {
                val valve = valves[idNext]
                if (valve != null && minute + dist <= minutes) {
                    res = max(res, sumOpened * dist + solveRecursive(minute + dist, sumOpened, openedMask, valve))
                }
            }
            memo[key] = res
            return res
        }
        return solveRecursive(1, 0, 0L, valves["AA"]!!)
    }

    fun part1(input: List<String>) = solve(prepareValves(input), 30)

    fun part2(input: List<String>): Int {
        val valves = prepareValves(input)
        val initial = valves["AA"]!!
        val list = valves.values.filter { it != initial }

        val maskAll = (1 shl list.size) - 1
        var res = 0
        for (mask in 0..maskAll) {
            val set1 = mutableMapOf<String, Valve>().apply { put(initial.id, initial) }
            val set2 = mutableMapOf<String, Valve>().apply { put(initial.id, initial) }
            for (i in list.indices) {
                if ((mask and (1 shl i)) != 0) {
                    set1[list[i].id] = list[i]
                } else {
                    set2[list[i].id] = list[i]
                }
            }
            res = max(res, solve(set1, 26) + solve(set2, 26))
        }
        return res
    }

    val testInput = readInputLines("Day16_test")
    check(part1(testInput), 1651)
    check(part2(testInput), 1707)

    val input = readInputLines("Day16")
    println(part1(input))
    println(part2(input))
}
