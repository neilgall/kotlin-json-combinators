import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.specs.StringSpec

data class MenuItem(val value: String, val onclick: String)

data class Popup(val menuitem: List<MenuItem>)

data class Menu(val id: String, val value: String, val popup: Popup)

data class Model(val menu: Menu)

val menuItemReader: JsonReader<MenuItem> = {
    MenuItem(
        value = string("value"),
        onclick = string("onclick")
    )
}

val popupReader: JsonReader<Popup> = {
    Popup(
        menuitem = array("menuitem", menuItemReader)
    )
}

val menuReader: JsonReader<Menu> = {
    Menu(
        id = string("id"),
        value = string("value"),
        popup = obj("popup", popupReader)
    )
}

val modelReader: JsonReader<Model> = {
    Model(
        menu = obj("menu", by = menuReader)
    )
}

class JsonCombinatorsSpec : StringSpec({

    "can parse example json" {
        val json = """
{"menu": {
  "id": "file",
  "value": "File",
  "popup": {
    "menuitem": [
      {"value": "New", "onclick": "CreateNewDoc()"},
      {"value": "Open", "onclick": "OpenDoc()"},
      {"value": "Close", "onclick": "CloseDoc()"}
    ]
  }
}}
"""
        val parser = JsonFactory(ObjectMapper()).createParser(json)
        val treeNode = parser.readValueAsTree<TreeNode>()
        treeNode shouldNotBe null

        val model = modelReader(treeNode)
        model shouldBe Model(
            menu = Menu(
                id = "file",
                value = "File",
                popup = Popup(
                    menuitem = listOf(
                        MenuItem(value = "New", onclick = "CreateNewDoc()"),
                        MenuItem(value = "Open", onclick = "OpenDoc()"),
                        MenuItem(value = "Close", onclick = "CloseDoc()")
                    )
                )
            )
        )
    }
})