fun main() {
    fun groups(input: List<String>): List<Int> {
        val groups = mutableListOf<Int>()
        var current = 0
        input.forEach {
            if (it.isEmpty()) {
                groups += current
                current = 0
            } else {
                current += it.toInt()
            }
        }
        groups += current
        return groups
    }

    fun part1(input: List<String>) = groups(input).max()

    fun part2(input: List<String>) = groups(input).sortedDescending().take(3).sum()

    // test if implementation meets criteria from the description, like:
    val testInput = readInputLines("Day01_test")
    check(part1(testInput) == 24000)
    check(part2(testInput) == 45000)

    val input = readInputLines("Day01")
    println(part1(input))
    println(part2(input))
}
