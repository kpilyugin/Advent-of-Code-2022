import java.lang.IllegalArgumentException

fun main() {
    fun Char.priority(): Int = when (this) {
        in 'A'..'Z' -> this - 'A' + 27
        in 'a'..'z' -> this - 'a' + 1
        else -> throw IllegalArgumentException()
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { bag ->
            val (l, r) = bag.chunked(bag.length / 2).map { it.toSet() }
            val common = l intersect r
            common.sumOf { it.priority() }
        }
    }

    fun part2(input: List<String>): Int {
        return input.chunked(3)
            .sumOf { group ->
                val (a, b, c) = group.map { it.toSet() }
                val common = a intersect b intersect c
                common.sumOf { it.priority() }
            }
    }

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
