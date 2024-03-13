import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File


class Admin(aLogin: String, aPassword: String, aRole: String) : User(aLogin, aPassword, aRole) {
    val json = File("files/menu.json").readText() // грузим меню, т.к. работаем с меню
    val jsonConf = Json {ignoreUnknownKeys = true}
    val menuList: MutableList<Menu> = jsonConf.decodeFromString(json)
    fun add(name: String, quantity: Int, price: Int, time: Int) {
        if (menuList.any { it.nameFood == name }) {
            println("Блюдо с таким названием уже есть, вы не можете добавить его повторно!!!")
        }
        val item: Menu = Menu(name, quantity, price, time)
        menuList.add(item)
        File("files/menu.json").writeText(Json.encodeToString(menuList))
    }

    fun delete(name: String) {
        val item = menuList.find { it.nameFood == name }

        if (item != null) {
            println("Блюдо ${item.nameFood}, в количестве ${item.quantity}, с ценой ${item.price} и временем приготовления ${item.time}, успешно удаленно")
            menuList.remove(item)
            File("files/menu.json").writeText(Json.encodeToString(menuList))

        } else {
            println("Такого блюда нет в списке")

        }
    }

    fun edit(name: String) {
        val item = menuList.find { it.nameFood == name }
        if (item != null) {
            var flag: Boolean = true
            println("Что хотите изменить в блюде: количество, цена, время приготовления")
            while (flag) {
                val inputTast = readLine()?.lowercase()
                when (inputTast) {
                    "количество" -> {
                        print("Введите количество блюд: ")
                        val inputQuantity = readLine()?.toIntOrNull()
                        if (inputQuantity != null) {
                            item.quantity = inputQuantity
                        }
                    }

                    "цена" -> {
                        print("Введите новую цену: ")
                        val inputPrice = readLine()?.toIntOrNull()
                        if (inputPrice != null) {
                            item.price = inputPrice
                        }
                    }

                    "время приготовления" -> {
                        print("Введите новое время приготовления: ")
                        val inputTime = readLine()?.toIntOrNull()
                        if (inputTime != null) {
                            item.price = inputTime
                        }
                    }

                    else -> {
                        println("Введите одну из позиций представленной в списке")
                    }

                }
                flag = false;
            }
            File("files/menu.json").writeText(Json.encodeToString(menuList))
        } else {
            println("Такого блюда нет в списке")
        }
    }


}
