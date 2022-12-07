import java.lang.IllegalStateException

sealed class FsEntity(val name: String) {
    abstract val size: Int
}

class Dir(name: String, val parent: Dir?) : FsEntity(name) {
    val children = mutableListOf<FsEntity>()

    fun findChild(name: String): Dir =
        children.find { it.name == name } as? Dir ?: throw IllegalStateException("Child directory $name not found")

    override val size: Int
        get() = children.sumOf { it.size }
}

class File(name: String, override val size: Int) : FsEntity(name)

fun main() {
    fun walk(root: Dir, input: List<String>) {
        var current = root
        input.forEach {
            val cmd = it.split(" ")
            if (cmd[0] == "$") {
                if (cmd[1] == "cd") {
                    current = when(cmd[2]) {
                        "/" -> root
                        ".." -> current.parent ?: throw IllegalStateException("Cannot exit from root")
                        else -> current.findChild(cmd[2])
                    }
                }
            } else {
                val child = if (cmd[0] == "dir") Dir(cmd[1], current) else File(cmd[1], cmd[0].toInt())
                current.children += child
            }
        }
    }

    fun directorySizes(dir: Dir): Sequence<Int> = sequence {
        yield(dir.size)
        dir.children.filterIsInstance<Dir>().forEach {
            yieldAll(directorySizes(it))
        }
    }

    fun part1(input: List<String>): Int {
        val root = Dir("root", null)
        walk(root, input)
        return directorySizes(root).filter { it < 100000 }.sum()
    }

    fun part2(input: List<String>): Int {
        val root = Dir("root", null)
        walk(root, input)
        val removeAtLeast = root.size - 4e7
        return directorySizes(root).filter { it >= removeAtLeast }.min()
    }

    val testInput = readInputLines("Day07_test")
    check(part1(testInput) == 95437)
    check(part2(testInput) == 24933642)

    val input = readInputLines("Day07")
    println(part1(input))
    println(part2(input))
}
