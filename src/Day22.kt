fun main() {
    val up = Vec2(-1, 0)
    val right = Vec2(0, 1)
    val down = Vec2(1, 0)
    val left = Vec2(0, -1)
    val dirs = listOf(right, down, left, up)

    data class Input(val table: List<String>, val commands: List<String>)

    fun parseInput(input: String): Input {
        val (tableStr, cmds) = input.split("\n\n")
        val table = tableStr.lines().toMutableList()
        val maxLength = table.maxOf { it.length }
        for (i in table.indices) {
            table[i] = table[i] + " ".repeat(maxLength - table[i].length)
        }
        val commands = Regex("([0-9]+)([^[0-9]])?+")
            .findAll(cmds)
            .flatMap { match ->
                match.groupValues.drop(1).filter { it.isNotEmpty() }
            }
            .toList()
        return Input(table, commands)
    }

    fun score(i: Int, j: Int, dir: Vec2) =  1000 * (i + 1) + 4 * (j + 1) + dirs.indexOf(dir)

    fun part1(input: String): Int {
        val (table, commands) = parseInput(input)
        var i = 0
        var j = table[0].indexOfFirst { it == '.' }
        var dir = right

        fun go(steps: Int) {
            repeat(steps) {
                if (dir.x != 0) {
                    var nextI = i + dir.x
                    if (nextI !in table.indices || table[nextI][j] == ' ') {
                        nextI = if (dir.x == 1) {
                            table.indexOfFirst { it[j] != ' ' }
                        } else {
                            table.indexOfLast { it[j] != ' ' }
                        }
                    }
                    if (table[nextI][j] != '#') {
                        i = nextI
                    } else return
                } else {
                    var nextJ = j + dir.y
                    val row = table[i]
                    if (nextJ !in row.indices || row[nextJ] == ' ') {
                        nextJ = if (dir.y == 1) row.indexOfFirst { it != ' ' } else row.indexOfLast { it != ' ' }
                    }
                    if (row[nextJ] != '#') {
                        j = nextJ
                    } else return
                }
            }
        }

        for (command in commands) {
            when (command) {
                "L" -> dir = dirs[(dirs.indexOf(dir) - 1).mod(4)]
                "R" -> dir = dirs[(dirs.indexOf(dir) + 1).mod(4)]
                else -> go(command.toInt())
            }
        }
        return score(i, j, dir)
    }

    data class Vec3(val x: Int, val y: Int, val z: Int) {
        operator fun unaryMinus(): Vec3 = Vec3(-x, -y, -z)

        override fun toString() = "($x, $y, $z)"
    }

    class Side(val normal: Vec3) {
        lateinit var texturePos: Vec2
        lateinit var textureDown: Vec3

        fun isNotWrapped() = !this::texturePos.isInitialized
    }

    fun start(table: List<String>): Vec2 {
        for (i in table.indices) {
            for (j in table[0].indices) {
                if (table[i][j] != ' ') return Vec2(i, j)
            }
        }
        throw IllegalStateException()
    }

    fun rotateVector(vec: Vec3, sin: Int, normal: Vec3): Vec3 {
        val (x, y, z) = normal
        val m0 = intArrayOf(x * x, x * y - z * sin, z + y * sin)
        val m1 = intArrayOf(x * y + z * sin, y * y , y * z - x * sin)
        val m2 = intArrayOf(x * z - y * sin, y * z + x * sin, z * z)
        return Vec3(
            m0[0] * vec.x + m0[1] * vec.y + m0[2] * vec.z,
            m1[0] * vec.x + m1[1] * vec.y + m1[2] * vec.z,
            m2[0] * vec.x + m2[1] * vec.y + m2[2] * vec.z
        )
    }

    fun part2(input: String, sideSize: Int): Int {
        val (table, commands) = parseInput(input)

        val sides = listOf(
            Side(Vec3(0, 0, 1)),
            Side(Vec3(0, 0, -1)),
            Side(Vec3(0, 1, 0)),
            Side(Vec3(0, -1, 0)),
            Side(Vec3(1, 0, 0)),
            Side(Vec3(-1, 0, 0)),
        )

        fun wrap(side: Side, pos: Vec2, down: Vec3) {
            println("Wrap: side normal = ${side.normal}, pos = $pos, down = $down")
            side.texturePos = pos
            side.textureDown = down

            for ((flatDir, rotation) in listOf(
                Vec2(0, 1) to 1,
                Vec2(0, -1) to -1,
                Vec2(1, 0) to 0
            )) {
                val newPos = pos + flatDir * sideSize
                if (newPos.x in table.indices && newPos.y in table[0].indices && table[newPos.x][newPos.y] != ' ') {
                    val sideDir = if (rotation != 0) rotateVector(down, rotation, side.normal) else down
                    val newSide = sides.first { it.normal == sideDir }
                    if (newSide.isNotWrapped()) {
                        var newDown = -side.normal
                        if (rotation != 0) newDown = rotateVector(newDown, -rotation, newSide.normal)
                        wrap(newSide, newPos, newDown)
                    }
                }
            }
        }
        wrap(sides[0], start(table), Vec3(0, -1, 0))

        var side = sides[0]
        var pos = side.texturePos
        var dir = right
        var dir3d = rotateVector(side.textureDown, 1, side.normal)

        fun Side.findTextureDir(dir: Vec3): Vec2 {
            var res = down
            var current = textureDown
            repeat(5) {
                if (current == dir) {
                    return res
                }
                res = dirs[(dirs.indexOf(res) + 1).mod(4)]
                current = rotateVector(current, -1, normal)
            }
            throw IllegalStateException()
        }

        fun positionOnSide(): Int {
            val relX = pos.x - side.texturePos.x
            val relY = pos.y - side.texturePos.y
            return when (dir) {
                left -> sideSize - 1 - relX
                right -> relX
                up -> relY
                down -> sideSize - 1 - relY
                else -> throw IllegalStateException()
            }
        }
        fun fromPositionOnSide(linePos: Int, dir: Vec2): Vec2 {
            return when (dir) {
                left -> Vec2(linePos, sideSize - 1)
                right -> Vec2(sideSize - 1 - linePos, 0)
                up -> Vec2(sideSize - 1, sideSize - 1 - linePos)
                down -> Vec2(0, linePos)
                else -> throw IllegalStateException()
            }
        }

        fun go(steps: Int) {
            repeat(steps) { step ->
                var nextPos = pos + dir
                var nextSide = side
                var nextDir = dir
                var nextDir3d = dir3d

                val startX = side.texturePos.x
                val startY = side.texturePos.y
                if (nextPos.x !in startX until startX + sideSize || nextPos.y !in startY until startY + sideSize) {
                    nextSide = sides.first { it.normal == dir3d }
                    nextDir3d = -side.normal
                    nextDir = nextSide.findTextureDir(nextDir3d)

                    val linePos = sideSize - 1 - positionOnSide()
//                    println("Switch: dir = $dir, nextDir = $nextDir, linePos = $linePos")
                    nextPos = fromPositionOnSide(linePos, nextDir)
                    nextPos += nextSide.texturePos
                }

                if (table[nextPos.x][nextPos.y] != '#') {
                    side = nextSide
                    pos = nextPos
                    dir = nextDir
                    dir3d = nextDir3d
                } else return
//                println("After step $step: x = ${pos.x}, y = ${pos.y}, side = ${side.normal}, dir = $dir, dir3d = $dir3d")
            }
        }
        for (command in commands) {
            when (command) {
                "L" -> {
                    dir = dirs[(dirs.indexOf(dir) - 1).mod(4)]
                    dir3d = rotateVector(dir3d, 1, side.normal)
                }
                "R" -> {
                    dir = dirs[(dirs.indexOf(dir) + 1).mod(4)]
                    dir3d = rotateVector(dir3d, -1, side.normal)
                }
                else -> go(command.toInt())
            }
//            println("After command $command: x = ${pos.x}, y = ${pos.y}, side = ${side.normal}, dir = $dir, dir3d = $dir3d")
        }
        return score(pos.x, pos.y, dir)
    }

    val testInput = readInput("Day22_test")
    check(part1(testInput), 6032)
    check(part2(testInput, 4), 5031)

    val input = readInput("Day22")
    println(part1(input))
    println(part2(input, 50))
}