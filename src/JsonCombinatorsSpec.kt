import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import java.io.StringWriter


class JsonCombinatorsSpec : StringSpec({

    val json = """
{
    "name": "foo",
    "description": null,
    "count": 42,
    "size": 99,
    "enabled": true,
    "hidden": null,
    "tags": ["abc", "def", "ghi"],
    "widgets": [
        { "type": "frumbler", "width": 16, "height": 22 },
        { "type": "hammerklug", "width": 73, "height": 19 }
    ]
}
"""

    fun parse(input: String): JsonNode {
        val parser = JsonFactory(ObjectMapper()).createParser(input)
        return parser.readValueAsTree()
    }

    fun write(json: JsonNode): String {
        val s = StringWriter()
        val writer = JsonFactory(ObjectMapper()).createGenerator(s)
        writer.writeTree(json)
        return s.toString()
    }

    "can read example json" {
        val model = modelReader(parse(json))
        model shouldBe Model(
            name = "foo",
            description = null,
            count = 42,
            size = 99,
            enabled = true,
            hidden = null,
            tags = listOf("abc", "def", "ghi"),
            widgets = listOf(Widget("frumbler", 16, 22), Widget("hammerklug", 73, 19))
        )
    }

    "can write example json" {
        val model = modelReader(parse(json))
        val out = write(modelWriter(model))

        out shouldBe json.replace(Regex("\\s"), "")
    }
})