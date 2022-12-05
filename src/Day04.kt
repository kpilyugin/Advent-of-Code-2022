fun main() {
    fun List<Int>.includes(other: List<Int>) = this[0] <= other[0] && this[1] >= other[1]

    fun intersects(a: List<Int>, b: List<Int>) = a[1] >= b[0] && b[1] >= a[0]

    fun countPairs(input: List<String>, predicate: (List<Int>, List<Int>) -> Boolean): Int {
        return input.count { line ->
            val (a, b) = line.split(",")
                .map { it.split("-").map(String::toInt) }
            predicate(a, b)
        }
    }

    fun part1(input: List<String>): Int = countPairs(input) { a, b ->
        a.includes(b) || b.includes(a)
    }

    fun part2(input: List<String>) = countPairs(input, ::intersects)

    val testInput = readInputLines("Day04_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInputLines("Day04")
    println(part1(input))
    println(part2(input))
}
