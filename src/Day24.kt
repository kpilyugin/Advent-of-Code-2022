import java.util.*

fun main() {
    fun start() = Vec2(0, 1)

    fun List<String>.target() = Vec2(size - 1, get(0).length - 2)

    data class State(val minute: Int, val pos: Vec2): Comparable<State> {
        override fun compareTo(other: State): Int {
            return compareValuesBy(this, other, { it.minute }, { it.pos.x }, { it.pos.y })
        }
    }

    fun solve(table: List<String>, initial: Vec2, target: Vec2, startMinute: Int = 0): Int {
        val n = table.size - 1
        val m = table[0].length - 1

        fun isFree(minute: Int, pos: Vec2): Boolean {
            if (pos == initial) return true
            fun isFreeDir(dir: Vec2, sign: Char): Boolean {
                val moved = pos - Vec2(1, 1) + dir * (startMinute + minute)
                val x = moved.x.mod(n - 1) + 1
                val y = moved.y.mod(m - 1) + 1
                return table[x][y] != sign
            }
            return isFreeDir(Vec2(1, 0), '^') && isFreeDir(Vec2(-1, 0), 'v') &&
                    isFreeDir(Vec2(0, 1), '<') && isFreeDir(Vec2(0, -1), '>')
        }

        val queue = PriorityQueue<State>()
        val visited = mutableSetOf<State>()
        val initialState = State(0, initial)
        queue += initialState
        visited += initialState
        while (queue.isNotEmpty()) {
            val cur = queue.poll()
            for (dir in listOf(Vec2(0, 0), Vec2(-1, 0), Vec2(1, 0), Vec2(0, -1), Vec2(0, 1))) {
                val nextPos = cur.pos + dir
                if (nextPos.run { x in 0..n && y in 0..m && table[x][y] != '#'}) {
                    val minute = cur.minute + 1
                    if (nextPos == target) {
                        return minute
                    }
                    val nextState = State(minute, nextPos)
                    if (nextState !in visited) {
                        if (isFree(minute, nextPos)) {
                            queue += nextState
                            visited += nextState
                        }
                    }
                }
            }
        }
        return -1
    }

    fun part1(input: List<String>) = solve(input, start(), input.target())

    fun part2(input: List<String>): Int {
        val forward = solve(input, start(), input.target())
        val back = solve(input, input.target(), start(), startMinute = forward)
        val forward2 = solve(input, start(), input.target(), startMinute = forward + back)
        return forward + back + forward2
    }

    val testInput = readInputLines("Day24_test")
    check(part1(testInput), 18)
    check(part2(testInput), 54)

    val input = readInputLines("Day24")
    println(part1(input))
    println(part2(input))
}
