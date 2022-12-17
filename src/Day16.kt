import kotlin.math.max

fun main() {
    class Valve(val id: String, val rate: Int, val tunnels: List<String>, val index: Int) {
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
            val valve = Valve(id, rate, tunnels, index)
            index++
            id to valve
        }
    }

    fun part1(input: List<String>): Int {
        val valves = parseValves(input)

        data class Key(val id: String, val day: Int, val mask: Long)

        val memo = hashMapOf<Key, Int>()

        fun solve(day: Int, sumOpened: Int, openedMask: Long, current: Valve): Int {
            if (day > 30) return 0
            val key = Key(current.id, day, openedMask)
            val existing = memo[key]
            if (existing != null) {
                return existing
            }
            val thisDay = sumOpened
            var res = sumOpened * (30 - day + 1)
            if (current.needOpen()) {
                current.opened = true
                val newSum = sumOpened + current.rate
                val mask = openedMask or (1L shl current.index)
                res = max(res, thisDay + solve(day + 1, newSum, mask, current))
                current.opened = false
            }
            for (idNext in valves[current.id]!!.tunnels) {
                val valve = valves[idNext]!!
                res = max(res, thisDay + solve(day + 1, sumOpened, openedMask, valve))
            }
            memo[key] = res
            return res
        }
        return solve(1, 0, 0L, valves["AA"]!!)
    }

    fun part2(input: List<String>): Int {
        val valves = parseValves(input)

        data class Key(val ids: Set<String>, val day: Int, val mask: Long)

        val memo = hashMapOf<Key, Int>()
        var maxOpened = 0

        println("Sum of valves = ${valves.values.sumOf { it.rate }}")

        fun hasUnopenedFrom(current: Valve, next: Valve): Boolean {
            val visited = HashSet<Valve>()
            fun dfs(cur: Valve) {
                for (id in cur.tunnels) {
                    val nxt = valves[id]!!
                    if (nxt !in visited) {
                        visited += nxt
                        dfs(nxt)
                    }
                }
            }
            visited += current
            dfs(next)
            return visited.any { it.needOpen() }
        }

        fun solve(day: Int, sumOpened: Int, openedMask: Long, current1: Valve, current2: Valve): Int {
            val key = Key(setOf(current1.id, current2.id), day, openedMask)
            val existing = memo[key]
            if (existing != null) {
                return existing
            }
            if (day > 21) return 0

            maxOpened = max(sumOpened, maxOpened)

            val thisDay = sumOpened
            var res = sumOpened * (26 - day + 1)
            val allOpened = valves.values.all { it.opened || it.rate == 0 }
            if (allOpened) {
                memo[key] = res
                return res
            }

            if (day < 3) {
                println("${memo.size} : $key")
            }

            val options1 = 1 + valves[current1.id]!!.tunnels.size
            val options2 = 1 + valves[current2.id]!!.tunnels.size
            fun Valve.next(option: Int) = valves[tunnels[option - 1]]!!

            for (option1 in 0 until options1) {
                for (option2 in 0 until options2) {
                    var isValid = true
                    if (current1.id == current2.id && option1 == option2) isValid = false
                    if (option1 == 0 && !current1.needOpen()) isValid = false
                    if (option2 == 0 && !current2.needOpen()) isValid = false
//                    if (option1 > 0 && !hasUnopenedFrom(current1, current1.next(option1))) isValid = false
//                    if (option2 > 0 && !hasUnopenedFrom(current2, current2.next(option2))) isValid = false

                    if (isValid) {
                        var nextSum = sumOpened
                        var nextMask = openedMask
                        var next1 = current1
                        var next2 = current2
                        if (option1 == 0) {
                            current1.opened = true
                            nextSum += current1.rate
                            nextMask = nextMask or (1L shl current1.index)
                        } else {
                            next1 = current1.next(option1)
                        }
                        if (option2 == 0) {
                            current2.opened = true
                            nextSum += current2.rate
                            nextMask = nextMask or (1L shl current2.index)
                        } else {
                            next2 = current2.next(option2)
                        }
                        res = max(res, thisDay + solve(day + 1, nextSum, nextMask, next1, next2))
                        if (option1 == 0) current1.opened = false
                        if (option2 == 0) current2.opened = false
                    }
                }
            }
            memo[key] = res
            return res
        }
        val initial = valves["AA"]!!
        val res = solve(1, 0, 0L, initial, initial)
        println("Max opened: $maxOpened")
        println("Memo size = ${memo.size}")
        return res
    }

    val testInput = readInputLines("Day16_test")
//    check(part1(testInput), 1651)
    check(part2(testInput), 1707)

    val input = readInputLines("Day16")
//    println(part1(input))
    println(part2(input))
}
