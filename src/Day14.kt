import kotlin.math.max

fun main() {
    fun solve(input: List<String>, withFloor: Boolean): Int {
        val d = Array(200) { CharArray(1000) { '.' } }
        var maxY = 0
        input.forEach { line ->
            line.split(" -> ")
                .map {
                    val (x, y) = it.split(",").map(String::toInt)
                    x to y
                }
                .zipWithNext()
                .forEach {
                    val (p0, p1) = it
                    for (x in rangeBetween(p0.first, p1.first)) {
                        for (y in rangeBetween(p0.second, p1.second)) {
                            d[y][x] = '#'
                            maxY = max(maxY, y)
                        }
                    }
                }
        }
        if (withFloor) {
            for (x in 0 until 1000) {
                d[maxY + 2][x] = '#'
            }
        }

        fun drop(x: Int, y: Int): Boolean {
            return when {
                y > 190 -> false
                d[y + 1][x] == '.' -> drop(x, y + 1)
                d[y + 1][x - 1] == '.' -> drop(x - 1, y + 1)
                d[y + 1][x + 1] == '.' -> drop(x + 1, y + 1)
                else -> {
                    d[y][x] = '+'
                    true
                }
            }
        }

        var count = 0
        while (d[0][500] != '+' && drop(500, 0)) {
            count++
        }
        return count
    }

    fun part1(input: List<String>) = solve(input, false)

    fun part2(input: List<String>): Int = solve(input, true)

    val testInput = readInputLines("Day14_test")
    check(part1(testInput), 24)
    check(part2(testInput), 93)

    val input = readInputLines("Day14")
    println(part1(input))
    println(part2(input))
}
