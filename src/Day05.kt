fun main() {
    fun String.extractNumbers(): List<Int> = Regex("[0-9]+")
        .findAll(this)
        .map { match -> match.value.toInt() }
        .toList()

    fun parseState(initial: String): MutableList<String> {
        return initial.lines()
            .dropLast(1)
            .map { line ->
                line.windowed(3, 4)
                    .map { if (it.trim().isEmpty()) "" else it[1].toString() }
            }
            .reduce { current, next ->
                current.zip(next, String::plus)
            }
            .toMutableList()
    }

    fun solve(input: String, move: (state: MutableList<String>, from: Int, count: Int, to: Int) -> Unit): String {
        val (initial, movesString) = input.split("\n\n")
        val state = parseState(initial)

        movesString.lines()
            .map(String::extractNumbers)
            .forEach { move(state, it[0], it[1] - 1, it[2] - 1) }

        return state.map { it[0] }.joinToString(separator = "")
    }

    fun part1(input: String): String = solve(input) { state, count, from, to ->
        val chunk = state[from].take(count).reversed()
        state[from] = state[from].drop(count)
        state[to] = chunk + state[to]
    }

    fun part2(input: String): String = solve(input) { state, count, from, to ->
        val chunk = state[from].take(count)
        state[from] = state[from].drop(count)
        state[to] = chunk + state[to]
    }

    val testInput = readInput("Day05_test")
    check(part1(testInput) == "CMZ")
    check(part2(testInput) == "MCD")

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}
