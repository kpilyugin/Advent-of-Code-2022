fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf {
            val (a, b) = it.split(" ")
            val their = a[0] - 'A'
            val our = b[0] - 'X'
            val score = when ((our - their + 3) % 3) {
                1 -> 6
                2 -> 0
                else -> 3
            }
            our + 1 + score
        }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf {
            val (a, b) = it.split(" ")
            val their = a[0] - 'A'
            val our = when (b[0]) {
                'X' -> (their + 2) % 3
                'Y' -> their
                else /*'Z'*/ -> (their + 1) % 3
            }
            val score = when (b[0]) {
                'Y' -> 3
                'Z' -> 6
                else -> 0
            }
            our + 1 + score
        }
    }

    val testInput = readInputLines("Day02_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 12)

    val input = readInputLines("Day02")
    println(part1(input))
    println(part2(input))
}
