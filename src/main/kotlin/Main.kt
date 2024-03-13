@file:Suppress("UNREACHABLE_CODE")

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

fun autentification(person: User) {
    var flag: Boolean = true
    var flag2: Boolean = true


    while (flag2) {
        val json = File("files/users.json").readText()
        val userList: MutableList<User> = Json.decodeFromString(json)
        println("\n\n\nВойти/Регистрация")
        val inputAction = readLine()?.lowercase()
        if (inputAction == "войти") {
            while (flag) {
                println("Login: ")
                val inputLogin = readLine()
                val user = userList.find { it.login == inputLogin }
                if (user != null) {
                    println("Password: ")
                    val inputPassword = readLine()
                    if (user.password == inputPassword) {
                        println("Role(Admin/Visitor): ")
                        val inputRole = readLine()?.lowercase()
                        if (user.role.lowercase() == inputRole) {
                            if (inputRole == "admin") {
                                println("Введите пароль для администратора")
                                val inputPasswordAdmin = readLine()
                                val adminPassword = File("files/passwordAdmin.txt")
                                if (adminPassword.exists()) {
                                    if (inputPasswordAdmin == adminPassword.readText()) {
                                        println("OK")
                                        println("Вы успешно вошли!")
                                    } else {
                                        println("Пароль не верный")
                                    }
                                } else {
                                    println("Такого файла не существует!!!!")
                                }
                            } else {
                                println("Вы успешно вошли!")
                            }
                            person.login = inputLogin.toString()
                            person.role = inputRole
                            person.password = inputPassword
                            flag = false
                            flag2 = false
                        } else {
                            println("Пользователя с такой ролью нет")
                        }
                    } else {
                        println("Пароль не верный")
                    }


                } else {
                    println("Такого пользователя не существует")
                }
            }
        } else if (inputAction == "регистрация") {
            println("Enter Login: ")
            var inputLoginReg = readLine()
            while (inputLoginReg.isNullOrEmpty() || userList.any { it.login == inputLoginReg }) {
                println("Логин не может быть пустым или пользоватлеь с таким логином существует")
                inputLoginReg = readLine()

            }
            println("Enter Password: ")
            var inputPasswordReg = readLine()
            while (inputPasswordReg.isNullOrEmpty()) {
                println("Пароль не может быть пустым")
                inputPasswordReg = readLine()

            }
            println("Enter Role(Admin/Visitor): ")
            var inputRoleReg = readLine()
            while (inputRoleReg.isNullOrEmpty() || !(inputRoleReg.lowercase() == "visitor" || inputRoleReg.lowercase() == "admin")) {
                println("Введите правильно роль")
                inputRoleReg = readLine()

            }
            if (inputRoleReg.lowercase() == "admin") {
                println("Введите пароль для администратора")
                val inputPasswordAdmin = readLine()
                val adminPassword = File("files/passwordAdmin.txt")
                if (adminPassword.exists()) {
                    if (inputPasswordAdmin == adminPassword.readText()) {
                        println("OK")
                    } else {
                        println("Пароль не верный")
                    }
                } else {
                    println("Такого файла не существует!!!!")
                }
            }
            person.login = inputLoginReg.toString()
            person.role = inputRoleReg
            person.password = inputPasswordReg
            userList.add(person)
            File("files/users.json").writeText(Json.encodeToString(userList))
            println("вы успешно зарегистрировались!!!!!")
            flag2 = true
        } else {
            println("Введите запрос корректно")
        }
    }

}


fun enter(user: User): Boolean {
    println("Приветвуем вас в ресторане VELIK\n")
    println(
        "Войдите или зарегистрируйтесь как пользователь или админ.\n" +
                "Role: User/Admin\n" +
                "Если вы выбрали роль админ то введите пароль для регистрации\n" +
                "Login:\n" +
                "Password:"
    )
    autentification(user)
    return false
}

fun start(user: User) {
    var trigger = true
    while (trigger) {
        trigger = enter(user)
    }
}

fun admin(user: User) {
    var userCopy = user
    userCopy = Admin(user.login, user.password, user.role)
    var flag = true
    while (flag) {
        println(
            "Добавить блюдо, Удалить блюдо, Изменить параметры блюда, Посмотреть отзывы, Меню, Выручка ресторана\n" +
                    "Если хотите выйти, напишите ВЫЙТИ"
        )
        val inputAction = readLine()?.lowercase()
        if (userCopy is Admin) {
            val admin = userCopy as Admin
            when (inputAction) {
                "добавить блюдо" -> {
                    println("Введите название блюда, его количество, цену и сложность выполнения(время испольнения): ")
                    val inputName = readLine()
                    val inputQuantity = readLine()?.toIntOrNull()
                    val inputPrice = readLine()?.toIntOrNull()
                    val inputTime = readLine()?.toIntOrNull()
                    if (inputName == null) {
                        println("Не может быть пустого имени")
                    } else if (inputQuantity == null || inputQuantity < 0) {
                        println("Кол-во не может быть отрицательным или не быть числом")
                    } else if (inputPrice == null || inputPrice < 0) {
                        println("Цена не может быть отрицательной или не быть числом")
                    } else if (inputTime == null || inputTime < 0) {
                        println("Время не может быть отрицательным или не быть числом")
                    } else {
                        admin.add(inputName, inputQuantity, inputPrice, inputTime)
                    }
                }

                "удалить блюдо" -> {
                    println("Введите имя блюда которое хотите удалить: ")
                    val inputName = readLine()
                    if (inputName == null) {
                        println("Не может быть пустого имени")
                    } else {
                        admin.delete(inputName)
                    }
                }

                "изменить параметры блюда" -> {
                    println("Напишите название блюда которое хотите редактировать")
                    var inputName = readLine()
                    if (inputName == null) {
                        println("Не может быть пустого названия")
                    } else {
                        admin.edit(inputName)
                    }
                }

                "выйти" -> {
                    flag = false
                }

                "посмотреть отзывы" -> {
                    val jsonConf = Json { ignoreUnknownKeys = true }
                    val json2 = File("files/reviews.json").readText()
                    var reviewsList: MutableList<Review> = jsonConf.decodeFromString(json2)
                    reviewsList.forEach {
                        println("Блюдо ${it.nameFood}, Оценка ${it.grade}, Комментарий ${it.comment}")
                    }
                }

                "меню" -> {
                    val jsonConf = Json { ignoreUnknownKeys = true }
                    val json2 = File("files/menu.json").readText()
                    var menuList: MutableList<Menu> = jsonConf.decodeFromString(json2)
                    menuList.forEach {
                        println("Блюдо ${it.nameFood}, Кол-во ${it.quantity}, Цена ${it.price}, Время приготовления ${it.time}")
                    }
                }

                "выручка ресторана" -> {
                    val json = File("files/totalmoney.json").readText()
                    val jsonConf = Json { ignoreUnknownKeys = true }
                    val total: Total = jsonConf.decodeFromString(json)
                    println(total.money)
                }

                else -> {
                    println("Введите корректую команду")
                }
            }
        }
    }
}

fun printList(list: MutableList<Menu>) {
    list.forEach {
        println("Название: ${it.nameFood}, Количество: ${it.quantity}, Цена: ${it.price}, Время приготовления: ${it.time}")
    }
}

fun visitor(user: User) {
    do {
        val userCopy = user
        val json = File("files/menu.json").readText() // грузим меню, т.к. работаем с меню
        val jsonConf = Json { ignoreUnknownKeys = true }
        val menuList: MutableList<Menu> = jsonConf.decodeFromString(json)
        val orderList: MutableList<Menu> = mutableListOf()

        val json2 = File("files/reviews.json").readText()
        var reviewsList: MutableList<Review> = jsonConf.decodeFromString(json2)

        val menuLock = Any()
        val cancelOrderFlag = AtomicBoolean(false)

        println("Наше меню")


        printList(menuList)


        var inputOrder = ""
        while (inputOrder != "стоп" && !cancelOrderFlag.get()) {

            inputOrder = readLine().toString()

            synchronized(menuLock) {
                val item = menuList.find { it.nameFood == inputOrder }
                if (item != null) {
                    if (item.quantity-- <= 0) {
                        println("Увы, товар закончился")
                    } else {
                        orderList.add(item)
                    }
                    println("если хотите остановится, введите: 'стоп' ")
                } else {
                    if ((inputOrder == "стоп")) {
                        println("Начинаем готовить заказ")
                    } else {
                        println("Такого блюда нет в списке")
                    }

                }
            }
        }

        if (!cancelOrderFlag.get()) {
            // Создаем флаг для отслеживания завершения выполнения потока
            val flag = AtomicBoolean(false)

            // Запускаем поток для приготовления заказа
            val thread = thread {
                synchronized(menuLock) {
                    flag.set(userCopy.cookFood(menuList, orderList, cancelOrderFlag))
                }
            }

            // Ожидаем завершения выполнения потока
            thread.join()
            var total = File("files/money_order.txt").readText().toInt()
            // Проверяем флаг для определения, нужно ли продолжать
            if (flag.get()) {

                println("Заказ успешно выполнен. Хотите сделать еще один?(да/нет)")
                var flag3 = true
                while (flag3) {
                    val inputAnswer = readlnOrNull().toString().lowercase()
                    when (inputAnswer) {
                        "да" -> {
                            cancelOrderFlag.set(false)
                            File("files/menu.json").writeText(Json.encodeToString(menuList))
                            total += orderList.sumOf { it.price }
                            File("files/money_order.txt").writeText(total.toString())
                            orderList.clear()
                            flag3 = false
                        }

                        "нет" -> {
                            File("files/menu.json").writeText(Json.encodeToString(menuList))
                            cancelOrderFlag.set(true)
                            total += orderList.sumOf { it.price }
                            println("Ваш счёт: ${total}")
                            val json = File("files/totalmoney.json").readText()
                            if (File("files/totalmoney.json").exists()) {
                                val jsonConf = Json { ignoreUnknownKeys = true }
                                var total1: Total = jsonConf.decodeFromString(json)
                                total1.money += total
                                File("files/totalmoney.json").writeText(Json.encodeToString(total1))
                                total = 0
                                if (File("files/money_order.txt").exists()) {
                                    File("files/money_order.txt").writeText(total.toString())
                                } else {
                                    println("нет такого файла")
                                }
                                println("Пользователь оплачивает заказ, подтвердить?(да/нет)")
                                var flag4 = true
                                while (flag4) {
                                    val inputAnsw = readLine()?.lowercase()
                                    when (inputAnsw) {

                                        "нет" -> println("Нельзя оставлять заказ не оплаченным")
                                        "да" -> {
                                            println("Заказ оплачен")
                                            flag4 = false
                                        }

                                        else -> println("введите корректную команду")
                                    }
                                }

                                review(orderList, reviewsList)
                                flag3 = false
                            } else {
                                println("нет такого файла")
                            }
                        }

                        else -> {
                            println("такого ответа нет")
                        }
                    }
                }

            } else {
                println("Заказ был отменен")
                cancelOrderFlag.set(false)
                orderList.clear()
            }

        }

    } while (!cancelOrderFlag.get())

}

fun review(orderList: MutableList<Menu>, reviewsList: MutableList<Review>) {
    println("Оставьте отзыв о блюдах")
    var flag1 = true

    while (flag1 == true) {
        println("Напишите название блюда или стоп, если не хотите оставлять отзыв")
        val inputName = readLine()
        if (inputName == "стоп") {
            flag1 = false
        } else if (inputName != null && orderList.any { it.nameFood == inputName }) {
            println("Оцените блюдо от 1 до 5")
            val inputGrade = readLine()?.toIntOrNull()
            if (inputGrade == null) {
                println("оценка должна быть числом от 1 до 5")
            } else if (inputGrade !in 1..5) {
                println("Оценка должна быть от 1 до 5")
            } else {
                println("Напишите комментарий к блюду")
                val inputComment = readLine()
                var review = Review(inputName, inputGrade, inputComment.toString())
                reviewsList.add(review)
                File("files/reviews.json").writeText(Json.encodeToString(reviewsList))
                println("Спасибо, приходите к нам еще!")
            }
        } else {
            println("Блюда нет в листе заказа")
        }
    }
}

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    var user: User = User()

    start(user)
    if (user.role == "admin") {
        admin(user)
    }

    if (user.role == "visitor") {
        visitor(user)
    }

}

