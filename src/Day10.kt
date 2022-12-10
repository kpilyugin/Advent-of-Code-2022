import kotlin.math.abs

fun main() {
    fun run(input: List<String>) = buildList {
        var x = 1
        add(x)
        input.forEach {
            if (it == "noop") {
                add(x)
            } else {
                add(x)
                val change = it.split(" ")[1].toInt()
                x += change
                add(x)
            }
        }
    }

    fun part1(input: List<String>): Int {
        val values = run(input)
        var res = 0
        for (i in 20..220 step 40) {
            res += i * values[i - 1]
        }
        return res
    }

    fun part2(input: List<String>) {
        val positions = run(input)
        val cycles = 240
        val line = CharArray(cycles) { '.' }
        for (i in 0 until cycles) {
            if (abs(positions[i] - i % 40) <= 1) {
                line[i] = '#'
            }
        }
        val image = String(line).chunked(40).joinToString(separator = "\n")
        println(image)
    }

    val testInput = readInputLines("Day10_test")
    check(part1(testInput), 13140)
    println(part2(testInput))

    val input = readInputLines("Day10")
    println(part1(input))
    println(part2(input))
}
