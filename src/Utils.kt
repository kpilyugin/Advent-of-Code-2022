import java.io.File
import kotlin.math.min
import kotlin.math.max

private fun inputFile(name: String) = File("src${File.separator}input", "$name.txt")

fun readInput(name: String) = inputFile(name).readText()

/**
 * Reads lines from the given input txt file.
 */
fun readInputLines(name: String) = inputFile(name).readLines()

fun check(actual: Any, expected: Any) {
    if (expected != actual) {
        throw IllegalStateException("Correct result is $expected, but got $actual")
    }
}

fun rangeBetween(a: Int, b: Int): IntRange = IntRange(min(a, b), max(a, b))

fun String.extractNumbers(): List<Int> = Regex("[0-9]+")
    .findAll(this)
    .map { match -> match.value.toInt() }
    .toList()

data class Vec2(val x: Int, val y: Int) {
    operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)

    operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)

    operator fun times(factor: Int) = Vec2(x * factor, y * factor)

    override fun toString() = "($x, $y)"

    val allNeighbours: List<Vec2> by lazy {
        buildList {
            for (dx in -1..1) {
                for (dy in -1..1) {
                    if (dx != 0 || dy != 0) add(Vec2(x + dx, y + dy))
                }
            }
        }
    }
}
