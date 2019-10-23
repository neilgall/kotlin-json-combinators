import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.*

class JsonReadError(expected: String, input: JsonNode) : Exception("expected '$expected' in $input")
class JsonWriteError(input: Any) : Exception("cannot serialise ${input.javaClass} as JSON value")

// ----
// reader

typealias JsonReader<T> = JsonNode.() -> T

inline fun <reified N, T> JsonNode.read(key: String, type: String, f: (N) -> T): T =
    when (val n = this[key]) {
        is N -> f(n)
        null -> throw JsonReadError("expected key '$key'", this)
        else -> throw JsonReadError("expected $type at '$key'", this)
    }

inline fun <reified N, T> JsonNode.readOpt(key: String, type: String, f: (N) -> T): T? =
    when (val n = this[key]) {
        is N -> f(n)
        null, is NullNode -> null
        else -> throw JsonReadError("expected $type at '$key'", this)
    }

val stringValue = { n: TextNode -> n.asText() }
val intValue = { n: IntNode -> n.asInt() }
val booleanValue = { n: BooleanNode -> n.asBoolean() }

fun JsonNode.string(key: String): String =
    read(key, "a string", stringValue)

fun JsonNode.optionalString(key: String): String? =
    readOpt(key, "a string", stringValue)

fun JsonNode.int(key: String): Int =
    read(key, "an integer", intValue)

fun JsonNode.optionalInt(key: String): Int? =
    readOpt(key, "an integer", intValue)

fun JsonNode.boolean(key: String): Boolean =
    read(key, "a boolean", booleanValue)

fun JsonNode.optionalBoolean(key: String): Boolean? =
    readOpt(key, "a boolean", booleanValue)

fun <N: JsonNode, T> JsonNode.array(key: String, f: (N) -> T): List<T> =
    read(key, "an array") { n: ArrayNode -> n.map { f(it as N) } }

//fun <T> JsonNode.array(key: String, by: JsonReader<T>): List<T> =
//    read(key, "an array") { n: ArrayNode -> n.map(by) }
//
//fun <T> JsonNode.optionalArray(key: String, by: JsonReader<T>): List<T>? =
//    readOpt(key, "an array") { n: ArrayNode -> n.map(by) }

inline fun <reified T> JsonNode.obj(key: String, by: (JsonNode) -> T): T =
    read(key, "a ${T::class}", by)

inline fun <reified T> JsonNode.optionalObj(key: String, by: (JsonNode) -> T): T? =
    readOpt(key, "a ${T::class}", by)

operator fun <T> JsonReader<T>.invoke(key: String): JsonReader<T> = {
    this[key]?.let(this@invoke) ?: throw JsonReadError("expected key '$key", this)
}


// ----
// writer

typealias JsonWriter<T> = T.() -> JsonNode

fun jsonValue(value: Any?): JsonNode = when (value) {
    null -> NullNode.instance
    is JsonNode -> value
    is String -> TextNode.valueOf(value)
    is Int -> IntNode.valueOf(value)
    is Boolean -> BooleanNode.valueOf(value)
    is Collection<*> -> jsonArray(value)
    else -> throw JsonWriteError(value)
}

fun jsonArray(items: Collection<*>?): JsonNode =
    if (items == null)
        NullNode.instance
    else
        ArrayNode(JsonNodeFactory.instance).apply {
            items.forEach { item -> add(jsonValue(item)) }
        }

fun <T> jsonArray(items: Collection<T>?, writer: JsonWriter<T>): JsonNode =
    if (items == null)
        NullNode.instance
    else
        ArrayNode(JsonNodeFactory.instance).apply {
            items.forEach { item -> add(writer(item)) }
        }

fun jsonObject(vararg fields: Pair<String, Any?>): JsonNode =
    ObjectNode(JsonNodeFactory.instance).apply {
        fields.forEach { (key, value) -> set<JsonNode>(key, jsonValue(value)) }
    }

