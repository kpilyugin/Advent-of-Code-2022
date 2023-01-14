fun main() {
    class Direction(var dirs: List<Vec2>)

    class Elf(var pos: Vec2) {
        var nextPos: Vec2? = null
    }

    class Result(val stableRound: Int, val freeCells: Int)

    fun solve(input: List<String>, rounds: Int?): Result {
        val elves = mutableListOf<Elf>()
        for (i in input.indices) {
            for (j in input[i].indices) {
                if (input[i][j] == '#') elves += Elf(Vec2(i, j))
            }
        }
        val directions = mutableListOf(
            Direction(listOf(Vec2(-1, 0), Vec2(-1, -1), Vec2(-1, 1))), // N
            Direction(listOf(Vec2(1, 0), Vec2(1, -1), Vec2(1, 1))), // S
            Direction(listOf(Vec2(0, -1), Vec2(-1, -1), Vec2(1, -1))), // W
            Direction(listOf(Vec2(0, 1), Vec2(-1, 1), Vec2(1, 1))), // E
        )
        repeat(rounds ?: Int.MAX_VALUE) { currentRound ->
            val elvesPositions = elves.map { it.pos }.toSet()
            val proposed = mutableMapOf<Vec2, Int>()
            fun Elf.propose() {
                if (pos.allNeighbours.all { it !in elvesPositions }) return

                for (mainDir in directions) {
                    if (mainDir.dirs.all { it + pos !in elvesPositions }) {
                        nextPos = pos + mainDir.dirs[0]
                        proposed.compute(nextPos!!) { _, value -> (value ?: 0) + 1 }
                        return
                    }
                }
            }
            fun Elf.move() {
                nextPos?.let {
                    if (proposed[it] == 1) {
                        pos = it
                    }
                }
                nextPos = null
            }

            elves.forEach { it.propose() }

            if (elves.all { it.nextPos == null }) {
                return Result(currentRound + 1, -1)
            }

            elves.forEach { it.move() }
            directions.add(directions.removeAt(0))
        }
        val xRange = elves.maxOf { it.pos.x } - elves.minOf { it.pos.x }
        val yRange = elves.maxOf { it.pos.y } - elves.minOf { it.pos.y }
        return Result(-1, (yRange + 1) * (xRange + 1) - elves.size)
    }

    fun part1(input: List<String>) = solve(input, 10).freeCells

    fun part2(input: List<String>) = solve(input, null).stableRound

    val testInput = readInputLines("Day23_test")
    check(part1(testInput), 110)
    check(part2(testInput), 20)

    val input = readInputLines("Day23")
    println(part1(input))
    println(part2(input))
}
