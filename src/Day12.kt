import java.util.PriorityQueue

fun main() {
    fun findAndReplace(table: List<CharArray>, ch: Char, replacement: Char) = buildList {
        for (i in table.indices) {
            for (j in table[0].indices) {
                if (table[i][j] == ch) {
                    table[i][j] = replacement
                    add(i to j)
                }
            }
        }
    }

    data class State(val x: Int, val y: Int, val dist: Int)

    fun bfs(table: List<CharArray>, start: List<Pair<Int, Int>>, target: Pair<Int, Int>): Int {
        val q = PriorityQueue<State>(compareBy { it.dist })
        val used = Array(table.size) { BooleanArray(table[0].size) }

        for ((sx, sy) in start) {
            q.add(State(sx, sy, 0))
            used[sx][sy] = true
        }
        while (q.isNotEmpty()) {
            val cur = q.poll()
            for ((dx, dy) in listOf(-1 to 0, 1 to 0, 0 to 1, 0 to -1)) {
                val nx = cur.x + dx
                val ny = cur.y + dy
                if (nx in table.indices &&
                    ny in table[0].indices &&
                    !used[nx][ny] &&
                    table[nx][ny] <= table[cur.x][cur.y] + 1
                ) {
                    if (nx == target.first && ny == target.second) {
                        return cur.dist + 1
                    }
                    q.add(State(nx, ny, cur.dist + 1))
                    used[nx][ny] = true
                }
            }
        }
        return -1
    }

    fun part1(input: List<String>): Int {
        val table = input.map { it.toCharArray() }
        val start = findAndReplace(table, 'S', 'a')
        val target = findAndReplace(table, 'E', 'z')[0]
        return bfs(table, start, target)
    }

    fun part2(input: List<String>): Int {
        val table = input.map { it.toCharArray() }
        val start = findAndReplace(table, 'a', 'a') + findAndReplace(table, 'S', 'a')
        val target = findAndReplace(table, 'E', 'z')[0]
        return bfs(table, start, target)
    }

    val testInput = readInputLines("Day12_test")
    check(part1(testInput), 31)
    check(part2(testInput), 29)

    val input = readInputLines("Day12")
    println(part1(input))
    println(part2(input))
}
