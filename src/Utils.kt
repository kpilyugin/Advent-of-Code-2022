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
