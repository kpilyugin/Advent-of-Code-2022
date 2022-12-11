fun main() {
    class Monkey(
        val operation: (Long) -> Long,
        val divisibleBy: Int,
        val test: (Long) -> Int) {
        val items = mutableListOf<Long>()
        var inspected = 0L

        fun inspect(monkeys: List<Monkey>, manage: (Long) -> Long = { it / 3 }) {
            for (item in items) {
                inspected++
                var cur = operation(item)
                cur = manage(cur)
                monkeys[test(cur)].items += cur
            }
            items.clear()
        }
    }

    fun String.parseMonkey(): Monkey {
        val (starting, operation, test, testTrue, testFalse) = split("\n").drop(1)

        val ops = operation.substringAfter("= ").split(" ")
        val opFunction = { old: Long ->
            val r = if (ops[2] == "old") old else ops[2].toLong()
            if (ops[1] == "*") old * r else old + r
        }
        val divisibleBy = test.substringAfter("by ").toInt()
        val ifTrue = testTrue.substringAfter("monkey ").toInt()
        val ifFalse = testFalse.substringAfter("monkey ").toInt()
        val testFunction = { value: Long ->
            if (value % divisibleBy == 0L) ifTrue else ifFalse
        }
        return Monkey(opFunction, divisibleBy, testFunction).apply {
            starting.substringAfter(": ").split(", ").forEach {
                items += it.toLong()
            }
        }
    }

    fun List<Monkey>.top2(): Long {
        val top = map { it.inspected }.sortedDescending()
        return top[0] * top[1]
    }

    fun part1(input: String): Long {
        val monkeys = input.split("\n\n").map { it.parseMonkey() }
        repeat(20) {
            for (monkey in monkeys) {
                monkey.inspect(monkeys)
            }
        }
        return monkeys.top2()
    }

    fun part2(input: String): Long {
        val monkeys = input.split("\n\n").map { it.parseMonkey() }
        val product = monkeys.map { it.divisibleBy }.reduce { d1, d2 -> d1 * d2 }

        repeat(10000) {
            for (monkey in monkeys) {
                monkey.inspect(monkeys) { value -> value % product }
            }
        }
        return monkeys.top2()
    }

    val testInput = readInput("Day11_test")
    check(part1(testInput), 10605L)
    check(part2(testInput), 2713310158L)

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}
