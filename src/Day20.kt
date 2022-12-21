fun main() {
    class PLong(val value: Long) {
        override fun toString() = value.toString()
    }

    fun solve(input: List<String>, key: Long, repeatMix: Int): Long {
        val initial = input.map { PLong(it.toLong() * key) }
        val n = initial.size

        val mixed = initial.toMutableList()
        repeat(repeatMix) {
            for (pValue in initial) {
                val idx = (pValue.value + mixed.indexOf(pValue)).mod(n - 1)
                mixed.remove(pValue)
                mixed.add(idx, pValue)
            }
        }

        val zeros = initial.filter { it.value == 0L }
        check(zeros.size == 1)
        val idxZero = mixed.indexOf(zeros[0])
        fun get(idx: Int): Long = mixed[(idx + idxZero) % n].value
        return get(1000) + get(2000) + get(3000)
    }

    fun part1(input: List<String>) = solve(input, key = 1, repeatMix = 1)

    fun part2(input: List<String>) = solve(input, key = 811589153, repeatMix = 10)

    val testInput = readInputLines("Day20_test")
    check(part1(testInput), 3L)
    check(part2(testInput), 1623178306L)

    val input = readInputLines("Day20")
    println(part1(input))
    println(part2(input))
}
