class Monkeys(val input: List<String>) {
    val monkeys = mutableMapOf<String, Monkey>()
    var ownId: String = ""

    init {
        input.forEach { line ->
            val (id, info) = line.split(": ")
            val monkey = when {
                " " in info -> {
                    val (m1, op, m2) = info.split(" ")
                    CompoundMonkey(id, m1, op, m2)
                }

                else -> SimpleMonkey(id, info.toLong())
            }
            monkeys[id] = monkey
        }
    }

    abstract class Monkey {
        abstract val id: String

        abstract fun yell(): Long

        abstract fun match(targetValue: Long?)

        abstract fun dependsOnOwn(): Boolean
    }

    inner class SimpleMonkey(override val id: String, val initialValue: Long) : Monkey() {
        var matchedValue: Long = -1

        override fun yell() = initialValue

        override fun dependsOnOwn() = id == ownId

        override fun match(targetValue: Long?) {
            if (id == ownId) {
                matchedValue = targetValue!!
            } else {
                throw IllegalStateException("Not own monkey")
            }
        }
    }

    inner class CompoundMonkey(override val id: String, val m1: String, val op: String, val m2: String) : Monkey() {
        private val monkey1: Monkey by lazy { monkeys[m1]!! }
        private val monkey2: Monkey by lazy { monkeys[m2]!! }

        val opFunction: (Long, Long) -> Long = when (op) {
            "+" -> Long::plus
            "-" -> Long::minus
            "*" -> Long::times
            "/" -> Long::div
            else -> throw IllegalStateException("Unsupported operation $op")
        }

        override fun yell() = opFunction(monkey1.yell(), monkey2.yell())

        override fun dependsOnOwn() = monkey1.dependsOnOwn() || monkey2.dependsOnOwn()

        override fun match(targetValue: Long?) {
            val depends1 = monkey1.dependsOnOwn()
            val depends2 = monkey2.dependsOnOwn()
            if (depends2) {
                val value1 = monkey1.yell()
                if (targetValue == null) {
                    monkey2.match(value1)
                } else {
                    val value2 = when(op) {
                        "+" -> targetValue - value1
                        "-" -> value1 - targetValue
                        "*" -> targetValue / value1
                        "/" -> value1 / targetValue
                        else -> throw IllegalStateException("Unsupported operation $op")
                    }
                    monkey2.match(value2)
                }
            } else if (depends1) {
                val value2 = monkey2.yell()
                if (targetValue == null) {
                    monkey1.match(value2)
                } else {
                    val value1 = when(op) {
                        "+" -> targetValue - value2
                        "-" -> targetValue + value2
                        "*" -> targetValue / value2
                        "/" -> targetValue * value2
                        else -> throw IllegalStateException("Unsupported operation $op")
                    }
                    monkey1.match(value1)
                }
            }
        }
    }

    fun yellRoot() = monkeys["root"]!!.yell()

    fun matchRootWithOwn(): Long {
        monkeys["root"]!!.match(null)
        return (monkeys[ownId]!! as SimpleMonkey).matchedValue
    }
}

fun main() {
    fun part1(input: List<String>) = Monkeys(input).yellRoot()

    fun part2(input: List<String>) = Monkeys(input).apply { ownId = "humn" }.matchRootWithOwn()

    val testInput = readInputLines("Day21_test")
    check(part1(testInput), 152L)
    check(part2(testInput), 301L)

    val input = readInputLines("Day21")
    println(part1(input))
    println(part2(input))
}
