import kotlin.math.abs

fun main() {
    data class Point(val x: Int, val y: Int, val z: Int) {
        fun touches(other: Point): Boolean {
            return (x == other.x && y == other.y && abs(z - other.z) == 1) ||
                    (x == other.x && z == other.z && abs(y - other.y) == 1) ||
                    (y == other.y && z == other.z && abs(x - other.x) == 1)
        }

        fun moveX(dx: Int) = Point(x + dx, y, z)
        fun moveY(dy: Int) = Point(x, y + dy, z)
        fun moveZ(dz: Int) = Point(x, y, z + dz)
    }

    fun parse(input: List<String>) = input.map { line ->
        val (x, y, z) = line.split(",").map { it.toInt() }
        Point(x, y, z)
    }

    fun surfaceArea(points: List<Point>): Int {
        var area = points.size * 6
        for (i in points.indices) {
            for (j in 0 until i) {
                if (points[i].touches(points[j])) area -= 2
            }
        }
        return area
    }

    fun part1(input: List<String>): Int {
        val points = parse(input)
        return surfaceArea(points)
    }

    fun part2(input: List<String>): Int {
        val points = parse(input)
        val maxX = points.maxOf { it.x }
        val maxY = points.maxOf { it.y }
        val maxZ = points.maxOf { it.z }
        fun Point.isOutside() = x !in 0..maxX || y !in 0..maxY || z !in 0..maxZ

        val pointsSet = points.toHashSet()
        val inside = HashSet<Point>()
        val outside = HashSet<Point>()

        fun walk(start: Point) {
            val q = ArrayDeque<Point>()
            q.addLast(start)
            val visited = HashSet<Point>()
            while (q.isNotEmpty()) {
                val cur = q.removeFirst()
                visited += cur
                for (move in listOf(Point::moveX, Point::moveY, Point::moveZ)) {
                    for (dir in listOf(-1, 1)) {
                        val p = move(cur, dir)
                        if (p !in pointsSet && p !in visited && p !in inside) {
                            if (p.isOutside() || p in outside) {
                                outside += visited
                                return
                            }
                            visited += p
                            q.addLast(p)
                        }
                    }
                }
            }
            inside += visited
        }

        for (i in 0 until maxX) {
            for (j in 0 until maxY) {
                for (k in 0 until maxZ) {
                    val p = Point(i, j, k)
                    if (p !in pointsSet && p !in inside && p !in outside) {
                        walk(p)
                    }
                }
            }
        }
        return surfaceArea(points) - surfaceArea(inside.toList())
    }

    val testInput = readInputLines("Day18_test")
    check(part1(testInput), 64)
    check(part2(testInput), 58)

    val input = readInputLines("Day18")
    println(part1(input))
    println(part2(input))
}
