fun main() {
    fun indexAfterDistinct(input: String, count: Int): Int {
        return input.windowed(count, 1)
            .withIndex()
            .first { it.value.toSet().size == count }
            .index + count
    }

    fun part1(input: String) = indexAfterDistinct(input, 4)

    fun part2(input: String) = indexAfterDistinct(input, 14)

    val testInput = readInput("Day06_test")
    check(part1(testInput) == 7)
    check(part2(testInput) == 19)

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}
