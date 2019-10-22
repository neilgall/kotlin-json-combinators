import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.node.*

class JsonError(expected: String, input: TreeNode) : Exception("expected '$expected' in $input")

typealias JsonReader<T> = TreeNode.() -> T

inline fun <reified N, T> TreeNode.read(key: String, type: String, f: (N) -> T): T =
    when (val n = this[key]) {
        is N -> f(n)
        null -> throw JsonError("expected key '$key'", this)
        else -> throw JsonError("expected $type at '$key'", this)
    }

fun TreeNode.string(key: String): String =
    read(key, "a string") { n: TextNode -> n.asText() }

fun TreeNode.int(key: String): Int =
    read(key, "an integer") { n: IntNode -> n.asInt() }

fun TreeNode.boolean(key: String): Boolean =
    read(key, "a boolean") { n: BooleanNode -> n.asBoolean() }

fun <T> TreeNode.array(key: String, r: JsonReader<T>): List<T> =
    read(key, "an array") { n: ArrayNode -> n.map(r) }

inline fun <reified T> TreeNode.obj(key: String, f: (TreeNode) -> T): T =
    read(key, "a ${T::class}", f)

