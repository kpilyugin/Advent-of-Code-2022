fun main() {
    fun Int.digitTo5() = when (this) {
        3 -> "="
        4 -> "-"
        else -> toString()
    }

    fun Char.digitFrom5() = when (this) {
        '-' -> -1
        '=' -> -2
        else -> this.digitToInt()
    }

    fun Long.to5(): String {
        var res = ""
        var cur = this
        while (cur > 0) {
            val digit = cur % 5
            cur = if (digit in 0..2) cur / 5 else cur / 5 + 1
            res += digit.toInt().digitTo5()
        }
        return res.reversed()
    }

    fun String.from5(): Long {
        var res = 0L
        var pow = 1L
        for (ch in reversed()) {
            res += ch.digitFrom5() * pow
            pow *= 5
        }
        return res
    }

    fun part1(input: List<String>) = input.sumOf { it.from5() }.to5()

    val testInput = readInputLines("Day25_test")
    check(part1(testInput), "2=-1=0")

    val input = readInputLines("Day25")
    println(part1(input))
}
