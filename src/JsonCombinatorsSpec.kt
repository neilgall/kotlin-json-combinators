import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import java.io.StringWriter


class JsonCombinatorsSpec : StringSpec({

    val json = """
{"menu": {
  "id": "file",
  "value": "File",
  "popup": {
    "menuitem": [
      {"value": "New", "onclick": "CreateNewDoc()", "order": 1},
      {"value": "Open", "onclick": "OpenDoc()", "order": 2},
      {"value": "Close", "onclick": "CloseDoc()", "order": 3}
    ]
  }
}}
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
            menu = Menu(
                id = "file",
                value = "File",
                popup = Popup(
                    menuitem = listOf(
                        MenuItem(value = "New", onclick = "CreateNewDoc()", order = 1),
                        MenuItem(value = "Open", onclick = "OpenDoc()", order = 2),
                        MenuItem(value = "Close", onclick = "CloseDoc()", order = 3)
                    )
                )
            )
        )
    }

    "can write example json" {
        val model = modelReader(parse(json))
        val out = write(modelWriter(model))

        out shouldBe json.replace(Regex("\\s"), "")
    }
})