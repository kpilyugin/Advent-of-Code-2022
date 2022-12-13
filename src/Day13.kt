sealed interface Data : Comparable<Data>

class IntData(val value: Int) : Data {
    override fun compareTo(other: Data): Int {
        return if (other is IntData) {
            value.compareTo(other.value)
        } else {
            -other.compareTo(this)
        }
    }

    override fun toString() = value.toString()
}

class ListData(val values: List<Data>) : Data {
    override fun compareTo(other: Data): Int {
        val otherValues = if (other is ListData) other.values else listOf(other)
        for (i in values.indices) {
            if (i >= otherValues.size) {
                return 1
            }
            val compareItems = values[i].compareTo(otherValues[i])
            if (compareItems != 0) {
                return compareItems
            }
        }
        return values.size.compareTo(otherValues.size)
    }

    override fun toString() = values.joinToString(prefix="[", postfix = "]")
}

fun main() {

    fun parse(line: String): Data {
        var position = 0

        fun parseChunk(): Data {
            if (line[position] == '[') {
                val values = mutableListOf<Data>()
                while (true) {
                    position++ // [ or ,
                    if (line[position] != ']') {
                        values += parseChunk()
                    }
                    if (line[position] == ']') {
                        position++
                        return ListData(values)
                    }
                }
            } else {
                val start = position
                while (line[position].isDigit()) position++
                return IntData(line.substring(start, position).toInt())
            }
        }

        return parseChunk()
    }

    fun part1(input: String): Int {
        return input.split("\n\n")
            .map { group ->
                val (left, right) = group.split("\n").map { parse(it) }
                left <= right
            }
            .asSequence()
            .withIndex()
            .filter { it.value }
            .sumOf { it.index + 1 }
    }

    fun part2(input: String): Int {
        val divider1 = parse("[[2]]")
        val divider2 = parse("[[6]]")

        val all = input.lines()
            .filter { it.isNotEmpty() }
            .map { parse(it) }
            .toMutableList()
            .apply {
                add(divider1)
                add(divider2)
                sort()
            }
        return (all.indexOf(divider1) + 1) * (all.indexOf(divider2) + 1)
    }

    val testInput = readInput("Day13_test")
    check(part1(testInput), 13)
    check(part2(testInput), 140)

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}
