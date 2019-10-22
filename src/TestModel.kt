
data class MenuItem(val value: String, val onclick: String, val order: Int)

data class Popup(val menuitem: List<MenuItem>)

data class Menu(val id: String, val value: String, val popup: Popup)

data class Model(val menu: Menu)

val menuItemReader: JsonReader<MenuItem> = {
    MenuItem(
        value = string("value"),
        onclick = string("onclick"),
        order = int("order")
    )
}

val menuItemWriter: JsonWriter<MenuItem> = {
    jsonObject(
        "value" to value,
        "onclick" to onclick,
        "order" to order
    )
}

val popupReader: JsonReader<Popup> = {
    Popup(
        menuitem = array("menuitem", menuItemReader)
    )
}

val popupWriter: JsonWriter<Popup> = {
    jsonObject(
        "menuitem" to jsonArray(menuitem, menuItemWriter)
    )
}

val menuReader: JsonReader<Menu> = {
    Menu(
        id = string("id"),
        value = string("value"),
        popup = obj("popup", popupReader)
    )
}

val menuWriter: JsonWriter<Menu> = {
    jsonObject(
        "id" to id,
        "value" to value,
        "popup" to popupWriter(popup)
    )
}

val modelReader: JsonReader<Model> = {
    Model(
        menu = obj("menu", by = menuReader)
    )
}

val modelWriter: JsonWriter<Model> = {
    jsonObject(
        "menu" to menuWriter(menu)
    )
}
