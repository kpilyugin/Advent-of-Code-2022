import java.lang.Integer.max

fun main() {
    fun List<Int>.indicesOfVisible(): List<Int> {
        return indices.filter { cur ->
            indices.all {
                it <= cur || get(it) < get(cur)
            } || indices.all {
                it >= cur || get(it) < get(cur)
            }
        }
    }

    fun part1(input: List<String>): Int {
        val table = input.map { line -> line.map { it.digitToInt() } }
        val n = table.size
        val m = table[0].size
        val d = Array(n) { IntArray(m) }

        for (i in 0 until n) {
            for (j in table[i].indicesOfVisible()) {
                d[i][j] = 1
            }
        }
        for (j in 0 until m) {
            val column = table.map { it[j] }
            for (i in column.indicesOfVisible()) {
                d[i][j] = 1
            }
        }
        return d.sumOf { it.sum() }
    }

    fun part2(input: List<String>): Int {
        val table = input.map { line -> line.map { it.digitToInt() } }
        val n = table.size
        val m = table[0].size

        var res = 0
        for (i in 0 until n) {
            for (j in 0 until m) {

                fun canSee(dx: Int, dy: Int): Int {
                    var x = i
                    var y = j
                    var visible = 0
                    while (true) {
                        val x1 = x + dx
                        val y1 = y + dy
                        if (x1 in 0 until n && y1 in 0 until m) {
                            visible++
                            if (table[x1][y1] < table[i][j]) {
                                x = x1
                                y = y1
                            } else {
                                return visible
                            }
                        } else {
                            return visible
                        }
                    }
                }

                res = max(res,
                    canSee(0, 1) * canSee(1, 0) * canSee(-1, 0) * canSee(0, -1)
                )
            }
        }
        return res
    }

    val testInput = readInputLines("Day08_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 8)

    val input = readInputLines("Day08")
    println(part1(input))
    println(part2(input))
}
