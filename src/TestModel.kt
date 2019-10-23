
data class Widget(
    val type: String,
    val width: Int,
    val height: Int
)

data class Model(
    val name: String,
    val description: String?,
    val count: Int,
    val size: Int?,
    val enabled: Boolean,
    val hidden: Boolean?,
    val tags: List<String>,
    val widgets: List<Widget>
)

val modelReader: JsonReader<Model> = {
    Model(
        name = string("name"),
        description = optionalString("description"),
        count = int("count"),
        size = optionalInt("size"),
        enabled = boolean("enabled"),
        hidden = optionalBoolean("hidden"),
        tags = array("tags", stringValue),
        widgets = array("widgets", widgetReader)
    )
}

val widgetReader: JsonReader<Widget> = {
    Widget(
        type = string("type"),
        width = int("width"),
        height = int("height")
    )
}

val modelWriter: JsonWriter<Model> = {
    jsonObject(
        "name" to name,
        "description" to description,
        "count" to count,
        "size" to size,
        "enabled" to enabled,
        "hidden" to hidden,
        "tags" to jsonArray(tags),
        "widgets" to jsonArray(widgets, widgetWriter)
    )
}

val widgetWriter: JsonWriter<Widget> = {
    jsonObject(
        "type" to type,
        "width" to width,
        "height" to height
    )
}
