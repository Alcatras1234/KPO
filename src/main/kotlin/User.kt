import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlin.concurrent.thread
import org.jline.reader.LineReaderBuilder
import java.util.concurrent.atomic.AtomicBoolean

@Serializable
open class User(var login: String = "", var password: String = "", var role: String = "") {
    fun cookFood(menuList : MutableList<Menu>, orderList: MutableList<Menu>, cancelFlag: AtomicBoolean): Boolean {
        println("Заказ принят")

        var time = orderList.sumOf{it.time}
        var timePlus = 0 // это надо чтобы дополнить время засыпания


        val orderThread = thread {
            // Имитируем приготовление заказа
            println("Приготовление заказа началось")
            Thread.sleep(time.toLong())

        }


        while (!cancelFlag.get()) {
            println("Хотите добавить блюдо к заказу? (Введите название блюда, 'отмена' для отмены заказа, или 'нет' для завершения)")
            val userInput = readLine() ?: ""
            when (userInput) {
                "нет" -> break // Если пользователь решил больше не добавлять блюда, выходим из цикла
                "отмена" -> {
                    cancelFlag.set(true)
                    return false // Заканчиваем выполнение метода, если заказ отменен
                }
                else -> {
                    // Ищем блюдо в меню
                    val menu = menuList.find { it.nameFood == userInput }
                    if (menu != null) {
                        timePlus += menu.time
                        menu.quantity--
                        orderList.add(menu)
                        println("$userInput добавлен в заказ.")
                    }
                    else {
                            println("Такого блюда нет в меню.")

                    }
                }
            }
        }

        // Динамически корректируем время ожидания перед завершением выполнения заказа
        Thread.sleep(timePlus.toLong())

        // Дожидаемся завершения потока обработки заказа
        orderThread.join()

        // Возвращаем true, если заказ успешно выполнен

        return true
    }
}