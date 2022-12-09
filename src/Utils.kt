import java.io.File

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
