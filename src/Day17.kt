fun main() {
    data class Point(var x: Int, var y: Int)

    class Rock(val lines: List<String>) {
        val width = lines[0].length
        val height = lines.size

        fun canPush(field: Array<CharArray>, pos: Point, moveX: Int) = !willIntersect(field, pos, moveX, 0)

        fun canFall(field: Array<CharArray>, pos: Point) = !willIntersect(field, pos, 0, -1)

        fun willIntersect(field: Array<CharArray>, pos: Point, dx: Int, dy: Int): Boolean {
            val newX = pos.x + dx
            val newY = pos.y + dy
            if (newX < 0 || newX + width > 7) return true
            for (y in lines.indices) {
                for (x in 0 until width) {
                    if (lines[height - y - 1][x] == '#' && field[x + newX][y + newY] == '#') {
                        return true
                    }
                }
            }
            return false
        }

        fun append(top: Array<CharArray>, pos: Point) {
            for (y in lines.indices) {
                for (x in 0 until width) {
                    if (lines[height - y - 1][x] == '#') {
                        top[x + pos.x][y + pos.y] = '#'
                    }
                }
            }
        }
    }

    fun solve(patterns: String, count: Long): Long {
        val rocks = readInput("Day17_rocks")
            .split("\n\n")
            .map { Rock(it.lines()) }

        val field = Array(7) { CharArray(5000) { if (it == 0) '#' else '.' } }
        var rock = 0
        var pattern = 0
        fun drop(rock: Rock) {
            val pos = Point(2, field.maxOf { it.lastIndexOf('#') } + 4)
            while (true) {
                val move = if (patterns[pattern] == '>') 1 else -1
                pattern = (pattern + 1) % patterns.length
                if (rock.canPush(field, pos, move)) {
                    pos.x += move
                }
                if (rock.canFall(field, pos)) {
                    pos.y--
                } else {
                    rock.append(field, pos)
                    return
                }
            }
        }

        class State(val pattern: String, val rocks: Int, val height: Int)
        val prevPattern = HashMap<Pair<Int, Int>, State>()

        val maxHeights = mutableListOf<Int>()
        repeat(2000) { rocksCounter ->
            drop(rocks[rock])
            rock = (rock + 1) % rocks.size

            val maxHeight = field.maxOf { it.lastIndexOf('#') }
            maxHeights += maxHeight
            val curPattern = field.map { it[maxHeight] }.toString()
            val last = prevPattern[rock to pattern]
            if (last != null && last.pattern == curPattern) {
                println("Found pattern = $curPattern")
                println("Height diff = ${maxHeight - last.height}, rocks diff = ${rocksCounter - last.rocks}")

                val heightDiff = maxHeight - last.height
                val cycle = rocksCounter - last.rocks
                val remainsFromLast = count - 1 - last.rocks
                val cycles = remainsFromLast / cycle
                val fromLastCycle = (remainsFromLast % cycle).toInt()
                return cycles * heightDiff + maxHeights[last.rocks + fromLastCycle]
            }
            prevPattern[rock to pattern] = State(curPattern, rocksCounter, maxHeight)
        }
        return -1
    }

    fun part1(patterns: String) = solve(patterns, 2022).toInt()

    fun part2(patterns: String) = solve(patterns, 1000000000000L)

    val testInput = readInput("Day17_test")
    check(part1(testInput), 3068)
    check(part2(testInput), 1514285714288L)

    val input = readInput("Day17")
    println(part1(input))
    println(part2(input))
}
