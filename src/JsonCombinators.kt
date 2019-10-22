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

inline fun <reified N, T> TreeNode.readOpt(key: String, type: String, f: (N) -> T): T? =
    when (val n = this[key]) {
        is N -> f(n)
        null -> null
        else -> throw JsonError("expected $type at '$key'", this)
    }

fun TreeNode.string(key: String): String =
    read(key, "a string") { n: TextNode -> n.asText() }

fun TreeNode.optionalString(key: String): String? =
    readOpt(key, "a string") { n: TextNode -> n.asText() }

fun TreeNode.int(key: String): Int =
    read(key, "an integer") { n: IntNode -> n.asInt() }

fun TreeNode.optionalInt(key: String): Int? =
    readOpt(key, "an integer") { n: IntNode -> n.asInt() }

fun TreeNode.boolean(key: String): Boolean =
    read(key, "a boolean") { n: BooleanNode -> n.asBoolean() }

fun TreeNode.optionalBoolean(key: String): Boolean? =
    readOpt(key, "a boolean") { n: BooleanNode -> n.asBoolean() }

fun <T> TreeNode.array(key: String, by: JsonReader<T>): List<T> =
    read(key, "an array") { n: ArrayNode -> n.map(by) }

fun <T> TreeNode.optionalArray(key: String, by: JsonReader<T>): List<T>? =
    readOpt(key, "an array") { n: ArrayNode -> n.map(by) }

inline fun <reified T> TreeNode.obj(key: String, by: (TreeNode) -> T): T =
    read(key, "a ${T::class}", by)

inline fun <reified T> TreeNode.optionalObj(key: String, by: (TreeNode) -> T): T? =
    readOpt(key, "a ${T::class}", by)
