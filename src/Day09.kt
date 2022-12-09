import kotlin.math.max
import kotlin.math.abs

data class Point(val x: Int, val y: Int) {
    fun dist(other: Point) = max(abs(x - other.x), abs(y - other.y))
}

fun main() {
    fun Point.move(dir: String): Point {
        return when (dir) {
            "R" -> Point(x + 1, y)
            "L" -> Point(x - 1, y)
            "U" -> Point(x, y + 1)
            "D" -> Point(x, y - 1)
            else -> throw UnsupportedOperationException()
        }
    }

    fun Point.moveToHead(head: Point): Point {
        fun move(from: Int, to: Int) = when {
            from > to -> from - 1
            from < to -> from + 1
            else -> from
        }
        return if (dist(head) > 1) {
            Point(
                move(x, head.x),
                move(y, head.y)
            )
        } else this
    }

    fun solve(input: List<String>, numTails: Int): Int {
        val visited = mutableSetOf<Point>()
        val initial = Point(0, 0)
        val rope = Array(numTails + 1) { initial }
        visited += initial
        input.forEach {
            val (dir, steps) = it.split(" ")
            repeat(steps.toInt()) {
                rope[0] = rope[0].move(dir)
                for (i in 1..numTails) {
                    rope[i] = rope[i].moveToHead(rope[i - 1])
                }
                visited += rope.last()
            }
        }
        return visited.size
    }

    fun part1(input: List<String>) = solve(input, 1)

    fun part2(input: List<String>) = solve(input, 9)

    val testInput = readInputLines("Day09_test")
    check(part1(testInput), 13)
    check(part2(testInput), 1)

    val testInput2 = readInputLines("Day09_test2")
    check(part2(testInput2), 36)

    val input = readInputLines("Day09")
    println(part1(input))
    println(part2(input))
}
