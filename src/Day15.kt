import kotlin.math.abs

fun main() {
    data class Point(val x: Int, val y: Int) {
        fun dist(other: Point) = abs(x - other.x) + abs(y - other.y)

        fun add(dx: Int, dy: Int) = Point(x + dx, y + dy)
    }

    class Sensor(val pos: Point, val closestBeacon: Point)

    fun parse(input: List<String>): List<Sensor> = input.map { line ->
        val (sensorPos, beaconPos) = line.split(":").map { desc ->
            val (x, y) = desc.substringAfter("at ").split(", ").map {
                it.substringAfter("=").toInt()
            }
            Point(x, y)
        }
        Sensor(sensorPos, beaconPos)
    }

    fun part1(input: List<String>, targetY: Int): Int {
        val sensors = parse(input)
        val minX = sensors.minOf { it.pos.x - it.pos.dist(it.closestBeacon) }
        val maxX = sensors.maxOf { it.pos.x + it.pos.dist(it.closestBeacon) }

        return (minX..maxX).count { x ->
            val point = Point(x, targetY)
            val isCloser = sensors.any { it.pos.dist(point) <= it.pos.dist(it.closestBeacon) }
            val noExisting = sensors.all { it.closestBeacon != point }
            isCloser && noExisting
        }
    }

    fun part2(input: List<String>, limit: Int): Long {
        val sensors = parse(input)

        fun check(p: Point) = p.x in 0..limit &&
                p.y in 0..limit &&
                sensors.all { it.pos.dist(p) > it.pos.dist(it.closestBeacon) }

        val result = mutableSetOf<Point>()
        fun walk(start: Point, dx: Int, dy: Int, size: Int) {
            var cur = start
            repeat(size) {
                if (check(cur)) {
                    result += cur
                }
                cur = cur.add(dx, dy)
            }
        }

        for (sensor in sensors) {
            val pos = sensor.pos
            val dist = pos.dist(sensor.closestBeacon)
            walk(pos.add(dist + 1, 0), -1, -1, dist + 1)
            walk(pos.add(dist + 1, 0), -1, 1, dist + 1)
            walk(pos.add(-(dist + 1), 0), 1, -1, dist + 1)
            walk(pos.add(-(dist + 1), 0), 1, 1, dist + 1)
        }
        if (result.size != 1) throw IllegalStateException(result.toString())
        val p = result.first()
        return p.x.toLong() * 4000000L + p.y.toLong()
    }

    val testInput = readInputLines("Day15_test")
    check(part1(testInput, 10), 26)
    check(part2(testInput, 20), 56000011L)

    val input = readInputLines("Day15")
    println(part1(input, 2000000))
    println(part2(input, 4000000))
}
